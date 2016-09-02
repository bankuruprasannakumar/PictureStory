package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IContentUserCommentDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.WebAddCommentOnContentRequest;
import com.picturestory.service.response.WebResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Random;

/**
 * Created by bankuru on 2/9/16.
 */
@Path("/contributor/comment")
@Produces("application/json")
@Consumes("application/json")

public class webAddComment {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IContentUserCommentDao mContentUserCommentDao;

    @Inject
    public webAddComment(IUserDetailsDao userDetailsDao, IContentUserCommentDao contentUserCommentDao, IContentDetailsDao contentDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserCommentDao = contentUserCommentDao;
        mContentDetailsDao = contentDetailsDao;
    }

    @GET
    public Response getContentDetailList(@CookieParam("cookieId") String cookieId,
                                         WebAddCommentOnContentRequest webAddCommentOnContentRequest) {
        try {
            if (cookieId == null) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            int userId = mUserDetailsDao.isCookiePresent(cookieId);
            if (userId == 0) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            User user = (User) mUserDetailsDao.getUser(userId);
            if (null == user) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = webAddCommentOnContentRequest.getContentId();
            String comment = webAddCommentOnContentRequest.getComment();

            if (null == mContentDetailsDao.getContentDetails(contentId)) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
            }
            boolean status = false;
            Random rand = new Random();
            int commentId = rand.nextInt(Integer.MAX_VALUE) + 1;
            ContentUserCommentAssociation contentUserCommentAssociation = new ContentUserCommentAssociation();
            contentUserCommentAssociation.setUserId(userId);
            contentUserCommentAssociation.setContentId(contentId);
            contentUserCommentAssociation.setComment(comment);
            contentUserCommentAssociation.setCommentId(commentId);
            contentUserCommentAssociation.setIngestionTime(System.currentTimeMillis());
            status = mContentUserCommentDao.addContentUserComment(contentUserCommentAssociation);
            if (status == false) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentUserCommentDao.getDetailedResponse().getErrorMessage());
            } else {
                return WebResponseBuilder.successResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}

