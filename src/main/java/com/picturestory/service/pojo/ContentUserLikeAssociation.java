package com.picturestory.service.pojo;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class ContentUserLikeAssociation {
    private int likedUserId;
    private int contentId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getLikeduserId() {
        return likedUserId;
    }

    public void setLikeduserId(int likeduserId) {
        this.likedUserId = likeduserId;
    }
}
