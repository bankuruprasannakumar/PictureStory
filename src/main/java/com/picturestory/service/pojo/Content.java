package com.picturestory.service.pojo;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class Content {
    private int contentId;
    private int userId;
    private String pictureUrl;
    private String pictureDescription;
    private String ingestionTime;
    private String name;
    private String place;
    private String date;
    private String pictureSummary;
    private boolean editorsPick;
    private long wallpaperEndTime;



    public long getWallpaperEndTime() {
        return wallpaperEndTime;
    }

    public void setWallpaperEndTime(long wallpaperEndTime) {
        this.wallpaperEndTime = wallpaperEndTime;
    }


    public String getPictureSummary() {
        return pictureSummary;
    }

    public void setPictureSummary(String pictureSummary) {
        this.pictureSummary = pictureSummary;
    }

    public boolean isEditorsPick() {
        return editorsPick;
    }

    public void setEditorsPick(boolean editorsPick) {
        this.editorsPick = editorsPick;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureDescription() {
        return pictureDescription;
    }

    public void setPictureDescription(String pictureDescription) {
        this.pictureDescription = pictureDescription;
    }

    public String getIngestionTime() {
        return ingestionTime;
    }

    public void setIngestionTime(String ingestionTime) {
        this.ingestionTime = ingestionTime;
    }
}
