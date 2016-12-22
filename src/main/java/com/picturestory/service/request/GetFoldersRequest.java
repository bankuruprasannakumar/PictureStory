package com.picturestory.service.request;


import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class GetFoldersRequest implements IRequest {
    int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isValid() {
        if (userId == 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        return msg.toString();
    }
}
