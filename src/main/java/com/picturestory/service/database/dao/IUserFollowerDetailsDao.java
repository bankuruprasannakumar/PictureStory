package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by bankuru on 4/2/16.
 */
public interface IUserFollowerDetailsDao<T, S> {
    //Returns users who follow userId
    public List<S> getAllFollowers(T userId, int startIndex, int maxCount);

    //Returns fullcount of users who follow userId
    public int fullCountOfAllFollowers(T userId);

    public ResponseData getDetailedResponse();

}
