package com.picturestory.service.api;

import com.picturestory.service.Constants ;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IContentUserCommentDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.request.AddCommentOnContentRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Random;
/**
 * Created by aasha.medhi on 9/25/15.
 */
@Path("/addComment")
@Produces("application/json")
@Consumes("application/json")
public class AddComment {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserCommentDao mContentUserCommentDao;
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public AddComment(IUserDetailsDao userDetailsDao, IContentUserCommentDao contentUserCommentDao, IContentDetailsDao contentDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserCommentDao = contentUserCommentDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @POST
    public Response addCommentOnContent(AddCommentOnContentRequest commentContentRequest) {
        try {

            if (commentContentRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!commentContentRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, commentContentRequest.errorMessage());
            }
            int userId = commentContentRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = commentContentRequest.getContentId();
            if (null == mContentDetailsDao.getContentDetails(contentId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
            }
            boolean status = false;
            Random rand = new Random();
            int commentId = rand.nextInt(Integer.MAX_VALUE) + 1;
            String comment = commentContentRequest.getComment();
            ContentUserCommentAssociation contentUserCommentAssociation = new ContentUserCommentAssociation();
            contentUserCommentAssociation.setUserId(userId);
            contentUserCommentAssociation.setContentId(contentId);
            contentUserCommentAssociation.setComment(comment);
            contentUserCommentAssociation.setCommentId(commentId);
            contentUserCommentAssociation.setIngestionTime(System.currentTimeMillis());
            status = mContentUserCommentDao.addContentUserComment(contentUserCommentAssociation);
            if (status == false) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentUserCommentDao.getDetailedResponse().getErrorMessage());
            } else {
                return ResponseBuilder.successResponse(composeResponse(commentId).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(int commentId) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.COMMENT_ID, commentId);
        return responseObj;
    }
}
