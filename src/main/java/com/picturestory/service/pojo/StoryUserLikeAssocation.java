package com.picturestory.service.pojo;

/**
 * Created by krish on 26/07/2016.
 */
public class StoryUserLikeAssocation {
    private int storyId;
    private int storyLikedUserId;

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getStoryLikedUserId() {
        return storyLikedUserId;
    }

    public void setStoryLikedUserId(int storyLikedUserId) {
        this.storyLikedUserId = storyLikedUserId;
    }
}
