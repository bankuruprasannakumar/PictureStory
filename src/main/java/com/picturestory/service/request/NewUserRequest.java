package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 23/9/15.
 */

@XmlRootElement
public class NewUserRequest implements IRequest {
    private String userName;
    private String userEmail;
    private String userImageUrl;
    private String gcmId;
    private String fbId;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public NewUserRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public boolean isValid() {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
/*
        if (userEmail == null || userEmail.isEmpty()) {
            return false;
        }
*/
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if (userName == null || userName.trim().isEmpty()) {
            errorMessage.append("Invalid userName. ");
        }
/*
        if (userEmail == null || userEmail.isEmpty()) {
            errorMessage.append("Invalid userEmail. ");
        }
*/
        return errorMessage.toString();
    }
}
