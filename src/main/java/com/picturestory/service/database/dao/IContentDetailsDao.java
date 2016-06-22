package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Content;
import com.picturestory.service.response.ResponseData ;

import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentDetailsDao<T> {

    public List<T> getAllContentDetailsContributedByUserId(int userId);

    public List<T> getAllContentDetailsLikedByUser(int userId);

    public List<Integer> getAllContentIdsLikedByUser(int userId);

    public List<Integer> getAllContentIdsCommentedByUser(int userId);

    public List<T> getAllContentCommentedAndLikedByUser(int userId);

    public List<T> getAllContentDetailsForIds(List<Integer> ids);

    public List<T> getAllContentDetails();

    public T getContentDetails(int id);

    public ResponseData getDetailedResponse();
}
