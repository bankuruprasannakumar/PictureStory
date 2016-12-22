package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserFolderAssociationDao;
import com.picturestory.service.database.dao.IUserSetIdDao;
import com.picturestory.service.pojo.UserFolderAssociation;
import com.picturestory.service.request.AddSetIdToFinishedSetsForUserRequest;
import com.picturestory.service.request.CreateFolderRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 22/12/16.
 */
@Path("/addSetIdForUser")
@Produces("application/json")
@Consumes("application/json")

public class AddSetIdForUser {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserSetIdDao mUserSetIdDao;

    @Inject
    public AddSetIdForUser(IUserDetailsDao userDetailsDao, IUserSetIdDao userSetIdDao){
        mUserDetailsDao = userDetailsDao;
        mUserSetIdDao = userSetIdDao;
    }

    @POST
    public Response addSetIdForUser(AddSetIdToFinishedSetsForUserRequest addSetIdToFinishedSetsForUserRequest) {
        try {
            if (addSetIdToFinishedSetsForUserRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!addSetIdToFinishedSetsForUserRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, addSetIdToFinishedSetsForUserRequest.errorMessage());
            }
            int userId = addSetIdToFinishedSetsForUserRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }

            boolean status = false;
            status = mUserSetIdDao.addSetIdToFinishedSets(userId, addSetIdToFinishedSetsForUserRequest.getSetId());
            if (status) {
                return ResponseBuilder.successResponse();
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserSetIdDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }


}
