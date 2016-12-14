package com.picturestory.service.pojo;

import java.util.List;

/**
 * Created by bankuru on 19/9/16.
 */
public class Postcard {
    private int userId;
    private int ContentId;
    private String pictureUrl;
    private int postcardId;
    private String text;
    private int templateId;
    private String location;
    private String postcardUserName;
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostcardUserName() {
        return postcardUserName;
    }

    public void setPostcardUserName(String postcardUserName) {
        this.postcardUserName = postcardUserName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContentId() {
        return ContentId;
    }

    public void setContentId(int contentId) {
        ContentId = contentId;
    }


    public int getPostcardId() {
        return postcardId;
    }

    public void setPostcardId(int postcardId) {
        this.postcardId = postcardId;
    }
}
