package com.picturestory.service.pojo;

/**
 * Created by krish on 02/07/2016.
 */
public class ContentCategoryAssociation {
    private int contentId;
    private int categoryId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
