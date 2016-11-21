package com.picturestory.service.api;

import com.google.gson.Gson;
import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.*;
import com.picturestory.service.request.GetMainFeedRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/getPersonalizedMainFeed")
@Produces("application/json")
@Consumes("application/json")
public class GetPersonalizedMainFeed {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IContentCategoryDao mContentCategoryDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;
    private final ICommentUserLikeDao mCommentUserLikeDao;
    private final IContentUserCommentDao mContentUserCommentDao;

    @Inject
    public GetPersonalizedMainFeed(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                                   IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                                   ICategoryDetailsDao mCategoryDetailsDao, IContentCategoryDao mContentCategoryDao,
                                   ICommentUserLikeDao mCommentUserLikeDao, IContentUserCommentDao contentUserCommentDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
        this.mContentCategoryDao = mContentCategoryDao;
        this.mCommentUserLikeDao = mCommentUserLikeDao;
        mContentUserCommentDao = contentUserCommentDao;
    }

    @POST
    public Response getPersonalizedMainFeed(GetMainFeedRequest getMainFeedReguest) {
        try {
            if (getMainFeedReguest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getMainFeedReguest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getMainFeedReguest.errorMessage());
            }
            int userId = getMainFeedReguest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            User user =(User)mUserDetailsDao.getUser(userId);
            long registeredTimeStamp = user.getRegisteredTime();
            List<Content> contentList;
            contentList = mContentDetailsDao.getAllPersonalizedContentDetailsTillSetId(GetSetId.getSetIdForFeed(registeredTimeStamp), user.getUserInterests());

            return ResponseBuilder.successResponse(composeResponse(userId,contentList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }



    private String composeResponse(int userId,List<Content> contentList) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            response.put(Constants.FULLCOUNT,contentList.size());
            JSONArray contentJSONArray = new JSONArray();
            if (null != contentList) {
                for (int index = 0; index < contentList.size(); index++) {
                    Content content = contentList.get(index);
                    JSONObject contentJSON = new JSONObject();
                    contentJSON.put(Constants.ID, content.getContentId());
                    contentJSON.put(Constants.NAME, content.getName());
                    contentJSON.put(Constants.PICTURE_URL,content.getPictureUrl());
                    contentJSON.put(Constants.PLACE,content.getPlace());
                    contentJSON.put(Constants.DATE,content.getDate());
                    contentJSON.put(Constants.PICTURE_DESCRIPTION,content.getPictureDescription());
                    contentJSON.put(Constants.PICTURE_SUMMARY,content.getPictureSummary());
                    contentJSON.put(Constants.EDITORS_PICK,content.isEditorsPick());

                    //set if liked by user

                    ContentUserLikeAssociation contentUserAssociation = new ContentUserLikeAssociation();
                    contentUserAssociation.setContentId(content.getContentId());
                    contentUserAssociation.setLikeduserId(userId);
                    contentJSON.put(Constants.LIKED_BY_USER, mContentUserLikeDao.isContentLikedByUser(contentUserAssociation));
                    contentJSON.put(Constants.LIKE_COUNT, mContentUserLikeDao.fullCountOfUserLikesForContentId(content.getContentId()));

                    //Add content creator details
                    JSONObject contentCreatorJSON = new JSONObject();
                    User user = (User) mUserDetailsDao.getUser(content.getUserId());
                    if (user != null) {
                        contentCreatorJSON.put(Constants.ID, user.getUserId());
                        contentCreatorJSON.put(Constants.NAME, user.getUserName());
                        contentCreatorJSON.put(Constants.DESCRIPTION,user.getUserDesc());
                        contentCreatorJSON.put(Constants.IMAGE_URL,user.getUserImage());
                        contentCreatorJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, user.getUserId()));
                        contentJSON.put(Constants.PERSON_DETAILS, contentCreatorJSON);
                    }

                    //Add category name list
                    JSONArray categoryJSONArray = new JSONArray();
                    List<Integer> categoryIdList = mContentCategoryDao.getCategoryIdListFromContentId(content.getContentId());
                    for(int i=0;i<categoryIdList.size();i++){
                        JSONObject categoryObject = new JSONObject();
                        categoryObject.put(Constants.CATEGORY_ID,categoryIdList.get(i));
                        categoryObject.put(Constants.CATEGORY_NAME,mCategoryDetailsDao.getCategoryName(categoryIdList.get(i)));
                        categoryJSONArray.put(categoryObject);
                    }
                    contentJSON.put(Constants.CATEGORY_NAME_LIST,categoryJSONArray);

                    //Add comment Details
                    List<ContentUserCommentAssociation> contentUserCommentAssociations = mContentUserCommentDao.getCommentsForContentIdWithIndex(content.getContentId(), 0, 3);
                    JSONArray contentUserCommentList = composeCommentListResponse(userId, contentUserCommentAssociations);
                    contentJSON.put(Constants.COMMENT_LIST, contentUserCommentList);
                    contentJSON.put(Constants.COMMENT_COUNT, mContentUserCommentDao.fullCountOfUserCommentsForContentId(content.getContentId()));
                    contentJSONArray.put(contentJSON);
                }
            }
            response.put(Constants.CONTENT_LIST, contentJSONArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response.toString();
    }

