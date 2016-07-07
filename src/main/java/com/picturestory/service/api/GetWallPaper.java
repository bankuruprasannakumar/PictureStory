package com.picturestory.service.api;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.GetWallPaperRequest;
import com.picturestory.service.request.SetWallPaperRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 24/6/16.
 */
@Path("/getWallPaper")
@Produces("application/json")
@Consumes("application/json")

public class GetWallPaper {
    private final IWallPaperDetailsDao mWallPaperDetailsDao;
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public GetWallPaper(IWallPaperDetailsDao wallPaperDetailsDao, IUserDetailsDao userDetailsDao){
        this.mWallPaperDetailsDao = wallPaperDetailsDao;
        this.mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response getWallPaper(GetWallPaperRequest getWallPaperRequest){
    try {
        if (getWallPaperRequest == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!getWallPaperRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getWallPaperRequest.errorMessage());
        }

        int userId = getWallPaperRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        User user =(User)mUserDetailsDao.getUser(userId);
        long registeredTimeStamp = user.getRegisteredTime();

        WallPaper wallPaperObject ;
        wallPaperObject = mWallPaperDetailsDao.getWallPaperFromSetId(timeStampTosetId(registeredTimeStamp));
        if (wallPaperObject != null) {
            JSONObject responseObj = composeResponse(wallPaperObject.getWallPaper());
            return ResponseBuilder.successResponse(responseObj.toString());
        }
        else {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mWallPaperDetailsDao.getDetailedResponse().getErrorMessage());
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }

}
    private long timeStampTosetId(long timeStamp){
        return (long)((System.currentTimeMillis()-timeStamp)/(1000*60*60*24));
    }

    private JSONObject composeResponse(String wallPaper) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.WALL_PAPER, wallPaper);
        return responseObj;
    }

}
