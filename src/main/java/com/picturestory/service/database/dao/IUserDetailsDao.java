package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Contributor;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by aasha.medhi on 9/24/15.
 */
public interface IUserDetailsDao<T> {
    public int addUser(T user);

    public boolean updateUser(T user);

    public boolean addContributor(Contributor contributor);

    public boolean deleteUser(int userId);

    public T getUser(int userId);

    public int createUser(T user);


    public int addUserForFbId(T user);

    public int isUserNamePresent(User user);

    public ResponseData getDetailedResponse();


}
