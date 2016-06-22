package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 22/6/16.
 */
@XmlRootElement
public class SendPushNotificationRequest implements IRequest {
    String gcmId;
    String message;
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }
    @Override
    public boolean isValid() {
        if(gcmId == null)
            return false;
        if(message == null || "".equals(message.trim()))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(gcmId == null)
            msg.append(Constants.INVALID_USER_ID);
        if(message == null || "".equals(message.trim()))
            msg.append(Constants.INVALID_MESSAGE);
        return msg.toString();
    }

}
