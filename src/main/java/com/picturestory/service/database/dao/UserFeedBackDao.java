package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.UserFeedBackAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bankuru on 11/1/16.
 */
public class UserFeedBackDao implements IUserFeedBackDao<UserFeedBackAssociation> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public UserFeedBackDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addFeedBack(UserFeedBackAssociation userFeedBackAssociation){
        String query = "";
        try {
            Gson gson = new Gson();
            String feedBack = gson.toJson(userFeedBackAssociation);
            JSONObject feedBackObject = new JSONObject(feedBack);
            feedBackObject.put(Constants.INGESTION_TIME,System.currentTimeMillis());
            query = Constants.INSERT_START + feedBackObject.toString() + Constants.INSERT_END;
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
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
