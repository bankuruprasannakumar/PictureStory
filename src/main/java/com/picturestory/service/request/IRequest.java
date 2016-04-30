package com.picturestory.service.request;

/**
 * Created by bankuru on 30/4/16.
 */
public interface IRequest {
    public boolean isValid();

    public String errorMessage();

}
