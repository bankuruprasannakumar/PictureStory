package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.GetSetId;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetContentByCategoryRequest;
import com.picturestory.service.request.GetContentByIdRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by krish on 04/07/2016.
 */
@Path("/getContentById")
@Produces("application/json")
@Consumes("application/json")
public class GetContentById {
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public GetContentById(IContentDetailsDao mContentDetailsDao) {
        this.mContentDetailsDao = mContentDetailsDao;
    }

    @POST
    public Response getContentById(GetContentByIdRequest getContentByIdRequest) {
        try {
            if (getContentByIdRequest == null)
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            if (!getContentByIdRequest.isValid())
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getContentByIdRequest.errorMessage());
            int contentId = getContentByIdRequest.getContentId();
            Content content = (Content) mContentDetailsDao.getContentDetails(contentId);
            if (content != null) {
                JSONObject responseObj = composeResponse(content);
                return ResponseBuilder.successResponse(responseObj.toString());
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentDetailsDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal server error");
        }
    }

    private JSONObject composeResponse(Content content) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);

            JSONObject contentJSON = new JSONObject();
            contentJSON.put(Constants.ID, content.getContentId());
            contentJSON.put(Constants.NAME, content.getName());
            contentJSON.put(Constants.PICTURE_URL,content.getPictureUrl());
            contentJSON.put(Constants.PLACE,content.getPlace());
            contentJSON.put(Constants.DATE,content.getDate());
            contentJSON.put(Constants.PICTURE_DESCRIPTION,content.getPictureDescription());
            contentJSON.put(Constants.PICTURE_SUMMARY,content.getPictureSummary());
            contentJSON.put(Constants.EDITORS_PICK,content.isEditorsPick());

            response.put(Constants.CONTENT, contentJSON);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}

