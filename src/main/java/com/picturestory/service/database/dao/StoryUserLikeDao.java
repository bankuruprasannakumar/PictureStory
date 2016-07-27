package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.StoryUserLikeAssocation;
import com.picturestory.service.response.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by krish on 26/07/2016.
 */
public class StoryUserLikeDao implements IStoryUserLikeDao<StoryUserLikeAssocation> {

    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public StoryUserLikeDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addStoryUserLike(StoryUserLikeAssocation storyUserLikeAssocation) {
        if(!isStoryLikedByUser(storyUserLikeAssocation)){
            String query = "";
            try {
                Gson gson = new Gson();
                String contentUserJSON = gson.toJson(storyUserLikeAssocation);
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
    public boolean deleteStoryUserLike(StoryUserLikeAssocation storyUserLikeAssocation) {
        String query = "";
        query = String.format("%s:%s AND %s:%s", Constants.STORY_LIKED_USER_ID, storyUserLikeAssocation.getStoryLikedUserId(), Constants.STORY_ID, storyUserLikeAssocation.getStoryId());
        query = Constants.DELETE_START + query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isStoryLikedByUser(StoryUserLikeAssocation storyUserLikeAssocation) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.STORY_LIKED_USER_ID, storyUserLikeAssocation.getStoryLikedUserId(), Constants.STORY_ID, storyUserLikeAssocation.getStoryId(), Constants.WT_JSON);
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
    public int fullCountOfUserLikesForStoryId(int storyId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.STORY_LIKED_USER_ID, Constants.ALL, Constants.STORY_ID, storyId, Constants.WT_JSON);
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
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }
}
