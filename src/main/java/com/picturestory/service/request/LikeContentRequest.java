package com.picturestory.service.request;


import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 30/12/15.
 */
@XmlRootElement
public class LikeContentRequest implements IRequest{

    int userId;
    int contentId;
    private String doLike;

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

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public void setDoLike(String doLike){
        this.doLike = doLike;
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
        if (contentId == 0)
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
        if (contentId == 0)
            msg.append(Constants.INVALID_CONTENT_ID);
        if (doLike == null || doLike.trim().isEmpty())
            System.out.println(userId+contentId+doLike);
            msg.append(Constants.INVALID_DO_LIKE);
        return msg.toString();
    }

}
