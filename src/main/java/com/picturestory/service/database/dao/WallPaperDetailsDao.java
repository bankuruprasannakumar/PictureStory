package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

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

    }

    @Override
    public ResponseData getDetailedResponse(){
        return mResponseData;
    }

}
