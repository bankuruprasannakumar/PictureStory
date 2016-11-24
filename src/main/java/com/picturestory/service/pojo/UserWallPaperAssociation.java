package com.picturestory.service.pojo;

import java.util.List;

/**
 * Created by bankuru on 24/11/16.
 */
public class UserWallPaperAssociation {
    int userId;
    List<Integer> wallPaperContnetIds;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getWallPaperContnetIds() {
        return wallPaperContnetIds;
    }

    public void setWallPaperContnetIds(List<Integer> wallPaperContnetIds) {
        this.wallPaperContnetIds = wallPaperContnetIds;
    }
}
