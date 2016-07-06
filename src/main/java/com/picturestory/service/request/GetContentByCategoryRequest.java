package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by krish on 04/07/2016.
 */
@XmlRootElement
public class GetContentByCategoryRequest implements IRequest {
    private Integer categoryId;
    private int userId;
    private String categoryName;
    private long registeredTimeStamp;

    public long getRegisteredTimeStamp() {
        return registeredTimeStamp;
    }

    public void setRegisteredTimeStamp(long registeredTimeStamp) {
        this.registeredTimeStamp = registeredTimeStamp;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

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
        if((categoryName==null || categoryName.trim().equals(""))&&(categoryId==null||categoryId<1))
            return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage="";
        if(userId<1)
            errorMessage+="Invalid User Id ";
        if((categoryName==null || categoryName.trim().equals(""))&&(categoryId==null||categoryId<1))
            errorMessage+= "Invalid category name and/or category id";
        return errorMessage;
    }
}
