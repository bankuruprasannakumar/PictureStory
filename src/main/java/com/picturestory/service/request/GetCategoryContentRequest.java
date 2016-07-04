package com.picturestory.service.request;

/**
 * Created by krish on 02/07/2016.
 */
public class GetCategoryContentRequest implements IRequest {
    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String errorMessage() {
        return null;
    }
}
