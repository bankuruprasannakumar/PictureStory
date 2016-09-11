package com.picturestory.service.pojo;

/**
 * Created by bankuru on 10/9/16.
 */
public class ContestContent {
    private String pictureUrl;
    private String story;
    private String title;
    private String location;
    private String userName;
    private String userEmail;
    private String mobileNumber;
    private String webSite;
    private String instagram;
    private String contestCategoryId;
    private int contestId;

    public ContestContent(String pictureUrl, String story, String title, String location, String userName, String userEmail, String mobileNumber, String webSite, String instagram, String category) {
        this.pictureUrl = pictureUrl;
        this.story = story;
        this.title = title;
        this.location = location;
        this.userName = userName;
        this.userEmail = userEmail;
        this.mobileNumber = mobileNumber;
        this.webSite = webSite;
        this.instagram = instagram;
        this.contestCategoryId = category;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getContestCategoryId() {
        return contestCategoryId;
    }

    public void setContestCategoryId(String contestCategoryId) {
        this.contestCategoryId = contestCategoryId;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }
}
