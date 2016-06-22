package com.picturestory.service.pojo;

/**
 * Created by bankuru on 30/12/15.
 */
public class UserGcmIdAssociation {
    private int userId;
    private String gcmId;

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
