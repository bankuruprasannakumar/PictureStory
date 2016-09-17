package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 27/8/16.
 */
@XmlRootElement
public class WebNewUserRequest implements IRequest{

    private String userEmail;
    private String fbId;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    @Override
    public boolean isValid() {
        if ((fbId == null || fbId.trim().isEmpty())&&(userEmail == null || userEmail.trim().isEmpty())) {
            return false;
        }
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if ((fbId == null || fbId.trim().isEmpty())&&(userEmail == null || userEmail.trim().isEmpty())){
            errorMessage.append("Invalid fbId or email id");
        }
        return errorMessage.toString();
    }
}
