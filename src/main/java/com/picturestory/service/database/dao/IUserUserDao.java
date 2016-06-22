package com.picturestory.service.database.dao;


import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 1/2/16.
 */
public interface IUserUserDao<T> {
    public boolean addUserUser(T userUser);

    public boolean deleteUserUser(T userUser);

    public boolean isFollowedByUser(T userUser);

    public ResponseData getDetailedResponse();

}
