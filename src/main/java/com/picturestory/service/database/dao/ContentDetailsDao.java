package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs ;
import com.picturestory.service.Constants ;
import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.pojo.*;
import com.picturestory.service.response.ResponseData ;
import com.picturestory.service.pojo.Content;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentDetailsDao implements IContentDetailsDao<Content> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public ContentDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }


    @Override
    public Content getContentDetails(int id) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.CONTENT_ID, id, Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject contentJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Content content = gson.fromJson(contentJsonObject.toString(), Content.class);
                    return content;
                } else {
                    mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return null;
                }
            } catch (JSONException j) {
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
    public List<Content> getAllContentDetails() {
        String query = String.format("q=s:%s&%s&%s=%s&%s=%s",Constants.PICTURE_DESCRIPTION,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        mResponseData = responseData;
        return null;
    }



    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
