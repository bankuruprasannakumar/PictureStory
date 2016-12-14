package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.Template;
import com.picturestory.service.pojo.TemplateBucket;
import com.picturestory.service.pojo.UserTemplateBucketAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bankuru on 4/11/16.
 */
public class UserTemplateBucketDao implements IUserTemplateBucketDao<UserTemplateBucketAssociation> {

    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public UserTemplateBucketDao(IDataAccessAdapter solrAdapter){
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean incrementUserTemplateCount(int userId) {
        JSONObject userJsonObject = new JSONObject();
        JSONObject setQuery = new JSONObject();
        String query = "";
        String recordId;
        int previousTemplateCount = 5;
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID, userId,Constants.TEMPLATE_COUNT, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    recordId = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getString(Constants.ID);
                    previousTemplateCount = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getInt(Constants.TEMPLATE_COUNT);
                } else {
                    mResponseData.setSuccess(false);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setErrorMessage(Constants.INVALID_USER_ID);
                    return false;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return false;
            }
        } else {
            mResponseData = responseData;
            return false;
        }
        try {
            setQuery = new JSONObject();
            setQuery.put(Constants.SET, previousTemplateCount);
            userJsonObject.put(Constants.TEMPLATE_COUNT, setQuery);
            userJsonObject.put(Constants.ID, recordId);
            query = userJsonObject.toString();
            query = Constants.INSERT_START + query +Constants.INSERT_END;
            mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return true;
            }
        } catch (JSONException j) {
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return false;
        }
        return false;
    }

    @Override
    public int getUserTemplateCount(int userId) {
        return 0;
    }

    @Override
    public boolean unlockUserTemplateBucket(int userId, int bucketId) {
        JSONObject userJsonObject = new JSONObject();
        JSONObject setQuery = new JSONObject();
        String query = "";
        String recordId;
        ArrayList<Integer> userTemplateBucketIds;
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID,userId,Constants.UNLOCKED_TEMPLATE_BUCKET_IDS,Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject responseJSON = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    recordId = responseJSON.getString(Constants.ID);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Integer>>(){}.getType();
                    JSONArray userTemplateBucketIdsJSONArray = responseJSON.getJSONArray(Constants.UNLOCKED_TEMPLATE_BUCKET_IDS);
                    userTemplateBucketIds = gson.fromJson(userTemplateBucketIdsJSONArray.toString(), listType);
                    if (userTemplateBucketIds.contains(bucketId)) {
                        return true;
                    } else {
                        userTemplateBucketIds.add(bucketId);
                    }
                } else {
                    mResponseData.setSuccess(false);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setErrorMessage(Constants.INVALID_USER_ID);
                    return false;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return false;
            }
        } else {
            mResponseData = responseData;
            return false;
        }
        try {
            setQuery = new JSONObject();
            setQuery.put(Constants.SET,userTemplateBucketIds);
            userJsonObject.put(Constants.UNLOCKED_TEMPLATE_BUCKET_IDS, setQuery);
            userJsonObject.put(Constants.ID, recordId);
            query = userJsonObject.toString();
            query = Constants.INSERT_START + query +Constants.INSERT_END;
            mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return true;
            }
        } catch (JSONException j) {
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return false;
        }
        return false;
    }

    @Override
    public UserTemplateBucketAssociation getUserTemplateBucketAssociation(int userId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID,userId,Constants.UNLOCKED_TEMPLATE_BUCKET_IDS,Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userTemplateBucketResponse = new JSONObject(responseData.getData());
                if (userTemplateBucketResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject responseJSON = userTemplateBucketResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    UserTemplateBucketAssociation userTemplateBucketAssociation = gson.fromJson(responseJSON.toString(),UserTemplateBucketAssociation.class);
                    return userTemplateBucketAssociation;
                }
                else {
                    mResponseData.setErrorMessage("Invalid wallPaper");
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
    public List<Template> getTemplatesForBucketIds(List<Integer> bucketIds) {
        if(bucketIds == null || bucketIds.isEmpty()){
            mResponseData.setSuccess(false);
            mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
            return new ArrayList<Template>();
        }
        String subQuery = "";
        subQuery = "(";
        for (int index = 0; index < bucketIds.size(); index++) {
            subQuery += bucketIds.get(index) + " OR ";
        }
        subQuery = subQuery.substring(0, (subQuery.length() - 3));
        subQuery += ")";

        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.TEMPLATE_ID,Constants.ALL,Constants.BUCKET_ID,subQuery, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject templateBucketResponse = new JSONObject(responseData.getData());
                if (templateBucketResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONArray templateArray = templateBucketResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Template>>(){}.getType();
                    List<Template> templateList = gson.fromJson(templateArray.toString(), listType);
                    return templateList;
                }
                else {
                    mResponseData.setErrorMessage("Invalid wallPaper");
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
    public List<TemplateBucket> getAllBuckets() {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.BUCKET_NAME, Constants.ALL,Constants.BUCKET_ID,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject templateBucketResponse = new JSONObject(responseData.getData());
                if (templateBucketResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONArray bucketArray = templateBucketResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<TemplateBucket>>(){}.getType();
                    List<TemplateBucket> bucketList = gson.fromJson(bucketArray.toString(), listType);
                    return bucketList;
                }
                else {
                    mResponseData.setErrorMessage("Invalid wallPaper");
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
}
