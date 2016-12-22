package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 27/5/16.
 */
@XmlRootElement
public class AddSetIdToFinishedSetsForUserRequest implements IRequest {
    int userId;
    int setId;

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

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
        if(setId < 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (setId < 0)
            msg.append(Constants.INVALID_SET_ID);
        return msg.toString();
    }

}
