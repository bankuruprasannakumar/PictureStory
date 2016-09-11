package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.UploadPictureToS3;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.ContestContent;
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
@Path("/contest/createpixtory")
@Produces("application/json")
@Consumes(MediaType.MULTIPART_FORM_DATA)

public class ContestCreatePixtory {
    private final IContentDetailsDao mContentDetailsDao;

    @Inject
    public ContestCreatePixtory(IContentDetailsDao contentDetailsDao) {
        mContentDetailsDao = contentDetailsDao;
    }

    //createpixtory
    @POST
    public Response createpixtory(@FormDataParam("imageFile") InputStream image,
                                  @FormDataParam("imageFormat") String format,
                                  @FormDataParam("story") String story,
                                  @FormDataParam("title") String title,
                                  @FormDataParam("location") String location,
                                  @FormDataParam("username") String userName,
                                  @FormDataParam("mail") String userEmail,
                                  @FormDataParam("phone") String mobileNumber,
                                  @FormDataParam("webSite") String webSite,
                                  @FormDataParam("instagram") String instagram,
                                  @FormDataParam("category") String category
                                  ) {
        try {

            String fileName = getVideoUrl(image);
            //https://dabx1e3n0nllc.cloudfront.net/Devesh+-+Texts+in+the+making
            String pictureUrl = "https://s3.ap-south-1.amazonaws.com/pixtory-uploaded-content/"+fileName+".jpeg";
            ContestContent contestContent = new ContestContent(pictureUrl, story, title, location, userName, userEmail, mobileNumber, webSite, instagram, category);
            int id = mContentDetailsDao.addContestContent(contestContent);
            if (id == 0) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, mContentDetailsDao.getDetailedResponse().getErrorMessage());
            } else {
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

    private String getVideoUrl(InputStream image) {
        String imageURL = null;
        if (image != null) {
            Random rand = new Random();
            int fileid = rand.nextInt();
            String uploadedFileLocation = "/home/ubuntu/" + fileid;
            writeToFile(image, uploadedFileLocation);
            UploadPictureToS3 uploadVideoToS3 = new UploadPictureToS3();
            imageURL = uploadVideoToS3.uploadImagesToS3(uploadedFileLocation, fileid + "", null, "contest");
            File file = new File(uploadedFileLocation);
            if (file.exists()) {
                file.delete();
            }
        }
        return imageURL;
    }
}
