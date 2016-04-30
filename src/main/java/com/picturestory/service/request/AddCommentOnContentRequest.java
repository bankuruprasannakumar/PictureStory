package com.picturestory.service.request;


import com.picturestory.service.Constants;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class AddCommentOnContentRequest implements IRequest{
    int userId;
    int contentId;
    String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    @Override
    public boolean isValid() {
        if(userId == 0)
            return false;
        if (contentId == 0)
            return false;
        if(comment == null || "".equals(comment.trim()))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (contentId == 0)
            msg.append(Constants.INVALID_CONTENT_ID);
        if(comment == null || "".equals(comment.trim()))
            msg.append(Constants.INVALID_COMMENT);
        return msg.toString();
    }
}