    private JSONArray composeCommentListResponse(int userId,List<ContentUserCommentAssociation> commentList) {
        try {
            JSONArray contentUserCommentList = new JSONArray();
            if (null != commentList) {
                for(int index = 0; index < commentList.size(); index++) {
                    ContentUserCommentAssociation contentUserCommentAssociation = commentList.get(index);
                    Gson gson = new Gson();
                    JSONObject commentJSON = new JSONObject(gson.toJson(contentUserCommentAssociation));
                    JSONObject contentCreatorJSON = new JSONObject();
                    User user = (User) mUserDetailsDao.getUser(contentUserCommentAssociation.getUserId());
                    if (user != null) {
                        contentCreatorJSON.put(Constants.NAME, user.getUserName());
                        contentCreatorJSON.put(Constants.IMAGE_URL, user.getUserImage());
                        contentCreatorJSON.put(Constants.DESCRIPTION, user.getUserDesc());
                        commentJSON.put(Constants.PERSON_DETAILS, contentCreatorJSON);
                    }

                    //set comment like count
                    commentJSON.put(Constants.LIKE_COUNT,mCommentUserLikeDao.fullCountOfUserLikesForCommentId(contentUserCommentAssociation.getCommentId()));

                    //set if liked by user
                    CommentUserLikeAssociation commentUserLikeAssociation = new CommentUserLikeAssociation();
                    commentUserLikeAssociation.setCommentId(contentUserCommentAssociation.getCommentId());
                    commentUserLikeAssociation.setCommentLikedUserId(userId);
                    commentJSON.put(Constants.LIKED_BY_USER,mCommentUserLikeDao.isCommentLikedByUser(commentUserLikeAssociation));

                    //set comment liked user list
                    List<User> userList = new ArrayList<User>();
                    userList = mCommentUserLikeDao.usersWhoLikedCommentId(contentUserCommentAssociation.getCommentId());
                    JSONArray userJSONArray = new JSONArray();
                    if(userList!=null)
                        for(User user1:userList){
                            JSONObject userJSON = new JSONObject();
                            userJSON.put(Constants.ID, user1.getUserId());
                            userJSON.put(Constants.NAME, user1.getUserName());
                            userJSON.put(Constants.DESCRIPTION,user1.getUserDesc());
                            userJSON.put(Constants.IMAGE_URL,user1.getUserImage());
                            userJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, user1.getUserId()));
                            userJSONArray.put(userJSON);
                        }
                    commentJSON.put(Constants.LIKED_USER_LIST,userJSONArray);

                    contentUserCommentList.put(commentJSON);
                }
            }
            return contentUserCommentList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }

}