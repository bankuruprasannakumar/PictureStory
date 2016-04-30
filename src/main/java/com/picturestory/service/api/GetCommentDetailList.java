package com.picturestory.service.api;

import com.google.gson.Gson;
import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentUserCommentDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.GetCommentDetailListRequest;
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
 * Created by aasha.medhi on 9/25/15.
 */
@Path("/getCommentDetailList")
@Produces("application/json")
@Consumes("application/json")
public class GetCommentDetailList {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserCommentDao mContentUserCommentDao;

    @Inject
    public GetCommentDetailList(IUserDetailsDao userDetailsDao, IContentUserCommentDao contentUserCommentDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserCommentDao = contentUserCommentDao;
    }

    @POST
    public Response getCommentDetailList(GetCommentDetailListRequest getCommentDetailListRequest) {
        try {
            if (getCommentDetailListRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getCommentDetailListRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getCommentDetailListRequest.errorMessage());
            }
            int userId = getCommentDetailListRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = getCommentDetailListRequest.getContentId();
            List<ContentUserCommentAssociation> contentUserCommentAssociations = mContentUserCommentDao.getAllCommentsForContentId(contentId);
            return ResponseBuilder.successResponse(composeResponse(contentUserCommentAssociations));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private String composeResponse(List<ContentUserCommentAssociation> commentList) {
        JSONObject response = new JSONObject();
        try {
            response.put(Constants.SUCCESS, true);
            JSONArray contentUserCommentList = new JSONArray();
            if (null != commentList) {
                for(int index = 0; index < commentList.size(); index++) {
                    ContentUserCommentAssociation contentUserCommentAssociation = commentList.get(index);
                    Gson gson = new Gson();
                    JSONObject contentJSON = new JSONObject(gson.toJson(contentUserCommentAssociation));
                    JSONObject contentCreatorJSON = new JSONObject();
                    User user = (User) mUserDetailsDao.getUser(contentUserCommentAssociation.getUserId());
                    if (user != null) {
                        contentCreatorJSON.put(Constants.NAME, user.getUserName());
                        contentCreatorJSON.put(Constants.IMAGE_URL, user.getUserImage());
                        contentCreatorJSON.put(Constants.DESCRIPTION, user.getUserDesc());
                        contentJSON.put(Constants.PERSON_DETAILS, contentCreatorJSON);
                    }
                    contentUserCommentList.put(contentJSON);
                }
            }
            response.put(Constants.COMMENT_LIST, contentUserCommentList);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response.toString();
    }
}
