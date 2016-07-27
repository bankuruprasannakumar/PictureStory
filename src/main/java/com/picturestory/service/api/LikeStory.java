package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentUserLikeDao;
import com.picturestory.service.database.dao.IStoryUserLikeDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.StoryUserLikeAssocation;
import com.picturestory.service.request.LikeContentRequest;
import com.picturestory.service.request.LikeStoryRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 30/4/16.
 */
@Path("/likeStory")
@Produces("application/json")
@Consumes("application/json")

public class LikeStory {
    private final IUserDetailsDao mUserDetailsDao;
    private final IStoryUserLikeDao mStoryUserLikeDao;

    @Inject
    public LikeStory(IUserDetailsDao userDetailsDao, IStoryUserLikeDao mStoryUserLikeDao){
        mUserDetailsDao = userDetailsDao;
        this.mStoryUserLikeDao = mStoryUserLikeDao;
    }
    @POST
    public Response likeStory(LikeStoryRequest likeStoryRequest) {
        try {
            if (likeStoryRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!likeStoryRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, likeStoryRequest.errorMessage());
            }
            int userId = likeStoryRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            StoryUserLikeAssocation storyUserLikeAssocation = new StoryUserLikeAssocation();
            storyUserLikeAssocation.setStoryLikedUserId(userId);
            storyUserLikeAssocation.setStoryId(likeStoryRequest.getStoryId());
            boolean status = false;
            if(likeStoryRequest.getDoLikeValue())
                status = mStoryUserLikeDao.addStoryUserLike(storyUserLikeAssocation);
            else
                status = mStoryUserLikeDao.deleteStoryUserLike(storyUserLikeAssocation);
            if (status) {
                return ResponseBuilder.successResponse();
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mStoryUserLikeDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}
