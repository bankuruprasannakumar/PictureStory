package com.picturestory.service.request;

import com.picturestory.service.Constants;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 22/12/16.
 */
@XmlRootElement
public class CreateFolderRequest implements IRequest{
    private int userId;
    private String folderName;
    private int folderImageId;
    private String doCreate;

    public boolean getDoCreateValue() {
        if(doCreate != null){
            if(doCreate.equals("true") || doCreate.equals("yes") || doCreate.equals("1"))
                return true;
        }
        return false;
    }

    public String getDoCreate() {
        return doCreate;
    }

    public void setDoCreate(String doCreate) {
        this.doCreate = doCreate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderImageId() {
        return folderImageId;
    }

    public void setFolderImageId(int folderImageId) {
        this.folderImageId = folderImageId;
    }

    @Override
    public boolean isValid() {
        if (userId == 0)
            return false;
        if (folderName == null || "".equals(folderName.trim()))
            return false;
        if (folderImageId == 0)
            return false;
        if (doCreate == null || doCreate.trim().isEmpty())
            return false;

        return true;


    }

    @Override
    public String errorMessage() {
        StringBuilder msg = new StringBuilder();
        if (userId == 0)
            msg.append(Constants.INVALID_USER_ID);
        if (folderName == null || "".equals(folderName.trim()))
            msg.append(Constants.INVALID_FOLDER_NAME);
        if (folderImageId == 0)
            msg.append(Constants.INVALID_FOLDER_ID);
        if (doCreate == null || doCreate.trim().isEmpty())
            msg.append(Constants.INVALID_DO_CREATE);

        return msg.toString();
    }
}
