package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by krish on 26/07/2016.
 */
public interface IStoryDetailsDao<T> {

    public T getStoryForId(int storyId);

    public List<T> getStoryListForContent(int contentId);

    public List<T> getStoriesContributedByUser(int userId);

    public List<T> getStoriesForIds(List<Integer> storyIdList);

    public int addStory(T story);

    public List<T> getAllStoriesLikedByUser(int userId);

    public List<T> getStoriesForContentWithRange(int contentId,int startIndex,int numRows);

    public List<T> getAllStoriesLikedByUserForContent(int contentId, int userId);

    public List<T> getAllStoriesContributedByUserForContent(int contentId, int userId);

    public List<Integer> getContentIdListForStoriesLikedByUser(int userId);

    public List<Integer> getContentIdListForStoriesContributedByUser(int userId);

    public ResponseData getDetailedResponse();

}
