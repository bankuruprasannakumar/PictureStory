package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 24/6/16.
 */
@XmlRootElement
public class UserSelectedWallPaperV2Request implements IRequest{
    int userId;
    int contentId;
    String doSelect;

    public boolean getDoSelectValue() {
        if(doSelect != null){
            if(doSelect.equals("true") || doSelect.equals("yes") || doSelect.equals("1"))
                return true;
        }
        return false;
    }

    public String getDoSelect() {
        return doSelect;
    }

    public void setDoSelect(String doSelect) {
        this.doSelect = doSelect;
    }

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
        if(userId == 0)
            return false;
        if (contentId == 0)
            return false;
        if (doSelect == null || doSelect.trim().isEmpty())
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
        if (doSelect == null || doSelect.trim().isEmpty())
            System.out.println(doSelect);
            msg.append("Invalid doSelect");
        return msg.toString();
    }
}
