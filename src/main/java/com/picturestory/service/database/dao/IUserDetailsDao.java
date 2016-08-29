package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Contributor;
import com.picturestory.service.pojo.CookieObject;
import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasha.medhi on 9/24/15.
 */
public interface IUserDetailsDao<T> {

    public boolean updateUser(T user);

    public boolean addContributor(Contributor contributor);


    public T getUser(int userId);

    public int createUser(T user);

    public boolean updateFbIdOfUser(T user);

    public boolean updateGcmIdOfUser(T user);

    public int addUserForFbId(T user);

    public int getTotalCount();

    public ArrayList<T> getUsersForIndex(int startIndex, int endIndex);

    public ResponseData getDetailedResponse();

    public int addUserForEmail(T user);

    public int isUserPresentForEmail(T user);

    public int isUserPresentForFbId(T user);

    public boolean createCookieForUser(CookieObject cookieObject);

    public int isCookiePresent(String cookieId);

}
