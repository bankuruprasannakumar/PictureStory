package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.ICategoryDetailsDao;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.GetCategoryNameListRequest;
import com.picturestory.service.request.GetCategoryRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by krish on 03/07/2016.
 */

@Path("/getCategoryName")
@Produces("application/json")
@Consumes("application/json")

public class GetCategoryName {
    private final ICategoryDetailsDao<Integer,String> mCategoryDetailsDao;

    @Inject
    public GetCategoryName(ICategoryDetailsDao iCategoryDetailsDao){
        this.mCategoryDetailsDao=iCategoryDetailsDao;
    }

    @POST
    public Response getCategoryName(GetCategoryRequest getCategoryRequest){
        try {
            if (getCategoryRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getCategoryRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getCategoryRequest.errorMessage());
            }

            Integer categoryId = getCategoryRequest.getCategoryId();

            String categoryName ;
            categoryName = mCategoryDetailsDao.getCategoryName(categoryId);
            if (categoryName != null) {
                JSONObject responseObj = composeResponse(categoryName);
                return ResponseBuilder.successResponse(responseObj.toString());
            }
            else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mCategoryDetailsDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }

    }
    private JSONObject composeResponse(String categoryName) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.CATEGORY_NAME, categoryName);
        return responseObj;
    }

}
