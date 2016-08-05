package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.*;
import com.picturestory.service.request.GetStoriesForContentRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by krish on 27/07/2016.
 */
@Path("/getStoriesForContent")
@Produces("application/json")
@Consumes("application/json")
public class GetStoriesForContent {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IUserUserDao mUserUserDao;
    private final IStoryDetailsDao mStoryDetailsDao;
    private final IStoryUserLikeDao mStoryUserLikeDao;

    @Inject
    public GetStoriesForContent(IUserDetailsDao mUserDetailsDao, IContentDetailsDao mContentDetailsDao,
                                IUserUserDao mUserUserDao,IStoryDetailsDao mStoryDetailsDao,
                                IStoryUserLikeDao mStoryUserLikeDao){
        this.mContentDetailsDao = mContentDetailsDao;
        this.mStoryDetailsDao = mStoryDetailsDao;
        this.mStoryUserLikeDao = mStoryUserLikeDao;
        this.mUserDetailsDao = mUserDetailsDao;
        this.mUserUserDao = mUserUserDao;
    }

    @POST
    public Response getStoriesForContent(GetStoriesForContentRequest getStoriesForContentRequest){
       try {
           if(getStoriesForContentRequest==null)
               return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_REQUEST);
           if(!getStoriesForContentRequest.isValid())
               return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,getStoriesForContentRequest.errorMessage());

           int contentId = getStoriesForContentRequest.getContentId();
           Content content = new Content();
           if((content=(Content) mContentDetailsDao.getContentDetails(contentId))==null)
               return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_CONTENT_ID);

           int startIndex = getStoriesForContentRequest.getStartIndex();
           int numRows = getStoriesForContentRequest.getNumRows();
           int userId = getStoriesForContentRequest.getUserId();

           JSONObject response = new JSONObject();
           try {
               //Add storyList
               JSONArray storyJSONArray = new JSONArray();
               response.put(Constants.SUCCESS, true);
               List<Story> storyList = mStoryDetailsDao.getStoriesForContentWithRange(contentId,startIndex,numRows);
               response.put(Constants.FULLCOUNT,storyList.size());
               for(Story story:storyList){
                   JSONObject storyObject = new JSONObject();
                   storyObject.put(Constants.STORY_ID,story.getStoryId());
                   storyObject.put(Constants.STORY_DESC,story.getStoryDesc());

                   //Add story contributor details
                   User storyUser = (User) mUserDetailsDao.getUser(story.getUserId());
                   JSONObject storyCreatorJSON = new JSONObject();
                   if (storyUser != null) {
                       storyCreatorJSON.put(Constants.ID, storyUser.getUserId());
                       storyCreatorJSON.put(Constants.NAME, storyUser.getUserName());
                       storyCreatorJSON.put(Constants.DESCRIPTION,storyUser.getUserDesc());
                       storyCreatorJSON.put(Constants.IMAGE_URL,storyUser.getUserImage());
                       storyCreatorJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, storyUser.getUserId()));
                       storyObject.put(Constants.PERSON_DETAILS, storyCreatorJSON);
                   }

                   //add story like count and is story liked by user
                   storyObject.put(Constants.STORY_LIKE_COUNT,mStoryUserLikeDao.fullCountOfUserLikesForStoryId(story.getStoryId()));
                   StoryUserLikeAssocation storyUserLikeAssocation = new StoryUserLikeAssocation();
                   storyUserLikeAssocation.setStoryId(story.getStoryId());
                   storyUserLikeAssocation.setStoryLikedUserId(userId);
                   storyObject.put(Constants.STORY_LIKED_BY_USER,mStoryUserLikeDao.isStoryLikedByUser(storyUserLikeAssocation));

                   //add isPhotographer's piece
                   boolean flag = false;
                   if(content.getUserId() == story.getUserId())
                       flag = true;
                   storyObject.put(Constants.IS_PHOTOGRAPHERS_PIECE,flag);

                   storyJSONArray.put(storyObject);
               }
               response.put(Constants.STORY_LIST,storyJSONArray);
           }catch (JSONException j){
               j.printStackTrace();
               return ResponseBuilder.error(Constants.ERRORCODE_JSON_EXCEPTION,j.toString());
           }
           return ResponseBuilder.successResponse(response.toString());
       }catch (Exception e){
           e.printStackTrace();
           return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION,"Internal server error");
       }
    }

    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }
}
