package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bankuru on 4/2/16.
 */
public class UserFollowerDetailsDao implements IUserFollowerDetailsDao<Integer,User> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public UserFollowerDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public List<User> getAllFollowers(Integer userId, int startIndex, int maxCount) {
        String query = String.format("q={!join from=%s to=%s}%s:%s&fq=%s:%s&%s=%s&%s=%s&%s", Constants.USER_ID,Constants.USER_ID, Constants.FOLLOWED_USER_ID,userId,Constants.USER_NAME,Constants.ALL,Constants.START,startIndex,Constants.ROWS,maxCount,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            return convertResponseDataToUserList(responseData);
        }
        mResponseData = responseData;
        return null;
    }
    private List<User> convertResponseDataToUserList(ResponseData responseData){
        try {
            JSONObject userResponseObject = new JSONObject(responseData.getData());
            if (userResponseObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                List<User> users = new ArrayList<User>();
                mResponseData.setSuccess(true);
                JSONArray userArray = userResponseObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                Gson gson = new Gson();
                for (int i = 0; i < userArray.length();i++){
                    JSONObject userObject = userArray.getJSONObject(i);
                    User user = gson.fromJson(userObject.toString(), User.class);
                    users.add(user);
                }
                return users;
            }
            else{
                mResponseData.setSuccess(true);
                return null;
            }
        } catch (JSONException j) {
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return null;
        }
    }

    @Override
    public int fullCountOfAllFollowers(Integer userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s",Constants.USER_ID,Constants.ALL, Constants.FOLLOWED_USER_ID,userId,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            return getFullCount(responseData);
        }
        mResponseData = responseData;
        return 0;
    }

    private int getFullCount(ResponseData responseData){
        try {
            JSONObject userResponseObject = new JSONObject(responseData.getData());
            return userResponseObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND);
        }catch (JSONException j){
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return 0;
        }
    }

    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
