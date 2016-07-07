package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Category;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by krish on 03/07/2016.
 */
public class CategoryDetailsDao implements ICategoryDetailsDao<Integer,String> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public CategoryDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }
    @Override
    public String getCategoryName(Integer categoryId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.CATEGORY_ID,categoryId, Constants.CATEGORY_NAME, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject categoryResponse = new JSONObject(responseData.getData());
                if (categoryResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject userJsonObject = categoryResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Category category = gson.fromJson(userJsonObject.toString(),Category.class);
                    return category.getCategoryName();
                }
                else {
                    mResponseData.setErrorMessage("Invalid category id");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return null;
                }
            }catch (JSONException j){
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        return null;

    }

    @Override
    public List<String> getCategoryNameList(List<Integer> categoryIdList) {
        String idList = "(";
        if(categoryIdList!=null)
            for(int i=0;i<categoryIdList.size()-1;i++)
                idList+=(categoryIdList.get(i)+" OR ");

        if(categoryIdList!=null)
            idList+=(categoryIdList.get(categoryIdList.size()-1));
        idList+=")";

        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.CATEGORY_ID,idList, Constants.CATEGORY_NAME, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject categoryResponse = new JSONObject(responseData.getData());
                if (categoryResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    List<String> categoryNameList = new ArrayList<String>();
                    JSONArray categoryArray = categoryResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    for(int i=0;i<categoryArray.length();i++){
                        JSONObject categoryObject = categoryArray.getJSONObject(i);
                        Category category = gson.fromJson(categoryObject.toString(),Category.class);
                        categoryNameList.add(category.getCategoryName());
                    }
                    return categoryNameList;
                }
                else {
                    mResponseData.setErrorMessage("Invalid category ids");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return null;
                }
            }catch (JSONException j){
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        return null;
    }

    @Override
    public Integer getCategoryId(String categoryName) {
        String query = "";
        query = String.format("q=%s:%s&%s", Constants.CATEGORY_NAME, categoryName, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject categoryResponse = new JSONObject(responseData.getData());
                if (categoryResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject userJsonObject = categoryResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Category category = gson.fromJson(userJsonObject.toString(),Category.class);
                    return category.getCategoryId();
                }
                else {
                    mResponseData.setErrorMessage("Invalid category id");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return null;
                }
            }catch (JSONException j){
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        return null;
    }



    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }
}
