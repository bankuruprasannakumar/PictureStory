package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.CommentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sriram on 2/9/16.
 */
public class CommentUserLikeDao implements ICommentUserLikeDao<CommentUserLikeAssociation> {

    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public CommentUserLikeDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addCommentUserLike(CommentUserLikeAssociation commentUserAssociation) {
        if(!isCommentLikedByUser(commentUserAssociation)){
            String query = "";
            try {
                Gson gson = new Gson();
                String commentUserJSON = gson.toJson(commentUserAssociation);
                query = Constants.INSERT_START + commentUserJSON + Constants.INSERT_END;
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
    public boolean deleteCommentUserLike(CommentUserLikeAssociation commentUserAssociation) {
        String query = "";
        query = String.format("%s:%s AND %s:%s", Constants.COMMENT_LIKED_USER_ID, commentUserAssociation.getCommentLikedUserId(), Constants.COMMENT_ID, commentUserAssociation.getCommentId());
        query = Constants.DELETE_START + query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCommentLikedByUser(CommentUserLikeAssociation commentUserAssociation) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.COMMENT_LIKED_USER_ID, commentUserAssociation.getCommentLikedUserId(), Constants.COMMENT_ID, commentUserAssociation.getCommentId(), Constants.WT_JSON);
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
    public int fullCountOfUserLikesForCommentId(int commentId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.COMMENT_LIKED_USER_ID, Constants.ALL, Constants.COMMENT_ID, commentId, Constants.WT_JSON);
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
    public List<User> usersWhoLikedCommentId(int commentId) {
        String query = String.format("fq=userName:*&q={!join from=commentLikedUserId to=userId}commentId:%s&rows=100&wt=json", commentId);
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

