package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetLikedUserListRequest;
import com.picturestory.service.request.GetMainFeedRequest;
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

@Path("/getLikedUserList")
@Produces("application/json")
@Consumes("application/json")
public class GetLikedUserList {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserLikeDao mContentUserLikeDao;
    private final IUserUserDao mUserUserDao;
    private final IContentDetailsDao mContentDetailsDao;
    @Inject
    public GetLikedUserList(IUserDetailsDao userDetailsDao,IContentDetailsDao mContentDetailsDao,
                       IContentUserLikeDao contentUserDao, IUserUserDao userUserDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserLikeDao = contentUserDao;
        mUserUserDao = userUserDao;
        this.mContentDetailsDao = mContentDetailsDao;
    }

    @POST
    public Response getLikedUserList(GetLikedUserListRequest getLikedUserListRequest) {
        try {
            if (getLikedUserListRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getLikedUserListRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getLikedUserListRequest.errorMessage());
            }

            int userId = getLikedUserListRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }

            int contentId = getLikedUserListRequest.getContentId();
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
