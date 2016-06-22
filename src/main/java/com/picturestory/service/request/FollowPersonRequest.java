package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 23/9/15.
 */

@XmlRootElement
public class FollowPersonRequest implements IRequest {
    int userId;
    int personId;
    private String doFollow;

    public boolean getDoFollowValue() {
        if(doFollow != null){
            if(doFollow.equals("true") || doFollow.equals("yes") || doFollow.equals("1"))
                return true;
        }
        return false;
    }

    public String getDoFollow() {
        return doFollow;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setDoFollow(String doFollow){
        this.doFollow = doFollow;
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
        if (personId == 0)
            return false;
        if (doFollow == null || doFollow.trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (personId == 0)
            msg.append(Constants.INVALID_CONTENT_ID);
        if (doFollow == null || doFollow.trim().isEmpty())
            msg.append(Constants.INVALID_DO_FOLLOW);
        return msg.toString();
    }

}
