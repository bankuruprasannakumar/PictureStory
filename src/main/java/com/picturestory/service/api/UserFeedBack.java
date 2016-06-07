package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserFeedBackDao;
import com.picturestory.service.pojo.UserFeedBackAssociation;
import com.picturestory.service.request.UserFeedBackRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 7/6/16.
 */

@Path("/userFeedBack")
@Produces("application/json")
@Consumes("application/json")
public class UserFeedBack {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserFeedBackDao mUserFeedBackDao;

    @Inject
    public UserFeedBack(IUserDetailsDao userDetailsDao,IUserFeedBackDao userFeedBackDao) {
        mUserDetailsDao = userDetailsDao;
        mUserFeedBackDao = userFeedBackDao;
    }

    @POST
    public Response userFeedBack(UserFeedBackRequest userFeedBackRequest){
        try {
            if (userFeedBackRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!userFeedBackRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, userFeedBackRequest.errorMessage());
            }
            int userId = userFeedBackRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            UserFeedBackAssociation userFeedBackAssociation = new UserFeedBackAssociation();
            userFeedBackAssociation.setUserId(userFeedBackRequest.getUserId());
            userFeedBackAssociation.setCategory(userFeedBackRequest.getCategory());
            userFeedBackAssociation.setFeedBack(userFeedBackRequest.getFeedBack());
            userFeedBackAssociation.setDetails(userFeedBackRequest.getDetails());
            userFeedBackAssociation.setSubCategory(userFeedBackRequest.getSubCategory());
            boolean status = false;
            status = mUserFeedBackDao.addFeedBack(userFeedBackAssociation);
            if (status == false) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserFeedBackDao.getDetailedResponse().getErrorMessage());
            } else {
                return ResponseBuilder.successResponse();
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

}
