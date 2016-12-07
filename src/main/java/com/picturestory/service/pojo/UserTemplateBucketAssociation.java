package com.picturestory.service.pojo;

import java.util.List;

/**
 * Created by bankuru on 3/11/16.
 */
public class UserTemplateBucketAssociation {
    private int userId;
    private List<Integer> unlockedTemplateBucketIds;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getUnlockedTemplateBucketIds() {
        return unlockedTemplateBucketIds;
    }

    public void setUnlockedTemplateBucketIds(List<Integer> unlockedTemplateBucketIds) {
        this.unlockedTemplateBucketIds = unlockedTemplateBucketIds;
    }
}
