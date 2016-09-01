package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.GetPushNfDataRequest;
import com.picturestory.service.response.ResponseBuilder;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by bankuru on 8/7/16.
 */

@Path("/getPNfData")
@Produces("application/json")
@Consumes("application/json")

public class GetPushNfData {

    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public GetPushNfData(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @POST
    public Response getSetId(GetPushNfDataRequest getPushNfDataRequest){
        try {
            if (getPushNfDataRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getPushNfDataRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getPushNfDataRequest.errorMessage());
            }
            int startIndex = getPushNfDataRequest.getStartIndex();
            int endIndex = getPushNfDataRequest.getEndIndex();

            ArrayList<User> users = mUserDetailsDao.getUsersForIndex(startIndex, endIndex);
            JSONObject responseObj = composeResponse(users);
            return ResponseBuilder.successResponse(responseObj.toString());
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(ArrayList<User> users) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        JSONArray pNfArray = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            JSONObject pushNfObject = new JSONObject();
            pushNfObject.put(Constants.USER_ID, user.getUserId());
            pushNfObject.put(Constants.USER_NAME, user.getUserName());
            pushNfObject.put(Constants.GCMID, user.getGcmId());
            long setId = GetSetId.getSetIdForFeed(user.getRegisteredTime());
            pushNfObject.put(Constants.SET_ID, setId);
            pushNfObject.put(Constants.IMAGE_URL, mContentDetailsDao.getImageForSetId(setId));
            pNfArray.put(pushNfObject);
        }
        responseObj.put(Constants.PNF_DATA, pNfArray);
        return responseObj;
    }

}
