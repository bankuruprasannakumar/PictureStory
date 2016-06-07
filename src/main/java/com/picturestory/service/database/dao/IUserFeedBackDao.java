package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 7/6/16.
 */
public interface IUserFeedBackDao<T>  {
    public boolean addFeedBack(T feedBack);
    public ResponseData getDetailedResponse();

}
