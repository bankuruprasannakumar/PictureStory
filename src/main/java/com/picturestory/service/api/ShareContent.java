package com.picturestory.service.api;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.ISharedContentAssociationDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;
import com.picturestory.service.pojo.SharedIdContentIdAssociation;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.SetWallPaperRequest;
import com.picturestory.service.request.ShareContentRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 10/7/16.
 */

@Path("/shareContent")
@Produces("application/json")
@Consumes("application/json")

public class ShareContent {
    private final ISharedContentAssociationDao mSharedContentAssociationDao;
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public ShareContent(ISharedContentAssociationDao sharedContentAssociationDao,
                        IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao){
        mSharedContentAssociationDao = sharedContentAssociationDao;
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @POST
    public Response setWallPaper(ShareContentRequest shareContentRequest){
    try {
        if (shareContentRequest == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!shareContentRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, shareContentRequest.errorMessage());
        }
        int userId = shareContentRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        int contentId = shareContentRequest.getContentId();
        if (null == mContentDetailsDao.getContentDetails(contentId)){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
        }
        int sharedContentId = mSharedContentAssociationDao.CreateSharedContentId(contentId);

        if (sharedContentId != 0) {
            JSONObject responseObj = composeResponse(sharedContentId);
            return ResponseBuilder.successResponse(responseObj.toString());
        }
        else {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mSharedContentAssociationDao.getDetailedResponse().getErrorMessage());
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }

}

    private JSONObject composeResponse(int sharedContentId) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.SHARED_CONTEND_ID, sharedContentId);
        return responseObj;
    }



}
