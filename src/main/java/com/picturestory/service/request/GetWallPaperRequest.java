package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 24/6/16.
 */
@XmlRootElement
public class GetWallPaperRequest implements IRequest{
    int userId;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isValid() {
        if (userId == 0 )
            return false;
        return true;
    }



    @Override
    public String errorMessage() {
        String errorMessage = "";
        if (userId == 0 )
            errorMessage = "Invalid wallPaper";
        return errorMessage;
    }
}
