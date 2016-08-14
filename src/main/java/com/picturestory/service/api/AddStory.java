package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IStoryDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.Story;
import com.picturestory.service.pojo.StoryUserLikeAssocation;
import com.picturestory.service.pojo.User;
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

import static com.picturestory.service.Constants.STORY_DETAILS;

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
        User user = (User)mUserDetailsDao.getUser(userId);
        if(null == user)
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_USER_ID);
        int contentId = addNewStoryRequest.getContentId();
        if(null == mContentDtailsDao.getContentDetails(contentId))
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_CONTENT_ID);
        if (userId != 1){
            boolean isStoryAlreadyPresent = mContentDtailsDao.isStoryPresentForContentByUser(userId, contentId);
            if (isStoryAlreadyPresent){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_USER_STORY, Constants.INVALID_USER_STORY_CONTENT);
            }
        }
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

            JSONObject storyObject = new JSONObject();
            storyObject.put(Constants.STORY_ID, storyId);
            storyObject.put(Constants.STORY_DESC,story.getStoryDesc());

            //Add story contributor details
            JSONObject storyCreatorJSON = new JSONObject();
            storyCreatorJSON.put(Constants.ID, user.getUserId());
            storyCreatorJSON.put(Constants.NAME, user.getUserName());
            storyCreatorJSON.put(Constants.DESCRIPTION, user.getUserDesc());
            storyCreatorJSON.put(Constants.IMAGE_URL, user.getUserImage());
            storyCreatorJSON.put(Constants.FOLLOWED_BY_USER, false);
            storyObject.put(Constants.AUTHOR_DETAILS, storyCreatorJSON);

            //add story like count and is story liked by user
            storyObject.put(Constants.STORY_LIKE_COUNT, 0);
            storyObject.put(Constants.STORY_LIKED_BY_USER, false);

            //add isPhotographer's piece
            storyObject.put(Constants.IS_PHOTOGRAPHERS_PIECE,false);

            response.put(Constants.STORY_DETAILS, storyObject);
            return ResponseBuilder.successResponse(response.toString());
        }catch (JSONException j){
            j.printStackTrace();
            return null;
        }
    }

}
