package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 03/07/2016.
 */
@XmlRootElement
public class GetCategoryRequest implements IRequest {
    private Integer categoryId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean isValid() {
        if(categoryId<=0)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage="";
        if(!isValid())
            errorMessage="Invalid category id";
        return errorMessage;
    }
}
