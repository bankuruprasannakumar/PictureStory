package com.picturestory.service.api;

import com.google.gson.Gson;
import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.ICommentUserLikeDao;
import com.picturestory.service.database.dao.IContentUserCommentDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.database.dao.IUserUserDao;
import com.picturestory.service.pojo.CommentUserLikeAssociation;
import com.picturestory.service.pojo.ContentUserCommentAssociation;
import com.picturestory.service.pojo.User;
import com.picturestory.service.pojo.UserUserAssociation;
import com.picturestory.service.request.GetCommentDetailListInBatchRequest;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasha.medhi on 9/25/15.
 */
@Path("/getCommentDetailListWithIndex")
@Produces("application/json")
@Consumes("application/json")
public class GetCommentDetailListInBatch {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentUserCommentDao mContentUserCommentDao;
    private final ICommentUserLikeDao mCommentUserLikeDao;
    private final IUserUserDao mUserUserDao;

    @Inject
    public GetCommentDetailListInBatch(IUserDetailsDao userDetailsDao, IContentUserCommentDao contentUserCommentDao,
                                       ICommentUserLikeDao mCommentUserLikeDao, IUserUserDao mUserUserDao) {
        mUserDetailsDao = userDetailsDao;
        mContentUserCommentDao = contentUserCommentDao;
        this.mCommentUserLikeDao = mCommentUserLikeDao;
        this.mUserUserDao = mUserUserDao;
    }

    @POST
    public Response getCommentDetailListInBatch(GetCommentDetailListInBatchRequest getCommentDetailListInBatchRequest) {
        try {
            if (getCommentDetailListInBatchRequest == null) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!getCommentDetailListInBatchRequest.isValid()) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, getCommentDetailListInBatchRequest.errorMessage());
            }
            int userId = getCommentDetailListInBatchRequest.getUserId();
            if (null == mUserDetailsDao.getUser(userId)) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            int contentId = getCommentDetailListInBatchRequest.getContentId();
            int start = getCommentDetailListInBatchRequest.getStart();
            int end = getCommentDetailListInBatchRequest.getEnd();
            List<ContentUserCommentAssociation> contentUserCommentAssociations = mContentUserCommentDao.getCommentsForContentIdWithIndex(contentId, start, end);
            return ResponseBuilder.successResponse(composeResponse(userId,contentUserCommentAssociations));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private String composeResponse(int userId,List<ContentUserCommentAssociation> commentList) {
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

                    //set comment like count
                    contentJSON.put(Constants.LIKE_COUNT,mCommentUserLikeDao.fullCountOfUserLikesForCommentId(contentUserCommentAssociation.getCommentId()));

                    //set if liked by user
                    CommentUserLikeAssociation commentUserLikeAssociation = new CommentUserLikeAssociation();
                    commentUserLikeAssociation.setCommentId(contentUserCommentAssociation.getCommentId());
                    commentUserLikeAssociation.setCommentLikedUserId(userId);
                    contentJSON.put(Constants.LIKED_BY_USER,mCommentUserLikeDao.isCommentLikedByUser(commentUserLikeAssociation));

                    //set comment liked user list
                    List<User> userList = new ArrayList<User>();
                    userList = mCommentUserLikeDao.usersWhoLikedCommentId(contentUserCommentAssociation.getCommentId());
                    JSONArray userJSONArray = new JSONArray();
                    if(userList!=null)
                            for(User user1:userList){
                                JSONObject userJSON = new JSONObject();
                                userJSON.put(Constants.ID, user1.getUserId());
                                userJSON.put(Constants.NAME, user1.getUserName());
                                userJSON.put(Constants.DESCRIPTION,user1.getUserDesc());
                                userJSON.put(Constants.IMAGE_URL,user1.getUserImage());
                                userJSON.put(Constants.FOLLOWED_BY_USER, isPersonFollowedByUser(userId, user1.getUserId()));
                                userJSONArray.put(userJSON);
                            }
                     contentJSON.put(Constants.LIKED_USER_LIST,userJSONArray);

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

    private boolean isPersonFollowedByUser(int userId, int personId) {
        UserUserAssociation userUserAssociation = new UserUserAssociation();
        userUserAssociation.setUserId(userId);
        userUserAssociation.setFollowedUserId(personId);
        return mUserUserDao.isFollowedByUser(userUserAssociation);
    }
}
