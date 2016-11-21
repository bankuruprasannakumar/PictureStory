package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 22/9/16.
 */
@XmlRootElement
public class UpdateUserDescriptionRequest implements IRequest{
    String userDescription;

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    @Override
    public boolean isValid() {
        if(userDescription == null || "".equals(userDescription.trim()))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userDescription == null || "".equals(userDescription.trim()))
            msg.append(Constants.INVALID_USER_DESCRIPTION);
        return msg.toString();
    }

}
