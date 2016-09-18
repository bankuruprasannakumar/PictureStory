package com.picturestory.service.api;

import com.google.gson.Gson;
import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.*;
import com.picturestory.service.request.GetPersonDetailsRequest;
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

/**
 * Created by bankuru on 26/5/16.
 */
@Path("/getAllPersonDetails")
@Produces("application/json")
@Consumes("application/json")

public class GetAllPersonDetails {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;
    private final IContentCategoryDao mContentCategoryDao;
    private final IPostcardDetailsDao mPostcardDetailsDao;
    private final ICommentUserLikeDao mCommentUserLikeDao;
    private final IContentUserCommentDao mContentUserCommentDao;

    @Inject
    public GetAllPersonDetails(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                               IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                               ICategoryDetailsDao mCategoryDetailsDao, IContentCategoryDao mContentCategoryDao,
                               IPostcardDetailsDao postcardDetailsDao, ICommentUserLikeDao mCommentUserLikeDao,
                               IContentUserCommentDao contentUserCommentDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
        this.mContentCategoryDao = mContentCategoryDao;
        mPostcardDetailsDao = postcardDetailsDao;
        this.mCommentUserLikeDao = mCommentUserLikeDao;
        mContentUserCommentDao = contentUserCommentDao;

    }

    @POST
    public Response getAllPersonDetails(GetPersonDetailsRequest getPersonDetailRequest) {
        try {
            if (getPersonDetailRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getPersonDetailRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getPersonDetailRequest.errorMessage());
            }
            int userId = getPersonDetailRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int personId = getPersonDetailRequest.getPersonId();
            if (null == mUserDetailsDao.getUser(personId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_PERSON_ID);
            }
            User user =(User)mUserDetailsDao.getUser(userId);
            long registeredTimeStamp = user.getRegisteredTime();
            User personDetails = (User) mUserDetailsDao.getUser(personId);

            //get all content for the user
            List<Content> userLikedContentList = mContentDetailsDao.getAllContentCommentedAndLikedByUser(personId);
            List<Content> userContributedContentList = new ArrayList<Content>();
            if (personDetails.isContributor()) {
                 userContributedContentList = mContentDetailsDao.getAllContentDetailsContributedByUserIdTillSetId(personId, GetSetId.getSetIdForFeed(registeredTimeStamp));
            }
            List<Postcard> userCreatedPostCardList = mPostcardDetailsDao.getAllPostCardsOfUser(personId);


            return ResponseBuilder.successResponse(composeResponse(userId, personDetails, userLikedContentList, userContributedContentList, userCreatedPostCardList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private String composeResponse(int userId,User personDetails,List<Content> userLikedContentList, List<Content> userContributedContentList, List<Postcard> userCreatedPostCardList) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            JSONArray userLikedContentJSONArray = composeContentResponse(userId, userLikedContentList);
            response.put(Constants.LIKED_CONTENT_LIST, userLikedContentJSONArray);

            //TODO : adding dummy data for now

            if (personDetails.isContributor()) {
                JSONArray userContributedContentJSONArray = composeContentResponse(userId, userContributedContentList);
                response.put(Constants.CONTRIBUTED_CONTENT_LIST, userContributedContentJSONArray);
            }
            else {
                response.put(Constants.CONTRIBUTED_CONTENT_LIST, new JSONArray());
            }

            //TODO : addding dummy data for postcard

            JSONArray postCardJSONArray = new JSONArray();
            if (postCardJSONArray != null && postCardJSONArray.length() != 0) {
                for (int i = 0; i < userCreatedPostCardList.size() ; i++) {
                    postCardJSONArray.put(i, userCreatedPostCardList.get(i).getPictureUrl());
                }
            }
            response.put(Constants.MY_POSTCARDS, postCardJSONArray);

            JSONObject personDetailsJSONObject = new JSONObject();
            personDetailsJSONObject.put(Constants.ID, personDetails.getUserId());
            personDetailsJSONObject.put(Constants.NAME, personDetails.getUserName());
            personDetailsJSONObject.put(Constants.DESCRIPTION,personDetails.getUserDesc());
            personDetailsJSONObject.put(Constants.IMAGE_URL,personDetails.getUserImage());
            personDetailsJSONObject.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, personDetails.getUserId()));
            response.put(Constants.USER_DETAILS,personDetailsJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response.toString();
    }


    private JSONArray composeContentResponse(int userId, List<Content> contentList) {
        JSONArray contentJSONArray = new JSONArray();
        try {
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
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
    return contentJSONArray;
    }

    private JSONArray composeCommentListResponse(int userId,List<ContentUserCommentAssociation> commentList) {
        try {
            JSONArray contentUserCommentList = new JSONArray();
            if (null != commentList) {
                for(int index = 0; index < commentList.size(); index++) {
                    ContentUserCommentAssociation contentUserCommentAssociation = commentList.get(index);
                    Gson gson = new Gson();
                    JSONObject contentJSON = new JSONObject(gson.toJson(contentUserCommentAssociation));
                    JSONObject contentCreatorJSON = new JSONObject();
                    User user = (User) mUserDetailsDao.getUser(contentUserCommentAssociation.getUserId());
                    if (user != null) {
                        contentCreatorJSON.put(Constants.NAME, user.getUserName());
                        contentCreatorJSON.put(Constants.IMAGE_URL, user.getUserImage());
                        contentCreatorJSON.put(Constants.DESCRIPTION, user.getUserDesc());
                        contentJSON.put(Constants.PERSON_DETAILS, contentCreatorJSON);
                    }

                    //set comment like count
                    contentJSON.put(Constants.LIKE_COUNT,mCommentUserLikeDao.fullCountOfUserLikesForCommentId(contentUserCommentAssociation.getCommentId()));

                    //set if liked by user
                    CommentUserLikeAssociation commentUserLikeAssociation = new CommentUserLikeAssociation();
                    commentUserLikeAssociation.setCommentId(contentUserCommentAssociation.getCommentId());
                    commentUserLikeAssociation.setCommentLikedUserId(userId);
                    contentJSON.put(Constants.LIKED_BY_USER,mCommentUserLikeDao.isCommentLikedByUser(commentUserLikeAssociation));

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
                    contentJSON.put(Constants.LIKED_USER_LIST,userJSONArray);

                    contentUserCommentList.put(contentJSON);
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
