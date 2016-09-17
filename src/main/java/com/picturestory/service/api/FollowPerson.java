package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserUserDao;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.FollowPersonRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 22/6/16.
 */
@Path("/followPerson")
@Produces("application/json")
@Consumes("application/json")

public class FollowPerson {
    private final IUserUserDao mUserUserDao;
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public FollowPerson(IUserUserDao userUserDao, IUserDetailsDao userDetailsDao){
        mUserUserDao = userUserDao;
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response followPerson(FollowPersonRequest followPersonRequest){
        if (followPersonRequest == null) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!followPersonRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        //Check if user is valid
        int userId = followPersonRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        //Check if person is valid
        int personId = followPersonRequest.getPersonId();
        if (null == mUserDetailsDao.getUser(personId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(followPersonRequest.getUserId());
        userUserAssociation.setFollowedUserId(followPersonRequest.getPersonId());
        boolean doFollow = followPersonRequest.getDoFollowValue();
        boolean status;
        if (doFollow) {
            status = mUserUserDao.addUserUser(userUserAssociation);
        } else {
            status = mUserUserDao.deleteUserUser(userUserAssociation);
        }
        if (status == true)
            return ResponseBuilder.successResponse();
        else
            return ResponseBuilder.error(mUserUserDao.getDetailedResponse().getErrorCode(), mUserUserDao.getDetailedResponse().getErrorMessage());
    }

}
