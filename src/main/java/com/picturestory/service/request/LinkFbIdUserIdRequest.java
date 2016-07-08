package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 8/7/16.
 */
@XmlRootElement
public class LinkFbIdUserIdRequest implements IRequest{
    int userId;
    String fbId;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Override
    public boolean isValid() {
        if (fbId == null || fbId.trim().isEmpty()) {
            return false;
        }
        if (userId == 0)
            return false;
        return true;

    }

    @Override
    public String errorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if (fbId == null || fbId.trim().isEmpty()) {
            errorMessage.append("Invalid fbId. ");
        }
        if (userId == 0 )
            errorMessage.append("Invalid userId");

        return errorMessage.toString();
    }
}
