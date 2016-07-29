package com.picturestory.service.api;

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
 * Created by krish on 28/07/2016.
 */
@Path("/getUserDetails")
@Produces("application/json")
@Consumes("application/json")

public class GetUserDetails {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;
    private final IContentCategoryDao mContentCategoryDao;
    private final IStoryDetailsDao mStoryDetailsDao;
    private final IStoryUserLikeDao mStoryUserLikeDao;

    @Inject
    public GetUserDetails(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                            IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                            ICategoryDetailsDao mCategoryDetailsDao, IContentCategoryDao mContentCategoryDao,
                          IStoryDetailsDao mStoryDetailsDao,IStoryUserLikeDao mStoryUserLikeDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
        this.mContentCategoryDao = mContentCategoryDao;
        this.mStoryDetailsDao = mStoryDetailsDao;
        this.mStoryUserLikeDao = mStoryUserLikeDao;
    }

    @POST
    public Response getPersonDetails(GetPersonDetailsRequest getPersonDetailRequest) {
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
            List<Content> likeContentList = new ArrayList<Content>();
            List<Content> contributedContentList = new ArrayList<Content>();
            if (personDetails.isContributor()) {
                if (userId == personId)
                    contributedContentList = mContentDetailsDao.getAllContentDetailsContributedByUserId(personId);
                else
                    contributedContentList = mContentDetailsDao.getAllContentDetailsContributedByUserIdTillSetId(personId, GetSetId.getSetIdForFeed(registeredTimeStamp));
                return ResponseBuilder.successResponse(composeResponse(userId,personDetails, contributedContentList));
            } else {
                List<Integer> likedIdList = new ArrayList<Integer>();
                List<Integer> contributedIdList = new ArrayList<Integer>();
                likedIdList = mStoryDetailsDao.getContentIdListForStoriesLikedByUser(userId);
                if(likedIdList!=null)
                    likeContentList = mContentDetailsDao.getAllContentDetailsForIds(likedIdList);
                contributedIdList = mStoryDetailsDao.getContentIdListForStoriesContributedByUser(userId);
                if(null!=contributedIdList)
                    contributedContentList = mContentDetailsDao.getAllContentDetailsForIds(contributedIdList);

                return ResponseBuilder.successResponse(composeContentResponse(userId,personDetails,likeContentList,contributedContentList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private String composeResponse(int userId,User personDetails,List<Content> contentList) {
        org.json.JSONObject response = new org.json.JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            response.put(Constants.FULLCOUNT,contentList.size());
            JSONArray contentJSONArray = new JSONArray();
            if (null != contentList) {
                for (int index = 0; index < contentList.size(); index++) {
                    Content content = contentList.get(index);
                    org.json.JSONObject contentJSON = new org.json.JSONObject();
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

                    contentJSONArray.put(contentJSON);
                }
            }
            response.put(Constants.CONTENT_LIST, contentJSONArray);
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

    private String composeContentResponse(int userId,User personDetails,List<Content> likeList,List<Content> contributedList) {
        org.json.JSONObject response = new org.json.JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            response.put(Constants.FULLCOUNT,likeList.size()+contributedList.size());
            JSONArray likedContentJSONArray = new JSONArray();
            JSONArray contributedContentJSONArray = new JSONArray();
            if (null != likeList) {
                for (int index = 0; index < likeList.size(); index++) {
                    Content content = likeList.get(index);
                    org.json.JSONObject contentJSON = new org.json.JSONObject();
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
                    List<Story> storyList = mStoryDetailsDao.getAllStoriesLikedByUserForContent(content.getContentId(),userId);
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

                    likedContentJSONArray.put(contentJSON);
                }
            }
            response.put(Constants.LIKED_CONTENT_LIST, likedContentJSONArray);

            if (null != contributedList) {
                for (int index = 0; index < contributedList.size(); index++) {
                    Content content = contributedList.get(index);
                    org.json.JSONObject contentJSON = new org.json.JSONObject();
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
                    List<Story> storyList = mStoryDetailsDao.getAllStoriesContributedByUserForContent(content.getContentId(),userId);
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

                    contributedContentJSONArray.put(contentJSON);
                }
            }
            response.put(Constants.CONTRIBUTED_CONTENT_LIST, contributedContentJSONArray);

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


    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }

}
