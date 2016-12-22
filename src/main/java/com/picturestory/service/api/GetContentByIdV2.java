package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetContentByIdRequest;
import com.picturestory.service.request.GetContentByIdV2Request;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 22/12/16.
 */
@Path("/v2/getContentById")
@Produces("application/json")
@Consumes("application/json")

public class GetContentByIdV2 {
    private final IContentDetailsDao mContentDetailsDao;
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;

    @Inject
    public GetContentByIdV2(IContentDetailsDao mContentDetailsDao, IUserDetailsDao userDetailsDao,
                            IContentUserLikeDao contentUserDao, IUserUserDao userUserDao) {
        this.mContentDetailsDao = mContentDetailsDao;
        mUserDetailsDao = userDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
    }

    @POST
    public Response getContentById(GetContentByIdV2Request getContentByIdRequest) {
        try {
            if (getContentByIdRequest == null)
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            if (!getContentByIdRequest.isValid())
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getContentByIdRequest.errorMessage());
            int userId = getContentByIdRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = getContentByIdRequest.getContentId();
            Content content = (Content) mContentDetailsDao.getContentDetails(contentId);
            if (content != null) {
                JSONObject responseObj = composeResponse(content, userId);
                return ResponseBuilder.successResponse(responseObj.toString());
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentDetailsDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal server error");
        }
    }

    private JSONObject composeResponse(Content content, int userId) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);

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
            //Add story creator Details
            if (content.getStoryUserId() != 0) {
                System.out.println(content.getStoryUserId());
                JSONObject storyUserJSON = new JSONObject();
                User storyUser = (User) mUserDetailsDao.getUser(content.getStoryUserId());
                if (storyUser != null) {
                    storyUserJSON.put(Constants.ID, user.getUserId());
                    storyUserJSON.put(Constants.NAME, user.getUserName());
                    storyUserJSON.put(Constants.DESCRIPTION, user.getUserDesc());
                    storyUserJSON.put(Constants.IMAGE_URL, user.getUserImage());
                    storyUserJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, user.getUserId()));
                    contentJSON.put(Constants.STORY_PERSON_DETAILS, storyUserJSON);
                }
            }

            response.put(Constants.CONTENT, contentJSON);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }

}
