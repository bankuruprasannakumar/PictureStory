package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sriram on 2/9/16.
 */
@XmlRootElement
public class LikeCommentRequest implements IRequest{

    int userId;
    int commentId;
    private String doLike;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public boolean getDoLikeValue() {
        if(doLike != null){
            if(doLike.equals("true") || doLike.equals("yes") || doLike.equals("1"))
                return true;
        }
        return false;
    }

    public String getDoLike() {
        return doLike;
    }

    public void setDoLike(String doLike){
        this.doLike = doLike;
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
        if (commentId == 0)
            return false;
        if (doLike == null || doLike.trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (commentId == 0)
            msg.append(Constants.INVALID_COMMENT_ID);
        if (doLike == null || doLike.trim().isEmpty())
            msg.append(Constants.INVALID_DO_LIKE);
        return msg.toString();
    }

}

