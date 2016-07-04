package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by krish on 03/07/2016.
 */
public interface IContentCategoryDao {

    //returns category ids list for a given content id
    public List<Integer> getCategoryIdListFromContentId(Integer contentId);

    //returns list of content Ids for a given category Id
    public List<Integer> getContentIdsFromCategoryId(Integer categoryId);

    public ResponseData getDetailedResponse();
}
