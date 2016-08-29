package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentUserLikeDao<T> {

    public boolean addContentUserLike(T t);

    public boolean deleteContentUserLike(T t);

    public boolean isContentLikedByUser(T t);

    public int fullCountOfUserLikesForContentId(int contentId);

    public List<User> usersWhoLikedContentId(int contentId);

    public ResponseData getDetailedResponse();
}
