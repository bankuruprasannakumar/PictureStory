package com.picturestory.service.database.dao;

import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by krish on 03/07/2016.
 */
public interface ICategoryDetailsDao<T,S> {

    //returns category name for a given category id
    public S getCategoryName(T categoryId);

    //returns list of category names for a given list of category ids
    public List<S> getCategoryNameList(List<T> categoryIdList);

    //returns categoryId for a given given category name
    public T getCategoryId(S categoryName);

    public ResponseData getDetailedResponse();
}
