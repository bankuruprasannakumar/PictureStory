package com.picturestory.service.database.dao;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bankuru on 4/11/16.
 */
public class UserTemplateCountDao implements IUserTemplateCountDao {

    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public UserTemplateCountDao(IDataAccessAdapter solrAdapter){
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
}
