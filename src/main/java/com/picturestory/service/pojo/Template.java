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
    private float lineSpacingAndr;
    private float boundaryHeightAndr;
    private float boundaryWidthAndr;
    private float lineSpacingIos;
    private float boundaryHeightIos;
    private float boundaryWidthIos;
    private float marginLeft;
    private float marginRight;
    private float marginHeight;

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginHeight() {
        return marginHeight;
    }

    public void setMarginHeight(float marginHeight) {
        this.marginHeight = marginHeight;
    }

    public float getLineSpacingIos() {
        return lineSpacingIos;
    }

    public void setLineSpacingIos(float lineSpacingIos) {
        this.lineSpacingIos = lineSpacingIos;
    }

    public float getBoundaryHeightIos() {
        return boundaryHeightIos;
    }

    public void setBoundaryHeightIos(float boundaryHeightIos) {
        this.boundaryHeightIos = boundaryHeightIos;
    }

    public float getBoundaryWidthIos() {
        return boundaryWidthIos;
    }

    public void setBoundaryWidthIos(float boundaryWidthIos) {
        this.boundaryWidthIos = boundaryWidthIos;
    }

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

    public float getLineSpacingAndr() {
        return lineSpacingAndr;
    }

    public void setLineSpacingAndr(float lineSpacingAndr) {
        this.lineSpacingAndr = lineSpacingAndr;
    }

    public float getBoundaryHeightAndr() {
        return boundaryHeightAndr;
    }

    public void setBoundaryHeightAndr(float boundaryHeightAndr) {
        this.boundaryHeightAndr = boundaryHeightAndr;
    }

    public float getBoundaryWidthAndr() {
        return boundaryWidthAndr;
    }

    public void setBoundaryWidthAndr(float boundaryWidthAndr) {
        this.boundaryWidthAndr = boundaryWidthAndr;
    }
}
