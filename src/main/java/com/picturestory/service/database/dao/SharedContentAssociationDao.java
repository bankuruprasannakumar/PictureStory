package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.SharedIdContentIdAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by bankuru on 10/7/16.
 */
public class SharedContentAssociationDao implements ISharedContentAssociationDao<SharedIdContentIdAssociation> {

    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public SharedContentAssociationDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public int CreateSharedContentId(int contentId) {
        String query = "";
        try {
            JSONObject sharedContentObject = new JSONObject();
            sharedContentObject.put(Constants.INGESTION_TIME,System.currentTimeMillis());
            sharedContentObject.put(Constants.CONTENT_ID, contentId);
            Random rand = new Random();
            int sharedContentId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            sharedContentObject.put(Constants.SHARED_CONTEND_ID, sharedContentId);
            query = Constants.INSERT_START + sharedContentObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return sharedContentId;
            }
        }catch (JSONException j){
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return 0;
        }
        return 0;
    }

    @Override
    public int getContentIdforSharedContentId(int sharedContentId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.SHARED_CONTEND_ID, sharedContentId, Constants.CONTENT_ID,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject sharedContentResponse = new JSONObject(responseData.getData());
                if (sharedContentResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject sharedContentIdJsonObject = sharedContentResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    SharedIdContentIdAssociation sharedIdContentIdAssociation = gson.fromJson(sharedContentIdJsonObject.toString(),SharedIdContentIdAssociation.class);
                    return sharedIdContentIdAssociation.getContentId();
                }
                else {
                    mResponseData.setErrorMessage("Invalid sharedContentId");
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
                    return 0;
                }
            }catch (JSONException j){
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return 0;
            }
        }
        return 0;
    }

    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
