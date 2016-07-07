package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 04/07/2016.
 */
@XmlRootElement
public class GetContentByCategoryRequest implements IRequest {
    private Integer categoryId;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean isValid() {
        if(userId<1)
            return false;
        if (categoryId < 1)
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage="";
        if(userId<1)
            errorMessage+="Invalid User Id ";
        if (categoryId < 1)
            errorMessage+="Invalid categoryId ";
        return errorMessage;
    }
}
