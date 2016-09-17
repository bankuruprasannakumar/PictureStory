package com.picturestory.service.api;

import com.picturestory.service.Constants;
import com.picturestory.service.database.dao.IUserDetailsDao;
import com.picturestory.service.pojo.CookieObject;
import com.picturestory.service.pojo.User;
import com.picturestory.service.request.NewUserRequest;
import com.picturestory.service.response.WebResponseBuilder;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Random;

import static com.picturestory.service.Constants.CHARSET;

/**
 * Created by bankuru on 27/8/16.
 */
@Path("/contributor/logout")
@Produces("application/json")
@Consumes("application/json")

public class WebLogoutUser {
    private final IUserDetailsDao mUserDetailsDao;

    @Inject
    public WebLogoutUser(IUserDetailsDao userDetailsDao) {
        mUserDetailsDao = userDetailsDao;
    }

    @GET
    public Response logOutUser(@CookieParam("cookieId") String cookieId) {
        try {
            if (cookieId != null){
                int userId = mUserDetailsDao.isCookiePresent(cookieId);
                if (userId == 0){
                    return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
                }
                boolean cookieDeleted = mUserDetailsDao.deleteCookiesForUser(userId);
                if (!cookieDeleted){
                    return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
                }
                return WebResponseBuilder.logoutSuccessResponse();
            }
            return WebResponseBuilder.error(Constants.ERRORCODE_INVALID_AUTH, Constants.INVALID_COOKIE);
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponseBuilder.error(Constants.ERRORCODE_IOEXCEPTION, "Internal Server Error");
        }
    }
}
