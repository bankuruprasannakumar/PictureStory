package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 27/07/2016.
 */
@XmlRootElement
public class GetStoriesForContentRequest implements IRequest {
    int userId;
    int contentId;
    int startIndex;
    int numRows;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    @Override
    public boolean isValid() {
        if(userId==0)
            return false;
        if(contentId==0)
            return false;
        if(numRows==0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errMsg = "";
        if(userId==0)
            errMsg+="Invalid user id ";
        if(contentId==0)
            errMsg+="Invalid content id ";
        if(numRows==0)
            errMsg+="Invalid numRows";
        return errMsg;
    }
}
