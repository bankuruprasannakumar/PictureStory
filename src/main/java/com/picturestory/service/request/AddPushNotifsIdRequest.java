package com.picturestory.service.request;


import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 30/12/15.
 */
@XmlRootElement
public class AddPushNotifsIdRequest implements IRequest{
    int userId;
    String gcmId;
    String apNsId;

    public String getApNsId() {
        return apNsId;
    }

    public void setApNsId(String apNsId) {
        this.apNsId = apNsId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

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
        if ((gcmId == null || gcmId.trim().isEmpty()) && (apNsId == null || apNsId.trim().isEmpty()))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if ((gcmId == null || gcmId.trim().isEmpty()) && (apNsId == null || apNsId.trim().isEmpty()))
            msg.append(Constants.INVALID_GCMID);
        return msg.toString();
    }
}
