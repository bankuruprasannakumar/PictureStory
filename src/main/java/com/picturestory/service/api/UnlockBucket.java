package com.picturestory.service.api;

import com.google.inject.Inject;
import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserTemplateBucketDao;
import com.picturestory.service.pojo.Template;
import com.picturestory.service.pojo.TemplateBucket;
import com.picturestory.service.pojo.UserTemplateBucketAssociation;
import com.picturestory.service.request.GetTemplateRequest;
import com.picturestory.service.request.UnlockTemplateBucketsRequest;
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
@Path("/unlockBucket")
@Produces("application/json")
@Consumes("application/json")

public class UnlockBucket {
    private final IUserDetailsDao mUserDetailsDao;
    private final IUserTemplateBucketDao mUserTemplateBucketDao;

    @Inject
    public UnlockBucket(IUserDetailsDao userDetailsDao, IUserTemplateBucketDao userTemplateBucketDao){
        this.mUserDetailsDao = userDetailsDao;
        this.mUserTemplateBucketDao = userTemplateBucketDao;
    }

    @POST
    public Response getTemplates(UnlockTemplateBucketsRequest unlockTemplateBucketsRequest){
    try {
        if (unlockTemplateBucketsRequest == null){
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
        }
        if (!unlockTemplateBucketsRequest.isValid()) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, unlockTemplateBucketsRequest.errorMessage());
        }

        int userId = unlockTemplateBucketsRequest.getUserId();
        if (null == mUserDetailsDao.getUser(userId)) {
            return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
        }
        mUserTemplateBucketDao.unlockUserTemplateBucket(userId, unlockTemplateBucketsRequest.getBucketId());
        return ResponseBuilder.successResponse();
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
    }
}
}
