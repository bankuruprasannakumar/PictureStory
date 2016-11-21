package com.picturestory.service.api;

/**
 * Created by sriram on 5/9/16.
 */

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserGcmIdDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.AddPushNotifsIdRequest;
import com.picturestory.service.request.UpdateUserInterestsRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/updateUserInterests")
@Produces("application/json")
@Consumes("application/json")

public class UpdateUserInterests {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public UpdateUserInterests(IUserDetailsDao userDetailsDao){
        mUserDetailsDao = userDetailsDao;
    }
    @POST
    public Response updateUserInterests(UpdateUserInterestsRequest updateUserInterestsRequest) {
        try {
            if (updateUserInterestsRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!updateUserInterestsRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, updateUserInterestsRequest.errorMessage());
            }
            int userId = updateUserInterestsRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            boolean status;
            status = mUserDetailsDao.updateUserInterests(userId,updateUserInterestsRequest.getUserInterests());
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