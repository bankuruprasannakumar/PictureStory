package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData ;

import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentUserCommentDao<T> {

    public boolean addContentUserComment(T t);

    public boolean deleteContentUserComment(T t);

    public boolean isContentCommentedByUser(T t);

    public int fullCountOfUserCommentsForContentId(int contentId);

    public List<T> getAllCommentsForContentId(int contentId);

    public List<T> getCommentsForContentIdWithIndex(int contentId, int start, int end);

    public ResponseData getDetailedResponse();
}
