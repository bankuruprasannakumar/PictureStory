package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.UploadVideoToS3;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.PixtoryStatus;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.AddContentRequest;
import com.picturestory.service.response.WebResponseBuilder;
import com.sun.jersey.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Random;

/**
 * Created by bankuru on 26/8/16.
 */
@Path("/contributor/createpixtory")
@Produces("application/json")
@Consumes(MediaType.MULTIPART_FORM_DATA)

public class ContestCreatePixtory {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public ContestCreatePixtory(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
    }

    //createpixtory
    @POST
    public Response createpixtory(@FormDataParam("image") InputStream image,
                                  @FormDataParam("imageFormat") String format,
                                  @FormDataParam("story") String story,
                                  @FormDataParam("title") String title,
                                  @FormDataParam("location") String location,
                                  @CookieParam("cookieId") String cookieId) {
        try {
            if (cookieId == null){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            int userId = mUserDetailsDao.isCookiePresent(cookieId);
            if (userId == 0){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
            }
            User user = (User) mUserDetailsDao.getUser(userId);
            AddContentRequest addContentRequest = new AddContentRequest(image, format, story, title, location);
            if (!addContentRequest.isValid()) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, addContentRequest.errorMessage());
            }
            String userName = user.getUserName();
            String fileName = getVideoUrl(addContentRequest, userName);
            //https://dabx1e3n0nllc.cloudfront.net/Devesh+-+Texts+in+the+making
            String pictureUrl = "https://dabx1e3n0nllc.cloudfront.net/"+fileName+".jpeg";
            Content content = new Content();
            content.setUserId(userId);
            content.setPictureUrl(pictureUrl);
            content.setPictureDescription(story);
            if (addContentRequest.getTitle() != null && !addContentRequest.getTitle().trim().isEmpty()) {
                content.setPictureSummary(addContentRequest.getTitle());
            }

            if (addContentRequest.getLocation() != null && !addContentRequest.getLocation().trim().isEmpty()) {
                content.setPlace(addContentRequest.getTitle());
            }

            content.setPixtoryStatus(PixtoryStatus.SUBMITTED.getValue());
            int id = mContentDetailsDao.addContent(content);
            if (id == 0) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentDetailsDao.getDetailedResponse().getErrorMessage());
            } else {
                content.setContentId(id);
                return WebResponseBuilder.successResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getVideoUrl(AddContentRequest addContentRequest, String userName) {
        InputStream image = addContentRequest.getImage();
        String imageURL = null;
        if (image != null) {
            Random rand = new Random();
            int fileid = rand.nextInt();
            String uploadedFileLocation = "/home/ubuntu/" + fileid;
            writeToFile(image, uploadedFileLocation);
            UploadVideoToS3 uploadVideoToS3 = new UploadVideoToS3();
            imageURL = uploadVideoToS3.uploadImagesToS3(uploadedFileLocation, fileid + "", userName);
            File file = new File(uploadedFileLocation);
            if (file.exists()) {
                file.delete();
            }
        }
        return imageURL;
    }
}
