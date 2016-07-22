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
    private long registeredTime;


    public long getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(long registeredTime) {
        this.registeredTime = registeredTime;
    }

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
        if ((fbId == null || fbId.trim().isEmpty())&&(userEmail == null || userEmail.trim().isEmpty())) {
            return false;
        }
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if (fbId == null || fbId.trim().isEmpty()) {
            errorMessage.append("Invalid fbId. or email id");
        }
        return errorMessage.toString();
    }
}
