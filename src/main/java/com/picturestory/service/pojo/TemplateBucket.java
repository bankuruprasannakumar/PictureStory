package com.picturestory.service.pojo;

/**
 * Created by bankuru on 7/12/16.
 */
public class TemplateBucket {
    private int bucketId;
    private String bucketName;
    private String bucketPictureUrl;

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketPictureUrl() {
        return bucketPictureUrl;
    }

    public void setBucketPictureUrl(String bucketPictureUrl) {
        this.bucketPictureUrl = bucketPictureUrl;
    }
}
