package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 26/07/2016.
 */
@XmlRootElement
public class LikeStoryRequest implements IRequest{
    int storyId;
    int userId;
    String doLike;


    public boolean getDoLikeValue() {
        if(doLike != null){
            if(doLike.equals("true") || doLike.equals("yes") || doLike.equals("1"))
                return true;
        }
        return false;
    }

    public String getDoLike() {
        return doLike;
    }

    public void setDoLike(String doLike){
        this.doLike = doLike;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
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
        if (storyId == 0)
            return false;
        if (doLike == null || doLike.trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (storyId == 0)
            msg.append(Constants.INVALID_STORY_ID);
        if (doLike == null || doLike.trim().isEmpty())
            msg.append(Constants.INVALID_DO_LIKE);
        return msg.toString();
    }
}
