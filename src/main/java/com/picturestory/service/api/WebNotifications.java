package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.UpdateUserDescriptionRequest;
import com.picturestory.service.response.ResponseBuilder;
import com.picturestory.service.response.WebResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 22/9/16.
 */
@Path("/contributor/notifications")
@Produces("application/json")
@Consumes("application/json")

public class WebNotifications {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public WebNotifications(IUserDetailsDao userDetailsDao){
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response updateUserDescription(@CookieParam("cookieId") String cookieId) {
        try {
            if (cookieId == null) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            int userId = mUserDetailsDao.isCookiePresent(cookieId);
            if (userId == 0) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            User user = (User) mUserDetailsDao.getUser(userId);
            if (null == user) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            boolean status;
/*
            user.setUserDesc(updateUserDescriptionRequest.getUserDescription());
*/
            status = mUserDetailsDao.updateUserDescription(user);
            if(status == false){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserDetailsDao.getDetailedResponse().getErrorMessage());
            }else{
                return ResponseBuilder.successResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

}
