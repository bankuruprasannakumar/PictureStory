package com.picturestory.service.api;


import com.google.gson.Gson;
import com.picturestory.service.Constants ;
import com.picturestory.service.database.dao.IUserDetailsDao ;
import com.picturestory.service.pojo.User ;
import com.picturestory.service.request.NewUserRequest ;
import com.picturestory.service.response.ResponseBuilder ;
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
@Path("/registerUser")
@Produces("application/json")
@Consumes("application/json")
public class RegisterUser {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public RegisterUser(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response registerUser(NewUserRequest userRequest) {
        try {
            if(userRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!userRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, userRequest.errorMessage());
            }
            User user = new User();
            user.setFbId(userRequest.getFbId());
            if (userRequest.getUserName() != null && !"".equals(userRequest.getUserName().trim())){
                user.setUserName(userRequest.getUserName());
            }
            if (userRequest.getUserEmail() != null && !"".equals(userRequest.getUserEmail().trim())){
                user.setUserEmail(userRequest.getUserEmail());
            }
            if (userRequest.getUserImageUrl() != null && !"".equals(userRequest.getUserImageUrl().trim()))
                user.setUserImage(userRequest.getUserImageUrl());
            if (userRequest.getGcmId()!=null && !"".equals(userRequest.getGcmId().trim()))
                user.setGcmId(userRequest.getGcmId());
            if (userRequest.getRegisteredTime()!=null && !"".equals(userRequest.getRegisteredTime().trim())){
                user.setRegisteredTime(userRequest.getRegisteredTime());
            }
            int userId = mUserDetailsDao.addUserForFbId(user);
            if (userId != 0) {
                JSONObject responseObj = composeResponse(userId);
                return ResponseBuilder.successResponse(responseObj.toString());
            } else {
                return ResponseBuilder.error(mUserDetailsDao.getDetailedResponse().getErrorCode(), mUserDetailsDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(int userId) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.USER_ID, userId);
        return responseObj;
    }
}
