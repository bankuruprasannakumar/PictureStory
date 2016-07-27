package com.picturestory.service.pojo;

/**
 * Created by krish on 26/07/2016.
 */
public class Story {
    private int contentId;
    private int storyId;
    private String storyDescription;
    private int userId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getStoryDesc() {
        return storyDescription;
    }

    public void setStoryDesc(String storyDesc) {
        this.storyDescription = storyDesc;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
