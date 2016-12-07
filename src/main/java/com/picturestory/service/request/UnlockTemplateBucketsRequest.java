package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 7/12/16.
 */
@XmlRootElement
public class UnlockTemplateBucketsRequest implements IRequest{
    private int userId;
    private int bucketId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    @Override
    public boolean isValid() {
        if(userId == 0)
            return false;
        if (bucketId == 0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if(userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (bucketId == 0)
            msg.append(Constants.INVALID_TEMPLATE_BUCKET_ID);
        return msg.toString();
    }
}
