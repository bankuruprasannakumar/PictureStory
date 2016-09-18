package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.api.utilities.UploadPictureToS3;
import com.picturestory.service.database.dao.IContentDetailsDao;
import com.picturestory.service.database.dao.IPostcardDetailsDao;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.Content;
import com.picturestory.service.pojo.Postcard;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.CreatePostcardRequest;
import com.picturestory.service.response.ResponseBuilder;
import com.picturestory.service.response.WebResponseBuilder;
import com.sun.jersey.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Random;

/**
 * Created by bankuru on 26/8/16.
 */
@Path("/createPostcard")
@Produces("application/json")
@Consumes(MediaType.MULTIPART_FORM_DATA)

public class CreatePostcard {
    private final IUserDetailsDao mUserDetailsDao;
    private final IContentDetailsDao mContentDetailsDao;
    private final IPostcardDetailsDao mPostcardDetailsDao;

    @Inject
    public CreatePostcard(IUserDetailsDao userDetailsDao, IContentDetailsDao contentDetailsDao,
                          IPostcardDetailsDao postcardDetailsDao) {
        mUserDetailsDao = userDetailsDao;
        mContentDetailsDao = contentDetailsDao;
        mPostcardDetailsDao = postcardDetailsDao;
    }

    //createpixtory
    @POST
    public Response createPostcard(@FormDataParam("image") InputStream image,
                                   @FormDataParam("imageFormat") String format,
                                   @FormDataParam("userId") int  userId,
                                   @FormDataParam("userId") int  contentId) {
        try {

            CreatePostcardRequest createPostcardRequest = new CreatePostcardRequest(image, format, userId, contentId);

            if (!createPostcardRequest.isValid()){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, createPostcardRequest.errorMessage());
            }

            User user =(User)mUserDetailsDao.getUser(userId);
            if (null == user) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            Content content = (Content) mContentDetailsDao.getContentDetails(contentId);
            if (null == content) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
            }

            String fileName = getVideoUrl(createPostcardRequest, null);
            //https://dabx1e3n0nllc.cloudfront.net/Devesh+-+Texts+in+the+making
            String pictureUrl = "https://s3.ap-south-1.amazonaws.com/pixtory-uploaded-content/"+fileName+".jpeg";

            Postcard postcard = new Postcard();
            postcard.setContentId(contentId);
            postcard.setPictureUrl(pictureUrl);
            postcard.setUserid(userId);

            int postcardId = mPostcardDetailsDao.createPostCard(postcard);

            if (postcardId != 0){
                JSONObject responseObj = composeResponse(pictureUrl);
                return ResponseBuilder.successResponse(responseObj.toString());
            } else {
                return ResponseBuilder.error(mPostcardDetailsDao.getDetailedResponse().getErrorCode(), mPostcardDetailsDao.getDetailedResponse().getErrorMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }

    private JSONObject composeResponse(String pictureUrl) throws JSONException {
        JSONObject responseObj = new JSONObject();
        responseObj.put(Constants.SUCCESS, true);
        responseObj.put(Constants.PICTURE_URL, pictureUrl);
        return responseObj;
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

    private String getVideoUrl(CreatePostcardRequest createPostcardRequest, String userName) {
        InputStream image = createPostcardRequest.getImage();
        String imageURL = null;
        if (image != null) {
            Random rand = new Random();
            int fileid = rand.nextInt();
            String uploadedFileLocation = "/home/ubuntu/" + fileid;
            writeToFile(image, uploadedFileLocation);
            UploadPictureToS3 uploadVideoToS3 = new UploadPictureToS3();
            imageURL = uploadVideoToS3.uploadImagesToS3(uploadedFileLocation, fileid + "", userName, "postcard");
            File file = new File(uploadedFileLocation);
            if (file.exists()) {
                file.delete();
            }
        }
        return imageURL;
    }
}
