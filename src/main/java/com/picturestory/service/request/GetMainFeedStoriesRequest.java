package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 27/07/2016.
 */
@XmlRootElement
public class GetMainFeedStoriesRequest implements IRequest {
    int userId;
    int startIndex;
    int numRows;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        if (userId == 0)
            return false;
        if(numRows==0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if(numRows==0)
            msg.append(" Invalid num rows");
        return msg.toString();
    }
}
