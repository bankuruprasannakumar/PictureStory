package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs ;
import com.picturestory.service.Constants ;
import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.response.ResponseData ;
import com.picturestory.service.pojo.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by aasha.medhi on 9/24/15.
 */
public class UserDetailsDao implements IUserDetailsDao<User>{
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public UserDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }


    @Override
    public int addUser(User user) {
        ResponseData responseData = isUserPresent(user);
        if(responseData.isSuccess()){
            System.out.println("User present check success");
            if(Integer.parseInt(responseData.getData())!=0){
                user.setUserId(Integer.parseInt(responseData.getData()));
                updateUser(user);
                return Integer.parseInt(responseData.getData()) ;
            }
            else {
                int currntUserId = createUser(user);
                if(currntUserId != 0){
                    return currntUserId;
                }
                return 0;
            }
        }
        return 0;
    }

    @Override
    public int addUserForFbId(User user) {
        ResponseData responseData = isUserPresentForFbId(user);
        if(responseData.isSuccess()){
            System.out.println("User present check success");
            if(Integer.parseInt(responseData.getData())!=0){
                user.setUserId(Integer.parseInt(responseData.getData()));
                updateUser(user);
                return Integer.parseInt(responseData.getData()) ;
            }
            else {
                int currntUserId = createUser(user);
                if(currntUserId != 0){
                    return currntUserId;
                }
                return 0;
            }
        }
        return 0;
    }

    private ResponseData isUserPresentForFbId(User user) {
        String query = String.format("q=%s:\"%s\"&%s", Constants.FB_ID, user.getFbId(),Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    int currentUserId = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getInt(Constants.USER_ID);
                    responseData.setData(String.valueOf(currentUserId));
                    mResponseData.setSuccess(true);
                }
                else {
                    responseData.setData(String.valueOf(0));
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }else {
            mResponseData = responseData;
        }
        return responseData;
    }

    @Override
    public boolean updateUser(User user) {
        String query = "";
        query = String.format("%s:%s AND %s:%s", Constants.USER_ID,user.getUserId(),Constants.USER_NAME,Constants.ALL);
        query = Constants.DELETE_START +query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (!mResponseData.isSuccess()) {
            return false;
        }
        try {
            Gson gson = new Gson();
            String jsonUser = gson.toJson(user);
            JSONObject userObject = new JSONObject(jsonUser);
            userObject.put(Constants.INGESTION_TIME,System.currentTimeMillis());
            query = Constants.INSERT_START + userObject.toString() + Constants.INSERT_END;
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
    public boolean deleteUser(int userId) {
        String query = "";
        query = String.format("%s:%s", Constants.USER_ID,userId);
        query = Constants.DELETE_START +query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public User getUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.USER_ID,userId, Constants.USER_NAME,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {

                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject userJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    User user = gson.fromJson(userJsonObject.toString(),User.class);
                    return user;
                }
                else {
                    mResponseData.setErrorMessage("Invalid userId");
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


    private ResponseData isUserPresent(User user) {
        String query = String.format("q=%s:\"%s\"&%s", Constants.USER_EMAIL, user.getUserEmail(),Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    int currentUserId = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getInt(Constants.USER_ID);
                    responseData.setData(String.valueOf(currentUserId));
                    mResponseData.setSuccess(true);
                }
                else {
                    responseData.setData(String.valueOf(0));
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }else {
            mResponseData = responseData;
        }
        return responseData;
    }

    @Override
    public int isUserNamePresent(User user) {
        String query = String.format("q=%s:\"%s\"&%s", Constants.USER_NAME, user.getUserName(),Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) >= 1) {
                    int currentUserId = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0).getInt(Constants.USER_ID);
                    return currentUserId;
                }
                else {
                    mResponseData.setErrorMessage(Constants.INVALID_USER_NAME);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    return 0;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return 0;
            }
        }else {
            mResponseData = responseData;
        }
        return 0;
    }

    @Override
    public int createUser(User user) {
        String query = "";
        try {
            Gson gson = new Gson();
            String jsonUser = gson.toJson(user);
            JSONObject userObject = new JSONObject(jsonUser);
            userObject.put(Constants.INGESTION_TIME,System.currentTimeMillis());
            Random rand = new Random();
            int currentuserId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            userObject.put(Constants.USER_ID, currentuserId);
            query = Constants.INSERT_START + userObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return currentuserId;
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

    private List<User> convertResponseDataToUserList(ResponseData responseData) {
        try {
            JSONObject userResponseObject = new JSONObject(responseData.getData());
            if (userResponseObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                List<User> users = new ArrayList<User>();
                mResponseData.setSuccess(true);
                JSONArray userArray = userResponseObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                Gson gson = new Gson();
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject userObject = userArray.getJSONObject(i);
                    User user = gson.fromJson(userObject.toString(), User.class);
                    users.add(user);
                }
                return users;
            } else {
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
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }
}
