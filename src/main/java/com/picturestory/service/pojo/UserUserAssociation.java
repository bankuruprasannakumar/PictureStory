package com.picturestory.service.pojo;

/**
 * Created by bankuru on 1/2/16.
 */
public class UserUserAssociation {
    private int userId;
    private int followedUserId;

    public int getFollowedUserId() {
        return followedUserId;
    }

    public void setFollowedUserId(int mFollowedUserId) {
        this.followedUserId = mFollowedUserId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int mUserId) {
        this.userId = mUserId;
    }

}
