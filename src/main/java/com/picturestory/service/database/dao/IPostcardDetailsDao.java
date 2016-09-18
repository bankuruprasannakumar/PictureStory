package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by bankuru on 19/9/16.
 */
public interface IPostcardDetailsDao<T> {

    public List<T> getAllPostCardsOfUser(int userId);

    public int createPostCard(T postcard);

    public ResponseData getDetailedResponse();
}
