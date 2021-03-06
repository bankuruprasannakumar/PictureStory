package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs ;
import com.picturestory.service.Constants ;
import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.database.dao.Utils.UnionAndIntersionHelper;
import com.picturestory.service.pojo.*;
import com.picturestory.service.response.ResponseData ;
import com.picturestory.service.pojo.Content;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentDetailsDao implements IContentDetailsDao<Content> {
    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public ContentDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public int addContent(Content content) {
        String query = "";
        try {
            Gson gson = new Gson();
            Random rand = new Random();
            int currentContentId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            content.setContentId(currentContentId);
            String contentJSON = gson.toJson(content);
            query = Constants.INSERT_START + contentJSON + Constants.INSERT_END;
            mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return currentContentId;
            }
        } catch (Exception j) {
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return 0;
        }
        mResponseData.setSuccess(true);
        return 0;
    }

    @Override
    public int addContestContent(ContestContent contestContent) {
        String query = "";
        try {
            Gson gson = new Gson();
            Random rand = new Random();
            int currentContestId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            contestContent.setContestId(currentContestId);
            String contentJSON = gson.toJson(contestContent);
            query = Constants.INSERT_START + contentJSON + Constants.INSERT_END;
            mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return currentContestId;
            }
        } catch (Exception j) {
            j.printStackTrace();
            mResponseData.setErrorMessage(j.toString());
            mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
            mResponseData.setSuccess(false);
            return 0;
        }
        mResponseData.setSuccess(true);
        return 0;
    }

    @Override
    public Content getContentDetails(int id) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.CONTENT_ID, id, Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject contentJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Content content = gson.fromJson(contentJsonObject.toString(), Content.class);
                    return content;
                } else {
                    mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
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
        return null;
    }

    @Override
    public Content getContentDetailsForUser(int contentId, int userId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s", Constants.CONTENT_ID, contentId, Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.USER_ID, userId, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject contentJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Content content = gson.fromJson(contentJsonObject.toString(), Content.class);
                    return content;
                } else {
                    mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
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
        return null;
    }

    @Override
    public List<Content> getAllContentDetailsContributedByUserId(int userId, int pixtoryStatus) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s&%s=%s&%s=%s",Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.USER_ID, userId, Constants.PIXTORY_STATUS, pixtoryStatus,Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Content> getAllContentDetailsContributedByUserId(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s",Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.USER_ID, userId, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Content> getAllContentDetailsLikedByUser(int userId) {
        String query = String.format("q={!join from=%s to=%s}%s:%s&fq=%s:%s&%s&%s=%s&%s=%s",Constants.CONTENT_ID,Constants.CONTENT_ID, Constants.LIKED_USER_ID,userId,Constants.PICTURE_URL,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Integer> getAllContentIdsLikedByUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.LIKED_USER_ID, userId, Constants.CONTENT_ID, Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS,Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    List<Integer> contentIdList = new ArrayList<Integer>();
                    mResponseData.setSuccess(true);
                    JSONArray contentLikedUserJSON = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ContentUserLikeAssociation>>(){}.getType();
                    List<ContentUserLikeAssociation> contentUserAssociationList = gson.fromJson(contentLikedUserJSON.toString(), listType);
                    for (int i = 0; i < contentUserAssociationList.size(); i++){
                        contentIdList.add(contentUserAssociationList.get(i).getContentId());
                    }
                    return contentIdList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Integer>();
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
    public List<Integer> getAllContentIdsCommentedByUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.USER_ID, userId, Constants.COMMENT_ID, Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS,Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    List<Integer> contentIdList = new ArrayList<Integer>();
                    mResponseData.setSuccess(true);
                    JSONArray contentCommentUserJSON = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ContentUserCommentAssociation>>(){}.getType();
                    List<ContentUserCommentAssociation> contentCommentUserAssociationList = gson.fromJson(contentCommentUserJSON.toString(), listType);
                    for (int i = 0; i < contentCommentUserAssociationList.size(); i++){
                        contentIdList.add(contentCommentUserAssociationList.get(i).getContentId());
                    }
                    return contentIdList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Integer>();
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
    public List<Content> getAllContentCommentedAndLikedByUser(int userId) {
        List<Integer> contentIdsLikedByUser = getAllContentIdsLikedByUser(userId);
        List<Integer> contentIdsCommentedByUser = getAllContentIdsCommentedByUser(userId);
        List<Integer> contentIdsCommentedAndLikedByUser;
        if (contentIdsCommentedByUser == null && contentIdsLikedByUser == null) {
            return null;
        }
        if (contentIdsCommentedByUser == null) {
            contentIdsCommentedAndLikedByUser = contentIdsLikedByUser;
        }
        else if (contentIdsLikedByUser == null){
            contentIdsCommentedAndLikedByUser = contentIdsCommentedByUser;
        } else {
            contentIdsCommentedAndLikedByUser = UnionAndIntersionHelper.union(contentIdsLikedByUser, contentIdsCommentedByUser);
        }
        List<Content> contentList = getAllContentDetailsForIds(contentIdsCommentedAndLikedByUser);
        return contentList;
    }

    @Override
    public List<Content> getAllContentDetailsForIds(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            mResponseData.setSuccess(false);
            mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
            return new ArrayList<Content>();
        }
        String subQuery = "";
        subQuery = "(";
        for (int index = 0; index < ids.size(); index++) {
            subQuery += ids.get(index) + " OR ";
        }
        subQuery = subQuery.substring(0, (subQuery.length() - 3));
        subQuery += ")";
        String query = String.format("q=%s:%s AND %s:%s&%s&%s=%s&%s=%s", Constants.CONTENT_ID, subQuery.toString(), Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
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
    public List<Content> getAllContentDetails() {
        String query = String.format("q=%s:%s&%s&%s=%s&%s=%s",Constants.PICTURE_DESCRIPTION,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Content> getAllContentDetailsForSet(long setId) {
        String query = String.format("q=%s:%s&%s&%s=%s",Constants.SET_ID,setId, Constants.WT_JSON, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Content> getAllContentDetailsTillSetId(long setId) {
        int totalNumberOfContents = getNumberOfContentsTillSetId(setId);
        if (totalNumberOfContents == 0) {
            return new ArrayList<Content>();
        }
        int startRow = 0;
        if (totalNumberOfContents > 100) {
            startRow = totalNumberOfContents - 100;
        }
        String query = String.format("q=%s:[%s TO %s]&%s=%s&%s=%s&%s&sort=setId asc",Constants.SET_ID,Constants.ALL, setId, Constants.START, startRow,Constants.ROWS,Configs.MAX_LIMIT, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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

    private int getNumberOfContentsTillSetId(long setId){
        String query = String.format("q=%s:[%s TO %s]&%s",Constants.SET_ID, Constants.ALL, setId, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                return responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND);
            } catch (JSONException j) {
                j.printStackTrace();
                mResponseData.setErrorMessage(j.toString());
                mResponseData.setErrorCode(Constants.ERRORCODE_JSON_EXCEPTION);
                mResponseData.setSuccess(false);
                return 0;
            }
        }
        mResponseData = responseData;
        return 0;
    }



    @Override
    public List<Content> getAllContentDetailsContributedByUserIdTillSetId(int userId, long setId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:[%s TO %s]&%s&%s=%s&%s=%s",Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.USER_ID,userId,Constants.SET_ID,Constants.ALL,setId, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
                }
                else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Content>();
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
    public List<Content> getAllContentDetailsForIdsTillSetId(List<Integer> ids, long setId) {
        if(ids == null || ids.isEmpty()){
            mResponseData.setSuccess(false);
            mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
            return new ArrayList<Content>();
        }
        String subQuery = "";
        subQuery = "(";
        for (int index = 0; index < ids.size(); index++) {
            subQuery += ids.get(index) + " OR ";
        }
        subQuery = subQuery.substring(0, (subQuery.length() - 3));
        subQuery += ")";
        String query = String.format("q=%s:%s AND %s:%s AND %s:[* TO %s]&%s&%s=%s&%s=%s&sort=%s desc", Constants.CONTENT_ID, subQuery.toString(), Constants.PICTURE_DESCRIPTION, Constants.ALL,Constants.SET_ID,setId, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT, Constants.SET_ID);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject responseJSONObject = new JSONObject(responseData.getData());
                if (responseJSONObject.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    mResponseData.setSuccess(true);
                    JSONArray contentArray = responseJSONObject.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>(){}.getType();
                    List<Content> contentList = gson.fromJson(contentArray.toString(), listType);
                    return contentList;
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
    public String getImageForSetId(long setId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s", Constants.CONTENT_ID, Constants.ALL, Constants.PICTURE_DESCRIPTION, Constants.ALL, Constants.SET_ID, setId, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONObject contentJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Content content = gson.fromJson(contentJsonObject.toString(), Content.class);
                    return content.getPictureUrl();
                } else {
                    mResponseData.setErrorMessage(Constants.INVALID_CONTENT_ID);
                    mResponseData.setErrorCode(Constants.ERRORCODE_INVALID_INPUT);
                    mResponseData.setSuccess(false);
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
        return null;
    }

    @Override
    public ResponseData getDetailedResponse() {
        return mResponseData;
    }

}
