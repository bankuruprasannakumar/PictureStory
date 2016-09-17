package com.picturestory.service.request;

import org.json.JSONArray;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sriram on 5/9/16.
 */
@XmlRootElement
public class UpdateUserInterestsRequest implements IRequest {
    int userId;
    List<Integer> userInterests;

    public List<Integer> getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(List<Integer> userInterests) {
        this.userInterests = userInterests;
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
        if(userInterests==null)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String msg = "";
        if(userId==0)
            msg+="Invalid user id.";
        if(userInterests==null)
            msg+="Invalid user interests." ;
        return msg;
    }
}