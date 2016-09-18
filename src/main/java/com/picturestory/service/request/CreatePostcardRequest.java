package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;

/**
 * Created by bankuru on 27/8/16.
 */
@XmlRootElement
public class CreatePostcardRequest implements IRequest{

    private InputStream image;
    private String format;
    private int userId;
    private int contentId;

    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public CreatePostcardRequest(InputStream image, String format, int userId, int contentId) {
        this.image = image;
        this.format = format;
        this.userId = userId;
        this.contentId = contentId;
    }

    @Override
    public boolean isValid() {
        if (image == null) {
            return false;
        }
        if (userId == 0) {
            return false;
        }

        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (image == null) {
            msg.append(Constants.INVALID_IMAGE);
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        }
        return msg.toString();
    }
}
