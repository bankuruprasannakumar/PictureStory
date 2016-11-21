package com.picturestory.service.database.dao;

/**
 * Created by bankuru on 4/11/16.
 */
public interface IUserTemplateCountDao {
    public boolean incrementUserTemplateCount(int userId);
    public int getUserTemplateCount(int userId);
}
