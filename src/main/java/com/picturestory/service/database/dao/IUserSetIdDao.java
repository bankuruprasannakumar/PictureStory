package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 22/12/16.
 */
public interface IUserSetIdDao<T> {
    public boolean addSetIdToFinishedSets(int userId, int setId);
    public ResponseData getDetailedResponse();

}
