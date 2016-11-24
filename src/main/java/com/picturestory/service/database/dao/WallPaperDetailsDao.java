package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.UserWallPaperAssociation;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by bankuru on 24/6/16.
 */
public class WallPaperDetailsDao implements IWallPaperDetailsDao {

    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public WallPaperDetailsDao(IDataAccessAdapter solrAdapter){
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean setWallPaper(WallPaper wallPaper) {
        String query = "";
        query = String.format("%s:%s", Constants.WALL_PAPER, Constants.ALL);
        query = Constants.DELETE_START +query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (!mResponseData.isSuccess()) {
            return false;
        }
        try {
            Gson gson = new Gson();
            String jsonWallPaper = gson.toJson(wallPaper);
            JSONObject wallPaperObject = new JSONObject(jsonWallPaper);
            query = Constants.INSERT_START + wallPaperObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return true;
            }
        }catch (JSONException j){
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return false;
        }
        return false;

    }


    @Override
    public WallPaper getWallPaper() {
        String query = "";
        query = String.format("q=%s:%s&%s", Constants.WALL_PAPER, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject wallPaperResponse = new JSONObject(responseData.getData());
                if (wallPaperResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject userJsonObject = wallPaperResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    WallPaper wallPaper = gson.fromJson(userJsonObject.toString(),WallPaper.class);
                    return wallPaper;
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
    public WallPaper getWallPaperFromSetId(Long setId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:true&%s", Constants.SET_ID, setId,Constants.IS_WALLPAPER, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject wallPaperResponse = new JSONObject(responseData.getData());
                if (wallPaperResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject userJsonObject = wallPaperResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Content content = gson.fromJson(userJsonObject.toString(),Content.class);
                    WallPaper wallPaper = new WallPaper();
                    wallPaper.setWallPaper(content.getPictureUrl());
                    return wallPaper;
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
    public List<Content> getWallPaperForV2(Long setId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:true&%s", Constants.SET_ID, setId,Constants.IS_WALLPAPER, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject wallPaperResponse = new JSONObject(responseData.getData());
                if (wallPaperResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONArray contentArray = wallPaperResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
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
    public List<Integer> getUserSelectedWallPaper(int userId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID, userId,Constants.WALL_PAPER_CONTENT_IDS, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject wallPaperResponse = new JSONObject(responseData.getData());
                if (wallPaperResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject userJsonObject = wallPaperResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    UserWallPaperAssociation userWallPaperAssociation = gson.fromJson(userJsonObject.toString(),UserWallPaperAssociation.class);
                    return userWallPaperAssociation.getWallPaperContnetIds();
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
    public boolean setUserSelectedWallPaper(UserWallPaperAssociation userWallPaperAssociation) {
        String query = "";
        try {
            Gson gson = new Gson();
            JSONObject userWallPaperJSONObject = new JSONObject();
            userWallPaperJSONObject.put(Constants.USER_ID, userWallPaperAssociation.getUserId());
            userWallPaperJSONObject.put(Constants.WALL_PAPER_CONTENT_IDS, userWallPaperAssociation.getWallPaperContnetIds());
            query = Constants.INSERT_START + userWallPaperJSONObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return true;
            }
        }catch (JSONException j){
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return false;
        }
        return false;
    }

    @Override
    public boolean updateUserSelectedWallPaper(UserWallPaperAssociation userWallPaperAssociation) {
        JSONObject userJsonObject = new JSONObject();
        JSONObject setQuery = new JSONObject();
        String query = "";
        String recordId;
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID, userWallPaperAssociation.getUserId(),Constants.WALL_PAPER_CONTENT_IDS, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    recordId = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getString(Constants.ID);
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
            setQuery.put(Constants.SET,userWallPaperAssociation.getWallPaperContnetIds());
            userJsonObject.put(Constants.WALL_PAPER_CONTENT_IDS, setQuery);
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
    public ResponseData getDetailedResponse(){
        return mResponseData;
    }

}
