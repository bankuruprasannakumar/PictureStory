package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserGcmIdDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.AddPushNotifsIdRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 30/12/15.
 */
@Path("/addPushNotifsId")
@Produces("application/json")
@Consumes("application/json")

public class AddPushNotifsId {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserGcmIdDao mUserGcmIdDao;

    @Inject
    public AddPushNotifsId(IUserDetailsDao userDetailsDao, IUserGcmIdDao userGcmIdDao){
        mUserDetailsDao = userDetailsDao;
        mUserGcmIdDao = userGcmIdDao;
    }
    @POST
    public Response addPushNotifsId(AddPushNotifsIdRequest addPushNotifsIdRequest) {
        try {
            if (addPushNotifsIdRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!addPushNotifsIdRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, addPushNotifsIdRequest.errorMessage());
            }
            int userId = addPushNotifsIdRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            boolean status;
            User u = (User) mUserDetailsDao.getUser(userId);
            u.setGcmId(addPushNotifsIdRequest.getGcmId());
            status = mUserDetailsDao.updateUser(u);
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
