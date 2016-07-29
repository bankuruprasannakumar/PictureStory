package com.picturestory.service.database.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Configs;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.Story;
import com.picturestory.service.response.ResponseData;
import com.sun.org.apache.regexp.internal.RE;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by krish on 26/07/2016.
 */
public class StoryDetailsDao implements IStoryDetailsDao<Story> {

    private IDataAccessAdapter mSolrAdapter;
    private ResponseData mResponseData;

    @Inject
    public StoryDetailsDao(IDataAccessAdapter solrAdapter) {
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }

    @Override
    public Story getStoryForId(int storyId) {
        String query = String.format("q=%s:%s AND %s:%s&%s", Constants.STORY_ID, storyId, Constants.STORY_DESC, Constants.ALL, Constants.WT_JSON);
        ResponseData responseData = (ResponseData) mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()) {
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if (userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) == 1) {
                    JSONObject contentJsonObject = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS).getJSONObject(0);
                    Gson gson = new Gson();
                    Story story = gson.fromJson(contentJsonObject.toString(), Story.class);
                    mResponseData.setSuccess(true);
                    return story;
                } else {
                    mResponseData.setErrorMessage(Constants.INVALID_STORY_ID);
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
    public List<Story> getStoryListForContent(int contentId) {
        String query = String.format("q=%s:%s AND %s:%s&%s",Constants.CONTENT_ID,contentId,Constants.STORY_ID,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try{
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Story> getStoriesContributedByUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s",Constants.STORY_ID,Constants.ALL,Constants.USER_ID,userId,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Story> getStoriesForIds(List<Integer> storyIdList) {
        String idList = "(";
        if(storyIdList!=null){
        for(int i=0;i<storyIdList.size()-1;i++)
            idList+=storyIdList.get(i)+" OR ";
        idList+=storyIdList.get(storyIdList.size()-1);
        }
        idList+=")";
        String query = String.format("q=%s:%s AND %s:%s&%%s",Constants.STORY_ID,idList,Constants.STORY_DESC,Constants.ALL,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else{
                    mResponseData.setErrorMessage(Constants.INVALID_STORY_ID);
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
    public int addStory(Story story){
        String query = "";
        try {
            Gson gson = new Gson();
            String jsonStory = gson.toJson(story);
            JSONObject storyObject = new JSONObject(jsonStory);
            storyObject.put(Constants.INGESTION_TIME,System.currentTimeMillis());
            Random rand = new Random();
            int currentStoryId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            storyObject.put(Constants.STORY_ID, currentStoryId);
            query = Constants.INSERT_START + storyObject.toString() + Constants.INSERT_END;
            mResponseData = (ResponseData)mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return currentStoryId;
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

    @Override
    public List<Story> getAllStoriesLikedByUser(int userId) {
        String query = String.format("q={!join from=%s to=%s}%s:%s&fq=%s:%s&%s&%s=%s&%s=%s",Constants.STORY_ID,Constants.STORY_ID, Constants.STORY_LIKED_USER_ID,userId,Constants.STORY_DESC,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else {
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Story> getStoriesForContentWithRange(int contentId, int startIndex, int numRows) {
        String query = String.format("q=%s:%s AND %s:%s&%s=%s&%s=%s&%s",Constants.CONTENT_ID,contentId,Constants.STORY_ID,Constants.ALL,Constants.START,startIndex,Constants.ROWS,numRows,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try{
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Story> getAllStoriesLikedByUserForContent(int contentId, int userId) {
        String query = String.format("q={!join from=%s to=%s}%s:%s&fq=%s:%s AND %s:%s&%s&%s=%s&%s=%s",Constants.STORY_ID,Constants.STORY_ID, Constants.STORY_LIKED_USER_ID,userId,Constants.CONTENT_ID,contentId,Constants.STORY_DESC,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else {
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Story> getAllStoriesContributedByUserForContent(int contentId, int userId) {
        String query = String.format("q=%s:%s AND %s:%s AND %s:%s&%s",Constants.STORY_ID,Constants.ALL,Constants.USER_ID,userId,Constants.CONTENT_ID,contentId,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Story> storyList = new ArrayList<Story>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        storyList.add(story);
                    }
                    mResponseData.setSuccess(true);
                    return storyList;
                }else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Story>();
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
    public List<Integer> getContentIdListForStoriesLikedByUser(int userId) {
        String query = String.format("q={!join from=%s to=%s}%s:%s&fq=%s:%s&%s&%s=%s&%s=%s",Constants.STORY_ID,Constants.STORY_ID, Constants.STORY_LIKED_USER_ID,userId,Constants.STORY_DESC,Constants.ALL, Constants.WT_JSON, Constants.START, 0, Constants.ROWS, Configs.MAX_LIMIT);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Integer> contentIdList = new ArrayList<Integer>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        if(contentIdList==null || !contentIdList.contains(story.getContentId()))
                            contentIdList.add(story.getContentId());
                    }
                    mResponseData.setSuccess(true);
                    return contentIdList;
                }else {
                    mResponseData.setSuccess(true);
                    return new ArrayList<Integer>();
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
    public List<Integer> getContentIdListForStoriesContributedByUser(int userId) {
        String query = String.format("q=%s:%s AND %s:%s&%s",Constants.STORY_ID,Constants.ALL,Constants.USER_ID,userId,Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if (responseData.isSuccess()){
            try {
                JSONObject userResponse = new JSONObject(responseData.getData());
                if(userResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND)>0){
                    JSONArray storyJSONArray = userResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    List<Integer> contentIdList = new ArrayList<Integer>();
                    Gson gson = new Gson();
                    for(int i=0;i<storyJSONArray.length();i++){
                        Story story = gson.fromJson(storyJSONArray.get(i).toString(),Story.class);
                        if(contentIdList==null || !contentIdList.contains(story.getContentId()))
                            contentIdList.add(story.getContentId());
                    }
                    mResponseData.setSuccess(true);
                    return contentIdList;
                }else{
                    mResponseData.setSuccess(true);
                    return new ArrayList<Integer>();
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
