package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

/**
 * Created by krish on 26/07/2016.
 */
public interface IStoryUserLikeDao<T> {

    public boolean addStoryUserLike(T t);

    public boolean deleteStoryUserLike(T t);

    public boolean isStoryLikedByUser(T t);

    public int fullCountOfUserLikesForStoryId(int storyId);

    public ResponseData getDetailedResponse();
}
