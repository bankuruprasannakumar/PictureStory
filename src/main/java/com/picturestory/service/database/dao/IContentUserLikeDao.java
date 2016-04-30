package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentUserLikeDao<T> {

    public boolean addContentUserLike(T t);

    public boolean deleteContentUserLike(T t);

    public boolean isContentLikedByUser(T t);

    public int fullCountOfUserLikesForContentId(int contentId);

    public ResponseData getDetailedResponse();
}
