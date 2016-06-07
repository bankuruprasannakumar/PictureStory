package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IContentUserLikeDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserFeedBackAssociation;
import com.picturestory.service.request.ContributorMailRequest;
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
@Path("/getContributorMail")
@Produces("application/json")
@Consumes("application/json")

public class GetContributorMail {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public GetContributorMail(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response userFeedBack(ContributorMailRequest contributorMailRequest){
        try {
            if (contributorMailRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!contributorMailRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, contributorMailRequest.errorMessage());
            }
            int userId = contributorMailRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            User user = (User)mUserDetailsDao.getUser(userId);
            user.setUserEmail(contributorMailRequest.getUserEmail());

            boolean status = false;
            status = mUserDetailsDao.updateUser(user);
            if (status == false) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserDetailsDao.getDetailedResponse().getErrorMessage());
            } else {
                return ResponseBuilder.successResponse();
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }



}
