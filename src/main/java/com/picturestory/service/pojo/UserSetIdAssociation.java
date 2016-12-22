package com.picturestory.service.pojo;

import java.util.List;

/**
 * Created by bankuru on 22/12/16.
 */
public class UserSetIdAssociation {
    private int userId;
    private List<Integer> userFinishedSetIds;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getUserFinishedSetIds() {
        return userFinishedSetIds;
    }

    public void setUserFinishedSetIds(List<Integer> userFinishedSetIds) {
        this.userFinishedSetIds = userFinishedSetIds;
    }
}
