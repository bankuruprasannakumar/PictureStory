package com.picturestory.service.pojo;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentUserCommentAssociation {
    private int userId;
    private int contentId;
    private int commentId;
    private String comment;
    private long ingestionTime;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public void setIngestionTime(long injestionTime) {
        this.ingestionTime = injestionTime;
    }

    public long getIngestionTime() {
        return ingestionTime;
    }

}
