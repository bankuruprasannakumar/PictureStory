package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.*;
import com.picturestory.service.request.GetMainFeedRequest;
import com.picturestory.service.request.GetMainFeedStoriesRequest;
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

@Path("/getMainFeedBatch")
@Produces("application/json")
@Consumes("application/json")
public class GetMainFeedBatch {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IStoryDetailsDao mStoryDetailsDao;
    private final IStoryUserLikeDao mStoryUserLikeDao;

    @Inject
    public GetMainFeedBatch(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                            IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                            IStoryDetailsDao mStoryDetailsDao, IStoryUserLikeDao mStoryUserLikeDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mStoryDetailsDao = mStoryDetailsDao;
        this.mStoryUserLikeDao = mStoryUserLikeDao;
    }

    @POST
    public Response getContentDetailList(GetMainFeedStoriesRequest getMainFeedStoriesRequest) {
        try {
            if (getMainFeedStoriesRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getMainFeedStoriesRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getMainFeedStoriesRequest.errorMessage());
            }
            int userId = getMainFeedStoriesRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            User user =(User)mUserDetailsDao.getUser(userId);
            long registeredTimeStamp = user.getRegisteredTime();
            int startIndex = getMainFeedStoriesRequest.getStartIndex();
            int numRows=getMainFeedStoriesRequest.getNumRows();

            List<Content> contentList;
            if(userId==1)
                contentList = mContentDetailsDao.getAllContentDetails();
            else if (registeredTimeStamp == 0)
                contentList = mContentDetailsDao.getAllContentDetailsTillSetId(0);
            else
                contentList = mContentDetailsDao.getAllContentTillSetIdWithRange(GetSetId.getSetIdForFeed(registeredTimeStamp),startIndex,numRows);

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

                    //Add storyList
                    JSONArray storyJSONArray = new JSONArray();
                    List<Story> storyList = mStoryDetailsDao.getStoriesForContentWithRange(content.getContentId(),0,3);
                    for(Story story:storyList){
                        JSONObject storyObject = new JSONObject();
                        storyObject.put(Constants.STORY_ID,story.getStoryId());
                        storyObject.put(Constants.STORY_DESC,story.getStoryDesc());
                        storyObject.put(Constants.CONTENT_ID,story.getContentId());

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
                    contentJSON.put(Constants.STORY_LIST,storyJSONArray);
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

}
