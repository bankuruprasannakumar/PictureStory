package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.User;
import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by sriram on 2/9/16.
 */
public interface ICommentUserLikeDao<T> {

    public boolean addCommentUserLike(T t);

    public boolean deleteCommentUserLike(T t);

    public boolean isCommentLikedByUser(T t);

    public int fullCountOfUserLikesForCommentId(int commentId);

    public List<User> usersWhoLikedCommentId(int commentId);

    public ResponseData getDetailedResponse();
}
