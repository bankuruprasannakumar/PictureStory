package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 8/7/16.
 */
@XmlRootElement
public class GetContentByIdRequest implements IRequest{

    int contentId;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    @Override
    public boolean isValid() {
        if (contentId == 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if (contentId == 0 )
            errorMessage.append("Invalid contentId");

        return errorMessage.toString();
    }
}
