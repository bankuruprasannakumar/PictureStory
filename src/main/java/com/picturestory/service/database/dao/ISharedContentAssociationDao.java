package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 10/7/16.
 */
public interface ISharedContentAssociationDao<T> {

    public int CreateSharedContentId(int contentId);

    public int getContentIdforSharedContentId(int sharedContentId);

    public ResponseData getDetailedResponse();

}
