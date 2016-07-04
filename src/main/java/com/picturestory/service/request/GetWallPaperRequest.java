package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 24/6/16.
 */
@XmlRootElement
public class GetWallPaperRequest implements IRequest{
    int userId;
    long registeredTimeStamp;

    public long getRegisteredTimeStamp() {
        return registeredTimeStamp;
    }

    public void setRegisteredTimeStamp(long registeredTimeStamp) {
        this.registeredTimeStamp = registeredTimeStamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isValid() {
        if (userId == 0 || registeredTimeStamp<0)
            return false;
        return true;
    }



    @Override
    public String errorMessage() {
        String errorMessage = "";
        if (userId == 0 || registeredTimeStamp<0)
            errorMessage = "Invalid wallPaper";
        return errorMessage;
    }
}
