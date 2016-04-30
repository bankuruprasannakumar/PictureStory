package com.picturestory.service.request;


import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by aasha.medhi on 10/19/15.
 */
@XmlRootElement

public class GetCommentDetailListRequest implements IRequest {
    int userId;
    int contentId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
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
        if(contentId == 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (contentId == 0)
            msg.append(Constants.INVALID_COMMENT_ID);
        return msg.toString();
    }
}
