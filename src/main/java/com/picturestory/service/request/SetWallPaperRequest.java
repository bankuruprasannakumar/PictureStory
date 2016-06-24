package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 24/6/16.
 */
@XmlRootElement
public class SetWallPaperRequest implements IRequest{
    String wallPaper;

    public String getWallPaper() {
        return wallPaper;
    }

    public void setWallPaper(String wallPaper) {
        this.wallPaper = wallPaper;
    }

    @Override
    public boolean isValid() {
        if (wallPaper == null || wallPaper.trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage = "";
        if (wallPaper == null || wallPaper.trim().isEmpty())
            errorMessage = "Invalid wallPaper";
        return errorMessage;
    }
}
