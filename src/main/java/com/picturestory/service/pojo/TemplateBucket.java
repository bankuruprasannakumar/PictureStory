package com.picturestory.service.pojo;

/**
 * Created by bankuru on 7/12/16.
 */
public class TemplateBucket {
    private int bucketId;
    private String bucketName;
    private String bucketPictureUrl;
    private String bucketThumbnailUrl;

    public String getBucketThumbnailUrl() {
        return bucketThumbnailUrl;
    }

    public void setBucketThumbnailUrl(String bucketThumbnailUrl) {
        this.bucketThumbnailUrl = bucketThumbnailUrl;
    }

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
