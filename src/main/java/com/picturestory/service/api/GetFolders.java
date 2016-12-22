package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserFolderAssociationDao;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserFolderAssociation;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.GetFoldersRequest;
import com.picturestory.service.request.GetWallPaperRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by bankuru on 22/12/16.
 */

@Path("/getFolders")
@Produces("application/json")
@Consumes("application/json")

public class GetFolders {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserFolderAssociationDao mUserFolderAssociationDao;

    @Inject
    public GetFolders(IUserDetailsDao userDetailsDao, IUserFolderAssociationDao userFolderAssociationDao){
        mUserDetailsDao = userDetailsDao;
        mUserFolderAssociationDao = userFolderAssociationDao;
    }

    @POST
    public Response getFolders(GetFoldersRequest getFoldersRequest){
        try {
            if (getFoldersRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getFoldersRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getFoldersRequest.errorMessage());
            }

            int userId = getFoldersRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            List<UserFolderAssociation> userFolderAssociations = mUserFolderAssociationDao.getFolderForUser(userId);
            if (userFolderAssociations != null) {
                JSONObject responseObj = composeResponse(userFolderAssociations);
                return ResponseBuilder.successResponse(responseObj.toString());
            }
            else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserFolderAssociationDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(List<UserFolderAssociation> userFolderAssociations) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            JSONArray userFolderJSONArray = new JSONArray();
            for (int index = 0 ; index < userFolderAssociations.size() ; index++) {
                JSONObject userFolderJSON = new JSONObject();
                userFolderJSON.put(Constants.FOLDER_IMAGE_ID, userFolderAssociations.get(index).getFolderImageId());
                userFolderJSON.put(Constants.FOLDER_NAME, userFolderAssociations.get(index).getFolderName());
                userFolderJSONArray.put(userFolderJSON);
            }
            response.put(Constants.FOLDERS_LIST, userFolderJSONArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }


}
