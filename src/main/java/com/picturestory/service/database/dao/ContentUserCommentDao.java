package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.response.ResponseData ;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentUserCommentDao implements IContentUserCommentDao<ContentUserCommentAssociation> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public ContentUserCommentDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public boolean addContentUserComment(ContentUserCommentAssociation contentUserCommentAssociation) {
        String query = "";
        try {
            Gson gson = new Gson();
            String contentUserJSON = gson.toJson(contentUserCommentAssociation);
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

    @Override
    public boolean deleteContentUserComment(ContentUserCommentAssociation contentUserCommentAssociation) {
        String query = "";
        query = String.format("%s:%s AND %s:%s AND %s:%s", Constants.USER_ID, contentUserCommentAssociation.getUserId(), Constants.CONTENT_ID, contentUserCommentAssociation.getContentId(), Constants.COMMENT_ID, contentUserCommentAssociation.getCommentId());
        query = Constants.DELETE_START + query + Constants.DELETE_END;
        mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
        if (mResponseData.isSuccess()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isContentCommentedByUser(ContentUserCommentAssociation contentUserCommentAssociation) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s", Constants.USER_ID, contentUserCommentAssociation.getUserId(), Constants.CONTENT_ID, contentUserCommentAssociation.getContentId(), Constants.COMMENT, Constants.ALL, Constants.WT_JSON);
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
    public int fullCountOfUserCommentsForContentId(int contentId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s", Constants.USER_ID, Constants.ALL, Constants.CONTENT_ID, contentId, Constants.COMMENT, Constants.ALL, Constants.WT_JSON);
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
    public List<ContentUserCommentAssociation> getAllCommentsForContentId(int contentId) {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s&sort=ingestionTime+desc", Constants.CONTENT_ID, contentId, Constants.COMMENT, Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ContentUserCommentAssociation>>(){}.getType();
                    List<ContentUserCommentAssociation> contentUserCommentList = gson.fromJson(contentArray.toString(), listType);
                    return contentUserCommentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return null;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        mResponseData = responseData;
        return null;
    }

    @Override
    public List<ContentUserCommentAssociation> getCommentsForContentIdWithIndex(int contentId, int start ,int end) {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s&sort=ingestionTime+desc", Constants.CONTENT_ID, contentId, Constants.COMMENT, Constants.ALL, Constants.WT_JSON, Constants.START, start, Constants.ROWS, end);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ContentUserCommentAssociation>>(){}.getType();
                    List<ContentUserCommentAssociation> contentUserCommentList = gson.fromJson(contentArray.toString(), listType);
                    return contentUserCommentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return null;
                }
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return null;
            }
        }
        mResponseData = responseData;
        return null;
    }

    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
