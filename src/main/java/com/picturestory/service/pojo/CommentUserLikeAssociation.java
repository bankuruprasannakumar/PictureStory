package com.picturestory.service.pojo;

/**
 * Created by sriram on 2/9/16.
 */
public class CommentUserLikeAssociation {
    private int commentId;
    private int commentLikedUserId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentLikedUserId() {
        return commentLikedUserId;
    }

    public void setCommentLikedUserId(int commentLikedUserId) {
        this.commentLikedUserId = commentLikedUserId;
    }
}
