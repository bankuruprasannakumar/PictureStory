package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.*;
import com.picturestory.service.response.WebResponseBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by bankuru on 29/8/16.
 */
@Path("/contributor/pixtoriesInApp")
@Produces("application/json")
@Consumes("application/json")

public class PixtoriesInApp {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IContentCategoryDao mContentCategoryDao;
    private final ICategoryDetailsDao mCategoryDetailsDao;

    @Inject
    public PixtoriesInApp(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                       IContentUserLikeDao contentUserDao, IUserUserDao userUserDao,
                       ICategoryDetailsDao mCategoryDetailsDao, IContentCategoryDao mContentCategoryDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mCategoryDetailsDao = mCategoryDetailsDao;
        this.mContentCategoryDao = mContentCategoryDao;
    }

    @GET
    public Response getContentDetailList(@CookieParam("cookieId") String cookieId) {
        try {
            if (cookieId == null){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            int userId = mUserDetailsDao.isCookiePresent(cookieId);
            if (userId == 0){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            User user = (User) mUserDetailsDao.getUser(userId);
            if (null == user) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            List<Content> contentList;
            contentList = mContentDetailsDao.getAllContentDetailsContributedByUserId(userId, PixtoryStatus.INAPP.getValue());
            return WebResponseBuilder.successResponse(composeResponse(contentList));
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }



    private String composeResponse(List<Content> contentList) throws JSONException{
        JSONObject responseJSON = new JSONObject();
        JSONObject diagnosticsJSON = new JSONObject();
        JSONObject dataJSON = new JSONObject();

        //construct userDetails
        JSONArray contentJSONArray = new JSONArray();
        if (contentList != null) {
            for (int i = 0; i < contentList.size(); i++) {
                Content content = contentList.get(i);
                JSONObject contentJSON = new JSONObject();
                contentJSON.put(Constants.ID, content.getContentId());
                contentJSON.put(Constants.IMAGE_URL, content.getPictureUrl());
                contentJSON.put(Constants.TITLE, content.getPictureDescription());
                contentJSONArray.put(contentJSON);
            }
        }
        //construct data
        dataJSON.put(Constants.SUCCESS, true);
        dataJSON.put(Constants.CONTENT_LIST, contentJSONArray);
        responseJSON.put(Constants.DIAGNOSTICS, diagnosticsJSON);
        responseJSON.put(Constants.DATA, dataJSON);
        return responseJSON.toString();
    }

    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }

}
