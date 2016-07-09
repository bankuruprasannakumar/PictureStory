package com.picturestory.service.pojo;

/**
 * Created by bankuru on 10/7/16.
 */
public class SharedIdContentIdAssociation {
    int contentId;
    int sharedContentId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getSharedContentId() {
        return sharedContentId;
    }

    public void setSharedContentId(int sharedContentId) {
        this.sharedContentId = sharedContentId;
    }
}
