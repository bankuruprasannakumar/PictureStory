package com.picturestory.service.api;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bankuru on 29/8/16.
 */
@Path("/contributor/pixtoryDetail")
@Produces("application/json")
@Consumes("application/json")

public class PixtoryDetail {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IContentUserCommentDao mContentUserCommentDao;

    @Inject
    public PixtoryDetail(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                         IContentUserLikeDao contentUserDao,
                         IContentUserCommentDao contentUserCommentDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mContentUserCommentDao = contentUserCommentDao;
    }

    @GET
    public Response getContentDetailList(@QueryParam("id") int contentId,
                                         @CookieParam("cookieId") String cookieId) {
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
            Content content = (Content)mContentDetailsDao.getContentDetailsForUser(contentId, userId);
            if (content == null){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
            }
            return WebResponseBuilder.successResponse(composeResponse(content));
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }



    private String composeResponse(Content content) throws JSONException{
        JSONObject responseJSON = new JSONObject();
        JSONObject diagnosticsJSON = new JSONObject();
        JSONObject dataJSON = new JSONObject();

        //construct userDetails
        JSONObject contentJSON = new JSONObject();
        contentJSON.put(Constants.ID, content.getContentId());
        contentJSON.put(Constants.IMAGE_URL, content.getPictureUrl());
        contentJSON.put(Constants.TITLE, content.getPictureSummary());

        //likedUsers
        List<User> userList = mContentUserLikeDao.usersWhoLikedContentId(content.getContentId());
        ArrayList<String> userNameList = new ArrayList<String>();
        if (userList != null && !userList.isEmpty()) {
            for (User user : userList) {
                userNameList.add(user.getUserName());
            }
        }
        contentJSON.put(Constants.LIKED_USERS,userNameList);
        //comments
        List<ContentUserCommentAssociation> commentList = mContentUserCommentDao.getAllCommentsForContentId(content.getContentId());
        JSONArray contentUserCommentList = new JSONArray();
        if (null != commentList) {
            for(int index = 0; index < commentList.size(); index++) {
                ContentUserCommentAssociation contentUserCommentAssociation = commentList.get(index);
                JSONObject commentJSON = new JSONObject();
                User user = (User) mUserDetailsDao.getUser(contentUserCommentAssociation.getUserId());
                if (user != null) {
                    commentJSON.put(Constants.USER_NAME, user.getUserName());
                    commentJSON.put(Constants.PROFILE_IMAGE, user.getUserImage());
                }
                commentJSON.put(Constants.ID, contentUserCommentAssociation.getCommentId());
                commentJSON.put(Constants.COMMENT, contentUserCommentAssociation.getComment());
                contentUserCommentList.put(commentJSON);
            }
        }
        contentJSON.put(Constants.COMMENT_LIST, contentUserCommentList);

        //construct data
//        dataJSON.put(Constants.SUCCESS, true);
//        dataJSON.put(Constants.CONTENT, contentJSON);
        responseJSON.put(Constants.DIAGNOSTICS, diagnosticsJSON);
        responseJSON.put(Constants.DATA, contentJSON);
        return responseJSON.toString();
    }
}
