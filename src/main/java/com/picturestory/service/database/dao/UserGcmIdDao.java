package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.UserGcmIdAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bankuru on 30/12/15.
 */
public class UserGcmIdDao implements IUserGcmIdDao<UserGcmIdAssociation> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public UserGcmIdDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addPushNotifsId(UserGcmIdAssociation userGcmIdAssociation) {
        if(!isUserGcmIdPresent(userGcmIdAssociation)){
            String query = "";
            try {
                Gson gson = new Gson();
                String userGcmIdJSON = gson.toJson(userGcmIdAssociation);
                query = Constants.INSERT_START + userGcmIdJSON + Constants.INSERT_END;
                mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
                if (mResponseData.isSuccess()) {
                    return true;
                }
            } catch (Exception j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return false;
            }
            return false;
        }
        mResponseData.setSuccess(true);
        return true;
    }
    public boolean isUserGcmIdPresent(UserGcmIdAssociation userGcmIdAssociation) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID, userGcmIdAssociation.getUserId(), Constants.GCMID, userGcmIdAssociation.getGcmId(), Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userGcmIdJSONResponse = new JSONObject(responseData.getData());
                if (userGcmIdJSONResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (JSONException j) {
                return false;
            }
        }
        return false;
    }


    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }
}
