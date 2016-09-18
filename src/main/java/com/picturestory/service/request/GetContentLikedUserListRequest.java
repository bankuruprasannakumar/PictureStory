package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sriram on 2/9/16.
 */

@XmlRootElement
public class GetContentLikedUserListRequest implements IRequest{

    private int contentId;
    private int userId;

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

    @Override
    public boolean isValid() {
        if(userId==0)
            return false;
        if(contentId==0)
            return false;

        return true;
    }

    @Override
    public String errorMessage() {
        if(userId==0)
            return "Invalid user id";
        if(contentId==0)
            return "Invalid content id";
        return null;
    }
}
