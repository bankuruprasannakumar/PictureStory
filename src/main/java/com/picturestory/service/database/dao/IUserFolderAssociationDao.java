package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by bankuru on 22/12/16.
 */
public interface IUserFolderAssociationDao<T> {
    public boolean addUserFolderAssociation(T userFolderAssociation);
    public List<T> getFolderForUser(int userId);
    public boolean deleteUserFolderAssociation(T userFolderAssociation);
    public ResponseData getDetailedResponse();
}
