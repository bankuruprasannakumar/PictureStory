package com.picturestory.service.api;


import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IContentUserCommentDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.request.DeleteCommentOnContentRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by aasha.medhi on 9/25/15.
 */
@Path("/deleteComment")
@Produces("application/json")
@Consumes("application/json")
public class DeleteComment {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserCommentDao mContentUserCommentDao;
    private final IContentDetailsDao mContentDetailsDao;
    @Inject
    public DeleteComment(IUserDetailsDao userDetailsDao, IContentUserCommentDao contentUserCommentDao, IContentDetailsDao contentDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserCommentDao = contentUserCommentDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @POST
    public Response deleteCommentOnContent(DeleteCommentOnContentRequest deleteCommentOnContentRequest) {
        try {
            if(deleteCommentOnContentRequest == null){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if(!deleteCommentOnContentRequest.isValid())
            {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, deleteCommentOnContentRequest.errorMessage());
            }
            int userId = deleteCommentOnContentRequest.getUserId();
            if(null == mUserDetailsDao.getUser(userId)){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = deleteCommentOnContentRequest.getContentId();
            if(null == mContentDetailsDao.getContentDetails(contentId)){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
            }
            boolean status = false;
            ContentUserCommentAssociation contentUserCommentAssociation = new ContentUserCommentAssociation();
            contentUserCommentAssociation.setUserId(userId);
            contentUserCommentAssociation.setContentId(contentId);
            contentUserCommentAssociation.setCommentId(deleteCommentOnContentRequest.getCommentId());
            status = mContentUserCommentDao.deleteContentUserComment(contentUserCommentAssociation);

            if(status == false){
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentUserCommentDao.getDetailedResponse().getErrorMessage());
            }else{
                return ResponseBuilder.successResponse();
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}
