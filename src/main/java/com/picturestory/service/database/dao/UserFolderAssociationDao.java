package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserFolderAssociation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by bankuru on 22/12/16.
 */
public class UserFolderAssociationDao implements IUserFolderAssociationDao<UserFolderAssociation> {

    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public UserFolderAssociationDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }


    @Override
    public boolean addUserFolderAssociation(UserFolderAssociation userFolderAssociation) {
        boolean isAssociationPresent = isUserFolderAssocitaionPresent(userFolderAssociation);
        if (isAssociationPresent) {
            return true;
        } else {
            return createUserFolderAssocitation(userFolderAssociation);
        }
    }

    @Override
    public List<UserFolderAssociation> getFolderForUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.USER_ID, userId, Constants.FOLDER_IMAGE_ID, Constants.ALL, Constants.FOLDER_NAME, Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userFolderResponse = new JSONObject(responseData.getData());
                if (userFolderResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0 ) {
                    JSONArray userFolderJsonArray = userFolderResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<UserFolderAssociation>>(){}.getType();
                    ArrayList<UserFolderAssociation> userFolderAssociationList = gson.fromJson(userFolderJsonArray.toString(),listType);
                    return userFolderAssociationList;

                }
                else {
                    return new ArrayList<UserFolderAssociation>();
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
        return null;
    }

    @Override
    public boolean deleteUserFolderAssociation(UserFolderAssociation userFolderAssociation) {
        String query = "";
        query = String.format("%s:%s AND %s:%s AND %s:%s",Constants.USER_ID,userFolderAssociation.getUserId(), Constants.FOLDER_IMAGE_ID, userFolderAssociation.getFolderImageId(), Constants.FOLDER_NAME, userFolderAssociation.getFolderName());
        query = Constants.DELETE_START +query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    private boolean isUserFolderAssocitaionPresent(UserFolderAssociation userFolderAssociation) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s", Constants.USER_ID, userFolderAssociation.getUserId(), Constants.FOLDER_IMAGE_ID, userFolderAssociation.getFolderImageId(), Constants.FOLDER_NAME, userFolderAssociation.getFolderName(), Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userFolderResponse = new JSONObject(responseData.getData());
                if (userFolderResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0 ) {
                    mResponseData.setSuccess(true);
                    return true;
                }
                else {
                    return false;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return false;
            }
        }else {
            mResponseData = responseData;
        }
        return false;
    }

    private boolean createUserFolderAssocitation(UserFolderAssociation userFolderAssociation) {
        String query = "";
        try {
            Gson gson = new Gson();
            String jsonUser = gson.toJson(userFolderAssociation);
            JSONObject userFolderObject = new JSONObject(jsonUser);
            query = Constants.INSERT_START + userFolderObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return true;
            } else {
                mResponseData.setSuccess(false);
                mResponseData.setErrorMessage(Constants.INVALID_USER_NAME);
                mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
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
