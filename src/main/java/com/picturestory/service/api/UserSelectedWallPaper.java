package com.picturestory.service.api;

import com.amazonaws.util.StringUtils;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserWallPaperAssociation;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.GetWallPaperRequest;
import com.picturestory.service.request.UserSelectedWallPaperV2Request;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bankuru on 24/6/16.
 */
@Path("/v2/userSelectedWallPaper")
@Produces("application/json")
@Consumes("application/json")

public class UserSelectedWallPaper {
    private final IWallPaperDetailsDao mWallPaperDetailsDao;
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;


    @Inject
    public UserSelectedWallPaper(IWallPaperDetailsDao wallPaperDetailsDao, IUserDetailsDao userDetailsDao,
                                 IContentDetailsDao contentDetailsDao){
        mWallPaperDetailsDao = wallPaperDetailsDao;
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @POST
    public Response getWallPaper(UserSelectedWallPaperV2Request userSelectedWallPaperV2Request){
    try {
        if (userSelectedWallPaperV2Request == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!userSelectedWallPaperV2Request.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, userSelectedWallPaperV2Request.errorMessage());
        }
        int contentId = userSelectedWallPaperV2Request.getContentId();
        if (null == mContentDetailsDao.getContentDetails(contentId)){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
        }

        int userId = userSelectedWallPaperV2Request.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        User user =(User)mUserDetailsDao.getUser(userId);

        boolean status;
        List<Integer> contentIds = mWallPaperDetailsDao.getUserSelectedWallPaper(userId);
        UserWallPaperAssociation userWallPaperAssociation = new UserWallPaperAssociation();
        userWallPaperAssociation.setUserId(userSelectedWallPaperV2Request.getUserId());
        if(userSelectedWallPaperV2Request.getDoSelectValue()) {
            if (contentIds == null || contentIds.isEmpty()) {
                contentIds = new ArrayList<Integer>();
                contentIds.add(userSelectedWallPaperV2Request.getContentId());
                userWallPaperAssociation.setWallPaperContnetIds(contentIds);
                status = mWallPaperDetailsDao.setUserSelectedWallPaper(userWallPaperAssociation);
            } else {
                if (contentIds.contains(userSelectedWallPaperV2Request.getContentId())) {
                    status = true;
                } else {
                    contentIds.add(userSelectedWallPaperV2Request.getContentId());
                    userWallPaperAssociation.setWallPaperContnetIds(contentIds);
                    status = mWallPaperDetailsDao.updateUserSelectedWallPaper(userWallPaperAssociation);
                }
            }
        }
        else{
            if (contentIds == null || contentIds.isEmpty()) {
                status = true;
            } else {
                if (contentIds.contains(userSelectedWallPaperV2Request.getContentId())) {
                    contentIds.remove(userSelectedWallPaperV2Request.getContentId());
                    userWallPaperAssociation.setWallPaperContnetIds(contentIds);
                    status = mWallPaperDetailsDao.updateUserSelectedWallPaper(userWallPaperAssociation);
                } else {
                    status = true;
                }
            }
        }

        if (status) {
            return ResponseBuilder.successResponse();
        } else {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mWallPaperDetailsDao.getDetailedResponse().getErrorMessage());
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }
    }
}
