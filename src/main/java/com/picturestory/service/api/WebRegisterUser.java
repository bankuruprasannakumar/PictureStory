package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.CookieObject;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.NewUserRequest;
import com.picturestory.service.response.WebResponseBuilder;
import com.picturestory.service.response.WebResponseData;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.util.Random;

import static com.picturestory.service.Constants.CHARSET;

/**
 * Created by bankuru on 27/8/16.
 */
@Path("/contributor/register")
@Produces("application/json")
@Consumes("application/json")

public class WebRegisterUser {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public WebRegisterUser(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @POST
    public Response registerUser(NewUserRequest userRequest,
                                 @CookieParam("cookieId") String cookieId) {
        try {
            if(userRequest == null){
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, Constants.INVALID_REQUEST);
            }
            if (!userRequest.isValid()) {
                return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_INPUT, userRequest.errorMessage());
            }
            if (cookieId != null){
                System.out.println("cookie is null");
                int userId = mUserDetailsDao.isCookiePresent(cookieId);
                if (userId == 0){
                    return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
                }
                User user = (User) mUserDetailsDao.getUser(userId);
                JSONObject responseJSON = composeCookiePresentSuccessResponse(user);
                return WebResponseBuilder.successResponse(responseJSON.toString());
            }
            User user = new User();
            String fbid = userRequest.getFbId();
            if(fbid!=null && !fbid.trim().isEmpty())
                user.setFbId(userRequest.getFbId());
            if (userRequest.getUserName() != null && !"".equals(userRequest.getUserName().trim())){
                user.setUserName(userRequest.getUserName());
            }
            if (userRequest.getUserEmail() != null && !"".equals(userRequest.getUserEmail().trim())){
                user.setUserEmail(userRequest.getUserEmail());
            }
            if (userRequest.getUserImageUrl() != null && !"".equals(userRequest.getUserImageUrl().trim()))
                user.setUserImage(userRequest.getUserImageUrl());
            if (userRequest.getGcmId()!=null && !"".equals(userRequest.getGcmId().trim()))
                user.setGcmId(userRequest.getGcmId());
            if (userRequest.getRegisteredTime() != 0){
                user.setRegisteredTime(userRequest.getRegisteredTime());
            }
            int isUserPresent  = 0;
            if(fbid!=null && !fbid.trim().isEmpty()) {
                isUserPresent = mUserDetailsDao.isUserPresentForFbId(user);
            }
            else {
                isUserPresent = mUserDetailsDao.isUserPresentForEmail(user);
            }
            int userId = 0;
            if (isUserPresent == 0) {
                userId = mUserDetailsDao.createUser(user);
            } else {
                userId = isUserPresent;
            }
            if (userId == 0) {
                return WebResponseBuilder.error(mUserDetailsDao.getDetailedResponse().getErrorCode(), mUserDetailsDao.getDetailedResponse().getErrorMessage());
            }
            String encodedText = getRandomEncodedCookie();
            if (encodedText == "false") {
                return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Unable to generate Cookie");
            }
            CookieObject cookieObject = new CookieObject();
            cookieObject.setCookieId(encodedText);
            cookieObject.setUserId(userId);
            boolean isCookieSet = mUserDetailsDao.createCookieForUser(cookieObject);

            if (isCookieSet) {
                User currentUser = (User)mUserDetailsDao.getUser(userId);
                JSONObject responseJSON = composeSuccessResponse(currentUser);
                return WebResponseBuilder.successRegisterResponse(responseJSON.toString(), encodedText);
            }
            Response response = WebResponseBuilder.error(mUserDetailsDao.getDetailedResponse().getErrorCode(), mUserDetailsDao.getDetailedResponse().getErrorMessage());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
    private String getRandomEncodedCookie(){
        try {
            Random rand = new Random();
            int cookieId = rand.nextInt( Integer.MAX_VALUE ) + 1;
            String encodedText = new String(Base64.encodeBase64(Integer.toString(cookieId).getBytes(CHARSET)));
            return encodedText;
        } catch (Exception e){
            return "false";
        }
    }
    private JSONObject composeSuccessResponse(User user) throws JSONException{
        JSONObject responseJSON = new JSONObject();
        JSONObject diagnosticsJSON = new JSONObject();
        JSONObject dataJSON = new JSONObject();

        //construct userDetails
        JSONObject userJSON = new JSONObject();
        if (null != user.getUserName() && !user.getUserName().trim().isEmpty()){
            userJSON.put(Constants.USER_NAME, user.getUserName());
        }
        if (user.getUserImage() != null && !user.getUserImage().trim().isEmpty()){
            userJSON.put(Constants.IMAGE_URL, user.getUserImage());
        }
        //construct data
        dataJSON.put(Constants.SUCCESS, true);
        dataJSON.put(Constants.USER_DETAILS, userJSON);

        responseJSON.put(Constants.DIAGNOSTICS, diagnosticsJSON);
        responseJSON.put(Constants.DATA, dataJSON);
        return responseJSON;
    }

    private JSONObject composeCookiePresentSuccessResponse(User user) throws JSONException{
            JSONObject responseJSON = new JSONObject();
            JSONObject diagnosticsJSON = new JSONObject();
            JSONObject dataJSON = new JSONObject();

            //construct userDetails
            JSONObject userJSON = new JSONObject();
            userJSON.put(Constants.USER_NAME, user.getUserName());
            userJSON.put(Constants.IMAGE_URL, user.getUserImage());
            //construct data
            dataJSON.put(Constants.SUCCESS, true);
            dataJSON.put(Constants.USER_DETAILS, userJSON);

            responseJSON.put(Constants.DIAGNOSTICS, diagnosticsJSON);
            responseJSON.put(Constants.DATA, dataJSON);
            return responseJSON;
    }

}
