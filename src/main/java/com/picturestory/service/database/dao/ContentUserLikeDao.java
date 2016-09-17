package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentUserLikeDao implements IContentUserLikeDao<ContentUserLikeAssociation> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public ContentUserLikeDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }
    @Override
    public boolean addContentUserLike(ContentUserLikeAssociation contentUserAssociation) {
        if(!isContentLikedByUser(contentUserAssociation)){
            String query = "";
            try {
                Gson gson = new Gson();
                String contentUserJSON = gson.toJson(contentUserAssociation);
                query = Constants.INSERT_START + contentUserJSON + Constants.INSERT_END;
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

    @Override
    public boolean deleteContentUserLike(ContentUserLikeAssociation contentUserAssociation) {
        String query = "";
        query = String.format("%s:%s AND %s:%s", Constants.LIKED_USER_ID, contentUserAssociation.getLikeduserId(), Constants.CONTENT_ID, contentUserAssociation.getContentId());
        query = Constants.DELETE_START + query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isContentLikedByUser(ContentUserLikeAssociation contentUserAssociation) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.LIKED_USER_ID, contentUserAssociation.getLikeduserId(), Constants.CONTENT_ID, contentUserAssociation.getContentId(), Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userSubCategoryJSONResponse = new JSONObject(responseData.getData());
                if (userSubCategoryJSONResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
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
    public int fullCountOfUserLikesForContentId(int contentId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.LIKED_USER_ID, Constants.ALL, Constants.CONTENT_ID, contentId, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userSubCategoryJSONResponse = new JSONObject(responseData.getData());
                int count = userSubCategoryJSONResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND);
                return count;
            } catch (JSONException j) {
                j.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override
    public List<User> usersWhoLikedContentId(int contentId) {
        String query = String.format("fq=userName:*&q={!join from=likedUserId to=userId}contentId:%s&wt=json", contentId);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONArray userJsonArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<User>>(){}.getType();
                    ArrayList<User> users = gson.fromJson(userJsonArray.toString(),listType);
                    return users;
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

    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
