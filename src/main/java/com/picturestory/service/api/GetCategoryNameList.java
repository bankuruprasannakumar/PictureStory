package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.ICategoryDetailsDao;
import com.picturestory.service.request.GetCategoryNameListRequest;
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
@Path("/getCategoryNameList")
@Produces("application/json")
@Consumes("application/json")
public class GetCategoryNameList {
    private final ICategoryDetailsDao<Integer,String> mCategoryDetailsDao;

    @Inject
    public GetCategoryNameList(ICategoryDetailsDao mCategoryDetailsDao){
        this.mCategoryDetailsDao=mCategoryDetailsDao;
    }


    @POST
    public Response getCategoryNameList(GetCategoryNameListRequest getCategoryNameListRequest){
        try {
            if (getCategoryNameListRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getCategoryNameListRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getCategoryNameListRequest.errorMessage());
            }

            List<Integer> categoryIdList = getCategoryNameListRequest.getCategoryIdList();

            List<String> categoryNameList ;
            categoryNameList = mCategoryDetailsDao.getCategoryNameList(categoryIdList);
            if (categoryNameList != null) {
                JSONObject responseObj = composeResponseList(categoryNameList);
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
    private JSONObject composeResponseList(List<String> categoryNameList) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.CATEGORY_NAME_LIST, categoryNameList);
        return responseObj;
    }
}
