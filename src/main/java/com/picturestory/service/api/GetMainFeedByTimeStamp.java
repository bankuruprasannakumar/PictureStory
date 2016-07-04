package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
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
import java.util.List;

/**
 * Created by krish on 04/07/2016.
 */

@Path("/getMainFeedByTimeStamp")
@Produces("application/json")
@Consumes("application/json")
public class GetMainFeedByTimeStamp {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IContentCategoryDao mContentCategoryDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;

    @Inject
    public GetMainFeedByTimeStamp(IUserDetailsDao userDetailsDao,IContentDetailsDao contentDetailsDao,
                       IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                       ICategoryDetailsDao mCategoryDetailsDao, IContentCategoryDao mContentCategoryDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
        this.mContentCategoryDao = mContentCategoryDao;
    }

    @POST
    public Response getContentDetailList(GetMainFeedRequest getMainFeedReguest) {
        try {
            if (getMainFeedReguest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getMainFeedReguest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getMainFeedReguest.errorMessage());
            }
            int userId = getMainFeedReguest.getUserId();
            long registeredTimeStamp = getMainFeedReguest.getTime();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            User user =(User)mUserDetailsDao.getUser(userId);


            List<Content> contentList = mContentDetailsDao.getAllContentDetailsTillSetId(timeStampTosetId(registeredTimeStamp));

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
                    List<Integer> categoryIdList = mContentCategoryDao.getCategoryIdListFromContentId(content.getContentId());
                    List<String> categoryNameList = mCategoryDetailsDao.getCategoryNameList(categoryIdList);
                    contentJSON.put(Constants.CATEGORY_NAME_LIST,categoryNameList);

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

    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }

    private long timeStampTosetId(long timeStamp){
        return (long)((System.currentTimeMillis()-timeStamp)/(1000*60*60*24));
    }

}
