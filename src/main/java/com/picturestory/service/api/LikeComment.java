package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.ICommentUserLikeDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.CommentUserLikeAssociation;
import com.picturestory.service.request.LikeCommentRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by sriram on 2/9/16.
 */
@Path("/likeComment")
@Produces("application/json")
@Consumes("application/json")

public class LikeComment {
    private final IUserDetailsDao mUserDetailsDao;
    private final ICommentUserLikeDao mUserLikeDao;

    @Inject
    public LikeComment(IUserDetailsDao userDetailsDao, ICommentUserLikeDao userLikeDao){
        mUserDetailsDao = userDetailsDao;
        mUserLikeDao = userLikeDao;
    }
    @POST
    public Response likeComment(LikeCommentRequest likeCommentRequest) {
        try {
            if (likeCommentRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!likeCommentRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, likeCommentRequest.errorMessage());
            }
            int userId = likeCommentRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            CommentUserLikeAssociation commentUserLike = new CommentUserLikeAssociation();
            commentUserLike.setCommentLikedUserId(userId);
            commentUserLike.setCommentId(likeCommentRequest.getCommentId());
            boolean status = false;
            if(likeCommentRequest.getDoLikeValue())
                status = mUserLikeDao.addCommentUserLike(commentUserLike);
            else
                status = mUserLikeDao.deleteCommentUserLike(commentUserLike);
            if (status) {
                return ResponseBuilder.successResponse();
            } else {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserLikeDao.getDetailedResponse().getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}

