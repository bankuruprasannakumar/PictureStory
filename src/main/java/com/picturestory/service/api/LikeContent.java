package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IContentUserLikeDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.ContentUserLikeAssociation;
import com.picturestory.service.request.LikeContentRequest;
import com.picturestory.service.response.ResponseBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by bankuru on 30/4/16.
 */
@Path("/likeContent")
@Produces("application/json")
@Consumes("application/json")

public class LikeContent {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserLikeDao mUserLikeDao;

    @Inject
    public LikeContent(IUserDetailsDao userDetailsDao, IContentUserLikeDao userLikeDao){
        mUserDetailsDao = userDetailsDao;
        mUserLikeDao = userLikeDao;
    }
    @POST
    public Response followSubCategory(LikeContentRequest likeContentRequest) {
        try {
            if (likeContentRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!likeContentRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, likeContentRequest.errorMessage());
            }
            int userId = likeContentRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            ContentUserLikeAssociation contentUserLike = new ContentUserLikeAssociation();
            contentUserLike.setLikeduserId(userId);
            contentUserLike.setContentId(likeContentRequest.getContentId());
            boolean status = false;
            if(likeContentRequest.getDoLike() == true)
                status = mUserLikeDao.addContentUserLike(contentUserLike);
            else
                status = mUserLikeDao.deleteContentUserLike(contentUserLike);
            if (status == false) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mUserLikeDao.getDetailedResponse().getErrorMessage());
            } else {
                return ResponseBuilder.successResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}
