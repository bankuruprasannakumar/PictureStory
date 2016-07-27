package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 26/07/2016.
 */
@XmlRootElement
public class AddNewStoryRequest implements IRequest {
    int contentId;
    int userId;
    String storyDescription;

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

    public String getStoryDescription() {
        return storyDescription;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }

    @Override
    public boolean isValid() {
        if (contentId==0)
            return false;
        if (userId==0)
            return false;
        if (storyDescription==null || storyDescription.trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMsg = "";
        if (contentId==0)
            errorMsg+="Invalid contentId ";
        if (userId==0)
            errorMsg+="Invalid userId ";
        if (storyDescription==null || storyDescription.trim().isEmpty())
            errorMsg+="Invalid story description";
        return errorMsg;
    }
}
