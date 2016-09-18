package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetContentLikedUserListRequest;
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
 * Created by sriram on 2/9/16.
 */

@Path("/getContentLikedUserList")
@Produces("application/json")
@Consumes("application/json")
public class GetContentLikedUserList {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IContentDetailsDao mContentDetailsDao;
    @Inject
    public GetContentLikedUserList(IUserDetailsDao userDetailsDao, IContentDetailsDao mContentDetailsDao,
                                   IContentUserLikeDao contentUserDao, IUserUserDao userUserDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mContentDetailsDao = mContentDetailsDao;
    }

    @POST
    public Response getLikedUserList(GetContentLikedUserListRequest getContentLikedUserListRequest) {
        try {
            if (getContentLikedUserListRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getContentLikedUserListRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getContentLikedUserListRequest.errorMessage());
            }

            int userId = getContentLikedUserListRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }

            int contentId = getContentLikedUserListRequest.getContentId();
            if(null == mContentDetailsDao.getContentDetails(contentId))
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT,Constants.INVALID_CONTENT_ID);

            List<User> userList= new ArrayList<User>();
            userList = mContentUserLikeDao.usersWhoLikedContentId(contentId);
            return ResponseBuilder.successResponse(composeResponse(userId,userList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }



    private String composeResponse(int userId,List<User> userList) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            response.put(Constants.FULLCOUNT,userList.size());
            JSONArray userJSONArray = new JSONArray();
            if (null != userList) {
                for (int index = 0; index < userList.size(); index++) {
                    User user = userList.get(index);
                    JSONObject userJSON = new JSONObject();
                    //Add liked user details
                    userJSON.put(Constants.ID, user.getUserId());
                    userJSON.put(Constants.NAME, user.getUserName());
                    userJSON.put(Constants.DESCRIPTION,user.getUserDesc());
                    userJSON.put(Constants.IMAGE_URL,user.getUserImage());
                    userJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, user.getUserId()));
                    userJSONArray.put(userJSON);
                }
            }
            response.put(Constants.LIKED_USER_LIST, userJSONArray);
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
