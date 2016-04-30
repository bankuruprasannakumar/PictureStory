package com.picturestory.service.request;


import com.picturestory.service.Constants;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public class GetMainFeedRequest implements IRequest {
    int userId;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
