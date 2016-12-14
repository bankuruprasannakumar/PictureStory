package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetPersonDetailsRequest;
import com.picturestory.service.response.ResponseBuilder;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
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
 * Created by bankuru on 26/5/16.
 */
@Path("/getPersonDetails")
@Produces("application/json")
@Consumes("application/json")

public class GetPersonDetails {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;
    private final IContentCategoryDao mContentCategoryDao;

    @Inject
    public GetPersonDetails(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
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
            List<Content> contentList;
            if (personDetails.isContributor()) {
                if (userId == personId)
                    contentList = mContentDetailsDao.getAllContentDetailsContributedByUserId(personId);
                else
                    contentList = mContentDetailsDao.getAllContentDetailsContributedByUserIdTillSetId(personId, GetSetId.getSetIdForFeed(registeredTimeStamp));
            } else {
                contentList = mContentDetailsDao.getAllContentCommentedAndLikedByUser(personId);
            }

            return ResponseBuilder.successResponse(composeResponse(userId,personDetails, contentList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private String composeResponse(int userId,User personDetails,List<Content> contentList) {
        org.json.JSONObject response = new org.json.JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
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
            response.put(Constants.LIKED_CONTENT_LIST, contentJSONArray);

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
