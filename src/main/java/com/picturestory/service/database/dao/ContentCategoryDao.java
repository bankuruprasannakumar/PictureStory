package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.ContentCategoryAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krish on 03/07/2016.
 */
public class ContentCategoryDao implements IContentCategoryDao {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public ContentCategoryDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public List<Integer> getCategoryIdListFromContentId(Integer contentId) {
        String query="";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.CONTENT_ID,contentId,Constants.CATEGORY_ID,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject categoryResponse = new JSONObject(responseData.getData());
                if(categoryResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    List<Integer> categoryIdList = new ArrayList<Integer>();
                    JSONArray responseArray = categoryResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    for(int i=0;i<responseArray.length();i++){
                        JSONObject responseObject = responseArray.getJSONObject(i);
                        ContentCategoryAssociation contentCategoryAssociation = gson.fromJson(responseObject.toString(),ContentCategoryAssociation.class);
                        categoryIdList.add(contentCategoryAssociation.getCategoryId());
                    }
                    return categoryIdList;
                }else{
                    mResponseData.setErrorMessage("Invalid content id");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
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
    public List<Integer> getContentIdsFromCategoryId(Integer categoryId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s",Constants.CATEGORY_ID,categoryId,Constants.CONTENT_ID,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject responseObject = new JSONObject(responseData.getData());
                if(responseObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    List<Integer> contentIdList = new ArrayList<Integer>();
                    JSONArray  responseArray = responseObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson= new Gson();
                    for (int i=0;i<responseArray.length();i++){
                        JSONObject categoryObject = responseArray.getJSONObject(i);
                        ContentCategoryAssociation contentCategoryAssociation = gson.fromJson(categoryObject.toString(),ContentCategoryAssociation.class);
                        contentIdList.add(contentCategoryAssociation.getContentId());
                    }
                    return contentIdList;
                }else{
                    mResponseData.setErrorMessage("Invalid category Id");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return null;
                }
            }catch (JSONException e){
                e.printStackTrace();
                mResponseData.setSuccess(false);
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setErrorMessage(e.toString());
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
