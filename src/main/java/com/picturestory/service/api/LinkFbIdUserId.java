package com.picturestory.service.api;


import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.LinkFbIdUserIdRequest;
import com.picturestory.service.request.NewUserRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by aasha.medhi on 9/25/15.
 */
@Path("/linkFbIdUserId")
@Produces("application/json")
@Consumes("application/json")
public class LinkFbIdUserId {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public LinkFbIdUserId(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response linkFbIdUserId(LinkFbIdUserIdRequest linkFbIdUserIdRequest) {
        try {
            if(linkFbIdUserIdRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!linkFbIdUserIdRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, linkFbIdUserIdRequest.errorMessage());
            }

            int userId = linkFbIdUserIdRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            User user =(User)mUserDetailsDao.getUser(userId);
            user.setFbId(linkFbIdUserIdRequest.getFbId());
            boolean status = mUserDetailsDao.updateUser(user);
            if (status) {
                return ResponseBuilder.successResponse();
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserDetailsDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

}
