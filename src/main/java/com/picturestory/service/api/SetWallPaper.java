package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.SetWallPaperRequest;
import com.picturestory.service.response.ResponseBuilder;

/**
 * Created by bankuru on 24/6/16.
 */
@Path("/setWallPaper")
@Produces("application/json")
@Consumes("application/json")

public class SetWallPaper {
    private final IWallPaperDetailsDao mWallPaperDetailsDao;

    @Inject
    public SetWallPaper(IWallPaperDetailsDao wallPaperDetailsDao){
        this.mWallPaperDetailsDao = wallPaperDetailsDao;
    }

    @POST
    public Response setWallPaper(SetWallPaperRequest setWallPaperRequest){

        if (setWallPaperRequest == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!setWallPaperRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, setWallPaperRequest.errorMessage());
        }

        String wallPaper = setWallPaperRequest.getWallPaper();
        WallPaper wallPaperObject = new WallPaper();
        wallPaperObject.setWallPaper(wallPaper);
        boolean success = false;
        success = mWallPaperDetailsDao.setWallPaper(wallPaperObject);
        if (success) {
            return ResponseBuilder.successResponse();
        }
        else {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mWallPaperDetailsDao.getDetailedResponse().getErrorMessage());
        }


    }

}
