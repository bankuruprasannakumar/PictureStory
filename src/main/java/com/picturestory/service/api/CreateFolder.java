package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.ICommentUserLikeDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserFolderAssociationDao;
import com.picturestory.service.pojo.CommentUserLikeAssociation;
import com.picturestory.service.pojo.UserFolderAssociation;
import com.picturestory.service.request.CreateFolderRequest;
import com.picturestory.service.request.LikeCommentRequest;
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
@Path("/createFolder")
@Produces("application/json")
@Consumes("application/json")

public class CreateFolder {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserFolderAssociationDao mUserFolderAssociationDao;

    @Inject
    public CreateFolder(IUserDetailsDao userDetailsDao, IUserFolderAssociationDao userFolderAssociationDao){
        mUserDetailsDao = userDetailsDao;
        mUserFolderAssociationDao = userFolderAssociationDao;
    }
    @POST
    public Response createFolder(CreateFolderRequest createFolderRequest) {
        try {
            if (createFolderRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!createFolderRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, createFolderRequest.errorMessage());
            }
            int userId = createFolderRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            UserFolderAssociation userFolderAssociation = new UserFolderAssociation();
            userFolderAssociation.setUserId(userId);
            userFolderAssociation.setFolderImageId(createFolderRequest.getFolderImageId());
            userFolderAssociation.setFolderName(createFolderRequest.getFolderName());
            boolean status = false;
            if(createFolderRequest.getDoCreateValue())
                status = mUserFolderAssociationDao.addUserFolderAssociation(userFolderAssociation);
            else
                status = mUserFolderAssociationDao.deleteUserFolderAssociation(userFolderAssociation);
            if (status) {
                return ResponseBuilder.successResponse();
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserFolderAssociationDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}
