package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;

/**
 * Created by bankuru on 27/8/16.
 */
@XmlRootElement
public class AddContentRequest implements IRequest{

    private InputStream image;
    private String format;
    private String story;
    private String title;
    private String location;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }


    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public AddContentRequest(InputStream image, String format, String story, String title, String location) {
        this.image = image;
        this.format = format;
        this.story = story;
        this.title = title;
        this.location = location;
    }

    @Override
    public boolean isValid() {
        if (image == null) {
            return false;
        }
        if (story == null || "".equals(story.trim())) {
            return false;
        }

        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (image == null) {
            msg.append(Constants.INVALID_IMAGE);        }
        if (story == null || "".equals(story.trim())) {
            msg.append(Constants.INVALID_STORY);
        }
        return msg.toString();
    }
}
