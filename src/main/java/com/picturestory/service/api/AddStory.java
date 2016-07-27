package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IStoryDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.Story;
import com.picturestory.service.request.AddNewStoryRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by krish on 26/07/2016.
 */
@Path("/addNewStory")
@Produces("application/json")
@Consumes("application/json")
public class AddStory {
    private final IStoryDetailsDao mStoryDetailsDao;
    private final IContentDetailsDao mContentDtailsDao;
    private final IUserDetailsDao mUserDetailsDao;
    @Inject
    public AddStory(IStoryDetailsDao mStoryDetailsDao, IContentDetailsDao mContentDtailsDao, IUserDetailsDao mUserDetailsDao){
        this.mStoryDetailsDao = mStoryDetailsDao;
        this.mContentDtailsDao = mContentDtailsDao;
        this.mUserDetailsDao = mUserDetailsDao;
    }

    @POST
    public Response addNewStory(AddNewStoryRequest addNewStoryRequest){
        if(addNewStoryRequest==null)
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_REQUEST);
        if(!addNewStoryRequest.isValid())
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,addNewStoryRequest.errorMessage());
        int userId = addNewStoryRequest.getUserId();
        if(null == mUserDetailsDao.getUser(userId))
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_USER_ID);
        int contentId = addNewStoryRequest.getContentId();
        if(null == mContentDtailsDao.getContentDetails(contentId))
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_CONTENT_ID);
        String storyDesc = addNewStoryRequest.getStoryDescription();
        Story story = new Story();
        story.setContentId(contentId);
        story.setUserId(userId);
        story.setStoryDesc(storyDesc);
        int storyId = mStoryDetailsDao.addStory(story);
        if (storyId==0)
            return ResponseBuilder.error(mStoryDetailsDao.getDetailedResponse().getErrorCode(),mStoryDetailsDao.getDetailedResponse().getErrorMessage());
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS,true);
            response.put(Constants.STORY_ID,storyId);
            return ResponseBuilder.successResponse(response.toString());
        }catch (JSONException j){
            j.printStackTrace();
            return null;
        }
    }

}
