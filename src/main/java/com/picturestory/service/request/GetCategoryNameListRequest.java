package com.picturestory.service.request;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by krish on 03/07/2016.
 */
@XmlRootElement
public class GetCategoryNameListRequest implements IRequest {
    public List<Integer> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Integer> categoryIdList) {
        this.categoryIdList = categoryIdList;
    }

    List<Integer> categoryIdList;

    @Override
    public boolean isValid() {
        if(categoryIdList.size()<1)
            return false;
        for(Integer id:categoryIdList)
            if(id<1)
                return false;
        return true;
    }

    @Override
    public String errorMessage() {
        String errorMessage = "";
        if(categoryIdList.size()<1)
            errorMessage="Empty category id list";
        for(Integer id:categoryIdList)
            if(id<1)
            {
                errorMessage="One of the id is invalid";
                break;
            }
        return errorMessage;
    }
}
