package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 24/6/16.
 */
@XmlRootElement
public class GetPushNfDataRequest implements IRequest{
    int startIndex;
    int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public boolean isValid() {
        if (endIndex == 0 )
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage = "";
        if (endIndex == 0 )
            errorMessage = "Invalid endIndex";
        return errorMessage;
    }
}
