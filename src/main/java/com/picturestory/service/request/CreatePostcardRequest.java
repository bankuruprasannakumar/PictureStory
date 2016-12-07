package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.util.List;

/**
 * Created by bankuru on 27/8/16.
 */
@XmlRootElement
public class CreatePostcardRequest implements IRequest{

    private InputStream image;
    private String format;
    private String text;
    private int templateId;
    private String location;
    private String postcardUserName;
    private int userId;
    private int contentId;
    private List<String> tags;

    public CreatePostcardRequest(InputStream image, String format, String text, int templateId, String location, String postcardUserName, int userId, int contentId, List<String> tags) {
        this.image = image;
        this.format = format;
        this.text = text;
        this.templateId = templateId;
        this.location = location;
        this.postcardUserName = postcardUserName;
        this.userId = userId;
        this.contentId = contentId;
        this.tags = tags;
    }

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


    @Override
    public boolean isValid() {
        if (image == null) {
            return false;
        }
        if (userId == 0) {
            return false;
        }
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (templateId == 0) {
            return false;
        }
        if (postcardUserName == null || postcardUserName.isEmpty()) {
            return false;
        }
        if (tags == null || tags.isEmpty()) {
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
        if (text == null || text.isEmpty()) {
            msg.append(Constants.INVALID_TEXT);
        }
        if (templateId == 0) {
            msg.append(Constants.INVALID_TEMPLATE_ID);
        }
        if (postcardUserName == null || postcardUserName.isEmpty()) {
            msg.append(Constants.INVALID_POST_CARD_USER_NAME);
        }
        if (tags == null || tags.isEmpty()) {
            msg.append(Constants.INVALID_TAG);
        }

        return msg.toString();
    }
}
