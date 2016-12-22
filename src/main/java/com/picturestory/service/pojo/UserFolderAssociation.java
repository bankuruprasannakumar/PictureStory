package com.picturestory.service.pojo;

/**
 * Created by bankuru on 22/12/16.
 */
public class UserFolderAssociation {
    private int userId;
    private String folderName;
    private int folderImageId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderImageId() {
        return folderImageId;
    }

    public void setFolderImageId(int folderImageId) {
        this.folderImageId = folderImageId;
    }
}
