package com.picturestory.service.pojo;

/**
 * Created by bankuru on 19/9/16.
 */
public class Postcard {
    private int userid;
    private int ContentId;
    private String pictureUrl;
    private int postcardId;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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
