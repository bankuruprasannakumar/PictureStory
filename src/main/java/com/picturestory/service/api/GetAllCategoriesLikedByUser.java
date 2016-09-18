package com.picturestory.service.api;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.ICategoryDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;
import com.picturestory.service.pojo.Category;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.request.GetAllCategoriesLikedByUserRequest;
import com.picturestory.service.request.GetWallPaperRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
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
@Path("/getUserIntrests")
@Produces("application/json")
@Consumes("application/json")

public class GetAllCategoriesLikedByUser {
    private final ICategoryDetailsDao mCategoryDetailsDao;
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public GetAllCategoriesLikedByUser(ICategoryDetailsDao categoryDetailsDao, IUserDetailsDao userDetailsDao){
        this.mCategoryDetailsDao = categoryDetailsDao;
        this.mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response getUserIntrests(GetAllCategoriesLikedByUserRequest getAllCategoriesLikedByUserRequest){
    try {
        if (getAllCategoriesLikedByUserRequest == null) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!getAllCategoriesLikedByUserRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getAllCategoriesLikedByUserRequest.errorMessage());
        }

        int userId = getAllCategoriesLikedByUserRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        User user = (User) mUserDetailsDao.getUser(userId);

        ArrayList<Category> allCategories = mCategoryDetailsDao.getAllCategoryDetails();
        ArrayList<Integer> userLikedCategories = mCategoryDetailsDao.getAllCategoriesLikedByUser(userId);
        JSONObject responseObj = composeResponse(allCategories, userLikedCategories);
        return ResponseBuilder.successResponse(responseObj.toString());
    }
        catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }
}

    private JSONObject composeResponse(ArrayList<Category> categoryArrayList, ArrayList<Integer> userLikedCategories) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        JSONArray categoryJSONArray = new JSONArray();
        for (int i = 0 ; i < categoryArrayList.size() ; i++) {
            Category category = categoryArrayList.get(i);
            JSONObject categoryJSON = new JSONObject();
            categoryJSON.put(Constants.CATEGORY_ID, category.getCategoryId());
            categoryJSON.put(Constants.CATEGORY_NAME, category.getCategoryName());
            categoryJSON.put(Constants.PICTURE_URL, category.getPictureUrl());
            if (userLikedCategories.contains(category.getCategoryId())) {
                categoryJSON.put(Constants.FOLLOWED_BY_USER, true);
            }
            else {
                categoryJSON.put(Constants.FOLLOWED_BY_USER, false);
            }
            categoryJSONArray.put(categoryJSON);
        }
        responseObj.put(Constants.CATEGORY_NAME_LIST, categoryJSONArray);
        return responseObj;
    }

}
