package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bankuru on 1/2/16.
 */
public class UserUserDao implements IUserUserDao<UserUserAssociation>{
    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public UserUserDao(IDataAccessAdapter solrAdapter){
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addUserUser(UserUserAssociation userUser) {
        if (isFollowedByUser(userUser)) {  //user user association already exist
            return true;
        } else {
            return createUserUser(userUser);
        }
    }

    @Override
    public boolean deleteUserUser(UserUserAssociation userUser) {
        String query = "";
        query = String.format("%s:%s AND %s:%s",Constants.USER_ID,userUser.getUserId(), Constants.FOLLOWED_USER_ID,userUser.getFollowedUserId());
        query = Constants.DELETE_START +query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFollowedByUser(UserUserAssociation userUser){
        String query = String.format("q=%s:%s AND %s:%s&%s",Constants.USER_ID, userUser.getUserId(),Constants.FOLLOWED_USER_ID, userUser.getFollowedUserId(),Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        System.out.println(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                System.out.println(userResponse.toString());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    mResponseData.setData(Constants.INVALID_USER_ID);
                    return true;
                } else {
                    return false;
                }
            } catch (JSONException j) {
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return false;
            }
        }
        mResponseData = responseData;
        return false;
    }

    private boolean createUserUser(UserUserAssociation userUser) {
        String query = "";
        try {
            Gson gson = new Gson();
            String userUserJson = gson.toJson(userUser);
            JSONObject userUserObject = new JSONObject(userUserJson);
            query = Constants.INSERT_START + userUserObject.toString() + Constants.INSERT_END;
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
    public ResponseData getDetailedResponse(){
        return mResponseData;
    }

}
