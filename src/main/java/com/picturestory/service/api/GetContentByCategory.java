package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetContentByCategoryRequest;
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
 * Created by krish on 04/07/2016.
 */
@Path("/getContentByCategory")
@Produces("application/json")
@Consumes("application/json")
public class GetContentByCategory {
    private final IContentCategoryDao mContentCategoryDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;

    @Inject
    public GetContentByCategory(IContentCategoryDao mContentCategoryDao,IContentDetailsDao mContentDetailsDao,
                                IContentUserLikeDao mContentUserLikeDao, IUserDetailsDao mUserDetailsDao,
                                IUserUserDao mUserUserDao, ICategoryDetailsDao mCategoryDetailsDao) {
        this.mContentCategoryDao = mContentCategoryDao;
        this.mContentDetailsDao = mContentDetailsDao;
        this.mUserDetailsDao = mUserDetailsDao;
        this.mContentUserLikeDao = mContentUserLikeDao;
        this.mUserUserDao = mUserUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
    }

    @POST
    public Response getContentByCategory(GetContentByCategoryRequest getContentByCategoryRequest) {
        try {
            if (getContentByCategoryRequest == null)
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            if (!getContentByCategoryRequest.isValid())
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getContentByCategoryRequest.errorMessage());
            Integer categoryId;
            if(getContentByCategoryRequest.getCategoryName()!=null && getContentByCategoryRequest.getCategoryName()!=""){
                String categoryName = getContentByCategoryRequest.getCategoryName();
                categoryId = (Integer) mCategoryDetailsDao.getCategoryId(categoryName);
            }
            else
                categoryId = getContentByCategoryRequest.getCategoryId();
            int userId = getContentByCategoryRequest.getUserId();

            List<Integer> contentIdList = mContentCategoryDao.getContentIdsFromCategoryId(categoryId);
            long registeredTimeStamp = getContentByCategoryRequest.getRegisteredTimeStamp();
            long setId=0;
            if( registeredTimeStamp>0)
                setId = timeStampTosetId(registeredTimeStamp);

            if (contentIdList != null) {
                JSONObject responseObj = composeResponse(userId,contentIdList,setId);
                return ResponseBuilder.successResponse(responseObj.toString());
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentCategoryDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal server error");
        }
    }

    private JSONObject composeResponse(int userId,List<Integer> contentIdList, long setId) {
        List<Content> contentList = mContentDetailsDao.getAllContentDetailsForIdsTillSetId(contentIdList,setId);
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

    private long timeStampTosetId(long timeStamp){
        return (long)((System.currentTimeMillis()-timeStamp)/(1000*60*60*24));
    }
}

