package com.picturestory.service.database.adapters;

/**
 * Created by aasha.medhi on 9/24/15.
 */
public interface IDataAccessAdapter<T> {
    public T updateRequest(String query);
    public T selectRequest(String query);
}
