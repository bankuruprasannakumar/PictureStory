package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContestContent;
import com.picturestory.service.response.ResponseData ;

import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentDetailsDao<T> {

    public List<T> getAllContentDetailsContributedByUserId(int userId);

    public List<T> getAllContentDetailsContributedByUserId(int userId, int pixtoryStatus);

    public List<T> getAllContentDetailsLikedByUser(int userId);

    public List<Integer> getAllContentIdsLikedByUser(int userId);

    public List<Integer> getAllContentIdsCommentedByUser(int userId);

    public List<T> getAllContentCommentedAndLikedByUser(int userId);

    public List<T> getAllContentDetailsForIds(List<Integer> ids);

    public List<T> getAllContentDetails();

    public T getContentDetails(int id);

    public T getContentDetailsForUser(int contentId, int userId);

    public List<T> getAllContentDetailsForSet(long setId);

    public List<T> getAllContentDetailsTillSetId(long setId);

    public List<T> getAllContentDetailsContributedByUserIdTillSetId(int userId, long setId);

    public List<T> getAllContentDetailsForIdsTillSetId(List<Integer> ids,long setId);

    public String getImageForSetId(long setId);

    public int addContent(T t);

    public int addContestContent(ContestContent contestContent);

    public ResponseData getDetailedResponse();
}
