package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 8/7/16.
 */

@Path("/getUserTotalCount")
@Produces("application/json")
@Consumes("application/json")

public class GetUserTotalCount {

    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public GetUserTotalCount(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response getUserTotalCount(){
        try {
            int numberOfUsers = mUserDetailsDao.getTotalCount();
            JSONObject responseObj = composeResponse(numberOfUsers);
            return ResponseBuilder.successResponse(responseObj.toString());
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(int numberOfUsers) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.NUMBER_OF_USERS, numberOfUsers);
        return responseObj;
    }

}
