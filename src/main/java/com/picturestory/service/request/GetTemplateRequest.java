package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 7/12/16.
 */
@XmlRootElement
public class GetTemplateRequest implements IRequest{
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isValid() {
        if(userId == 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        return msg.toString();
    }

}
