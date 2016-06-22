package com.picturestory.service.database.dao;


import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 30/12/15.
 */
public interface IUserGcmIdDao<T> {
    public boolean addPushNotifsId(T userGcmIdAssociation);
    public ResponseData getDetailedResponse();

}
