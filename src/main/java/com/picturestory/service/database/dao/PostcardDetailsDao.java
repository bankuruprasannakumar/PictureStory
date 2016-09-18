package com.picturestory.service.database.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.pojo.Category;
import com.picturestory.service.pojo.Postcard;
import com.picturestory.service.response.ResponseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by bankuru on 24/6/16.
 */
public class PostcardDetailsDao implements IPostcardDetailsDao<Postcard> {

    private ResponseData mResponseData;
    private IDataAccessAdapter mSolrAdapter;

    @Inject
    public PostcardDetailsDao(IDataAccessAdapter solrAdapter){
        mSolrAdapter = solrAdapter;
        mResponseData = new ResponseData();
    }


    @Override
    public List<Postcard> getAllPostCardsOfUser(int userId) {
        String query = "";
        query = String.format("q=%s:%s AND %s:%s&%s", Constants.POSTCARD_ID,Constants.ALL, Constants.USER_ID, userId, Constants.WT_JSON);
        ResponseData responseData = (ResponseData)mSolrAdapter.selectRequest(query);
        if(responseData.isSuccess()){
            try {
                JSONObject categoryResponse = new JSONObject(responseData.getData());
                if (categoryResponse.getJSONObject(Constants.RESPONSE).getInt(Constants.NUMFOUND) > 0) {
                    JSONArray postcardJSONArray = categoryResponse.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.DOCS);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Postcard>>(){}.getType();
                    ArrayList<Postcard> postcards = gson.fromJson(postcardJSONArray.toString(),listType);
                    return postcards;
                }
                else {
                    return new ArrayList<Postcard>();
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
    public int createPostCard(Postcard postcard) {
        String query = "";
        try {
            Gson gson = new Gson();
            Random rand = new Random();
            int currentPostcardId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            postcard.setPostcardId(currentPostcardId);
            String contentJSON = gson.toJson(postcard);
            query = Constants.INSERT_START + contentJSON + Constants.INSERT_END;
            mResponseData = (ResponseData) mSolrAdapter.updateRequest(query);
            if (mResponseData.isSuccess()) {
                return currentPostcardId;
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
    public ResponseData getDetailedResponse(){
        return mResponseData;
    }

}
