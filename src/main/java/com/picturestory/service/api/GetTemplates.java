package com.picturestory.service.api;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserTemplateBucketDao;
import com.picturestory.service.database.dao.IWallPaperDetailsDao;
import com.picturestory.service.pojo.*;
import com.picturestory.service.request.GetTemplateRequest;
import com.picturestory.service.request.GetWallPaperRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by bankuru on 24/6/16.
 */
@Path("/getTemplates")
@Produces("application/json")
@Consumes("application/json")

public class GetTemplates {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserTemplateBucketDao mUserTemplateBucketDao;

    @Inject
    public GetTemplates(IUserDetailsDao userDetailsDao, IUserTemplateBucketDao userTemplateBucketDao){
        this.mUserDetailsDao = userDetailsDao;
        this.mUserTemplateBucketDao = userTemplateBucketDao;
    }

    @POST
    public Response getTemplates(GetTemplateRequest getTemplateRequest){
    try {
        if (getTemplateRequest == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!getTemplateRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getTemplateRequest.errorMessage());
        }

        int userId = getTemplateRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        UserTemplateBucketAssociation userTemplateBucketAssociation = (UserTemplateBucketAssociation)mUserTemplateBucketDao.getUserTemplateBucketAssociation(userId);
        List<Integer> userBucketIds = userTemplateBucketAssociation.getUnlockedTemplateBucketIds();
        List<Template> userTemplateList = mUserTemplateBucketDao.getTemplatesForBucketIds(userBucketIds);
        List<TemplateBucket> templateBuckets = mUserTemplateBucketDao.getAllBuckets();
        JSONObject responseObj = composeResponse(userBucketIds, userTemplateList, templateBuckets);
        return ResponseBuilder.successResponse(responseObj.toString());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }
}

    private JSONObject composeResponse(List<Integer> userBucketIds, List<Template> userTemplateList, List<TemplateBucket> templateBuckets) throws JSONException {
        JSONObject responseObj = new JSONObject();
        JSONArray bucketList = new JSONArray();
        for (int i = 0 ; i < templateBuckets.size() ; i++) {
            JSONObject bucketObject = new JSONObject();
            bucketObject.put(Constants.BUCKET_ID, templateBuckets.get(i).getBucketId());
            bucketObject.put(Constants.BUCKET_NAME, templateBuckets.get(i).getBucketName());
            bucketObject.put(Constants.BUCKET_PICTURE_URL, templateBuckets.get(i).getBucketPictureUrl());
            boolean unlockedStatus = userBucketIds.contains(templateBuckets.get(i).getBucketId());
            bucketObject.put(Constants.UNLOCKED_STATUS, unlockedStatus);
            bucketList.put(bucketObject);
        }
        JSONArray templateList = new JSONArray(userTemplateList);
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.BUCKET_LIST, bucketList);
        responseObj.put(Constants.TEMPLATE_LIST, templateList);
        return responseObj;
    }

}
