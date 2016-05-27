package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Content;
import com.picturestory.service.response.ResponseData ;

import java.util.List;

/**
 * Created by aasha.medhi on 10/19/15.
 */
public interface IContentDetailsDao<T> {

    public List<T> getAllContentDetailsForUserId(int userId);

    public List<T> getAllContentDetails();

    public T getContentDetails(int id);

    public ResponseData getDetailedResponse();
}
