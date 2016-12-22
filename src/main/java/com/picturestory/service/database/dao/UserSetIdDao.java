package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.UserSetIdAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bankuru on 22/12/16.
 */
public class UserSetIdDao implements IUserSetIdDao<UserSetIdAssociation> {

    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public UserSetIdDao(IDataAccessAdapter solrAdapter){
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addSetIdToFinishedSets(int userId, int setId) {
        JSONObject userJsonObject = new JSONObject();
        JSONObject setQuery = new JSONObject();
        String query = "";
        String recordId;
        ArrayList<Integer> userFinishedSetIds;
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID,userId,Constants.USER_FINISHED_SET_IDS,Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userSetIdsResponse = new JSONObject(responseData.getData());
                if (userSetIdsResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject responseJSON = userSetIdsResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    recordId = responseJSON.getString(Constants.ID);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Integer>>(){}.getType();
                    JSONArray userSetIdsJSONArray = responseJSON.getJSONArray(Constants.USER_FINISHED_SET_IDS);
                    userFinishedSetIds = gson.fromJson(userSetIdsJSONArray.toString(), listType);
                    if (userFinishedSetIds.contains(setId)) {
                        return true;
                    } else {
                        userFinishedSetIds.add(setId);
                    }
                } else {
                    userFinishedSetIds = new ArrayList<Integer>();
                    userFinishedSetIds.add(setId);
                    JSONObject userSetIdsJSON = new JSONObject();
                    userSetIdsJSON.put(Constants.USER_ID, userId);
                    userSetIdsJSON.put(Constants.USER_FINISHED_SET_IDS, userFinishedSetIds);
                    query = Constants.INSERT_START + userSetIdsJSON.toString() + Constants.INSERT_END;
                    mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
                    if (mResponseData.isSuccess()) {
                        return true;
                    }
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
            setQuery.put(Constants.SET,userFinishedSetIds);
            userJsonObject.put(Constants.USER_FINISHED_SET_IDS, setQuery);
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
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }
}
