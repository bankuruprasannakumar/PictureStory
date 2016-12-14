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
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    //createpostcard
    @POST
    public Response createPostcard(@FormDataParam("image") InputStream image,
                                   @FormDataParam("tag") List<FormDataBodyPart> tag,
                                   @FormDataParam("imageFormat") String format,
                                   @FormDataParam("text") String text,
                                   @FormDataParam("templateId") int templateId,
                                   @FormDataParam("location") String location,
                                   @FormDataParam("postcardUserName") String postcardUserName,
                                   @FormDataParam("userId") int  userId,
                                   @FormDataParam("contentId") int  contentId) {
        try {
            List<String> extractedTags = new ArrayList<String>();
            for(FormDataBodyPart keyword : tag)
                extractedTags.add(keyword.getValueAs(String.class));
            CreatePostcardRequest createPostcardRequest = new CreatePostcardRequest(image, format, text, templateId, location, postcardUserName, userId, contentId, extractedTags);

            if (!createPostcardRequest.isValid()){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, createPostcardRequest.errorMessage());
            }

            User user =(User)mUserDetailsDao.getUser(userId);
            if (null == user) {
                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_USER_ID);
            }
            if (createPostcardRequest.getContentId() != 0) {
                Content content = (Content) mContentDetailsDao.getContentDetails(contentId);
                if (null == content) {
                    return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_CONTENT_ID);
                }
            }
//            if (!isImage(image)) {
//                return ResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_IMAGE);
//            }
            // check for valid templateId
            

            String fileName = getVideoUrl(createPostcardRequest, null);
            //https://dabx1e3n0nllc.cloudfront.net/Devesh+-+Texts+in+the+making
            String pictureUrl = "https://s3.ap-south-1.amazonaws.com/pixtory-uploaded-content/"+fileName+".jpeg";

            Postcard postcard = new Postcard();
            postcard.setPictureUrl(pictureUrl);
            postcard.setUserId(createPostcardRequest.getUserId());
            postcard.setText(createPostcardRequest.getText());
            postcard.setPostcardUserName(createPostcardRequest.getPostcardUserName());
            postcard.setTemplateId(createPostcardRequest.getTemplateId());
            postcard.setTags(createPostcardRequest.getTags());
            if (contentId != 0) {
                postcard.setContentId(contentId);
            }
            if (createPostcardRequest.getLocation() != null || !createPostcardRequest.getLocation().isEmpty()) {
                postcard.setLocation(createPostcardRequest.getLocation());
            }
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

    private boolean isImage(InputStream inputStream) {
            try {
                ImageIO.read(inputStream).toString();
                return true;
            } catch (Exception e) {
                return false;
            }

    }
}
