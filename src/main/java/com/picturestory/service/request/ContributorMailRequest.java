package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 7/6/16.
 */
@XmlRootElement
public class ContributorMailRequest implements IRequest {
    int userId;
    String userEmail;
    String userName;
    String mobileNumber;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean isValid() {
        if (userId == 0)
            return false;
        if (userEmail == null || "".equals(userEmail.trim()))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (userEmail == null || "".equals(userEmail.trim()))
            msg.append(Constants.INVALID_USER_EMAIL);
        return msg.toString();
    }
}
