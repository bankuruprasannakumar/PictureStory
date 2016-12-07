package com.picturestory.service.pojo;

/**
 * Created by bankuru on 7/12/16.
 */
public class Template {
    private int templateId;
    private String pictureUrl;
    private int bucketId;
    private String fontName;
    private String fontColor;
    private float fontSize;
    private float lineSpacing;
    private float boundaryHeight;
    private float boundaryWidth;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getBoundaryHeight() {
        return boundaryHeight;
    }

    public void setBoundaryHeight(float boundaryHeight) {
        this.boundaryHeight = boundaryHeight;
    }

    public float getBoundaryWidth() {
        return boundaryWidth;
    }

    public void setBoundaryWidth(float boundaryWidth) {
        this.boundaryWidth = boundaryWidth;
    }
}
