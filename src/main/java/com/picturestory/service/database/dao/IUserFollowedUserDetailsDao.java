package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by bankuru on 4/2/16.
 */
public interface IUserFollowedUserDetailsDao<T,S> {
    //Returns users who are  followed by userId
    public List<S> getAllFollowedUsers(T userId, int startIndex, int maxCount);

    //Returns fullcount of users who are followed by userId
    public int fullCountAllFollowedUsers(T userId);

    public ResponseData getDetailedResponse();

}
