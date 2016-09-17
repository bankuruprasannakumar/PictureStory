package com.picturestory.service.response;

import com.google.gson.Gson;
import com.picturestory.service.Constants;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;


/**
 * Created by bankuru on 21/8/15.
 */
public class WebResponseBuilder {
    public static Response successResponse(String webResponseData) {
        try {
            return Response.ok(webResponseData, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

    public static Response successRegisterResponse(String webResponseData, String cookieValue) {
        try {
            return Response.ok(webResponseData, MediaType.APPLICATION_JSON).cookie(new NewCookie(Constants.COOKIE_ID, cookieValue))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

    public static Response successResponse() {
        try {
            WebResponseData webResponseData = new WebResponseData();
            webResponseData.getData().setSuccess(true);
            Gson gson = new Gson();
            String response = gson.toJson(webResponseData);
            return Response.ok(response, MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

//    public NewCookie(String name,
//                     String value,
//                     String path,
//                     String domain,
//                     int version,
//                     String comment,
//                     int maxAge,
//                     Date expiry,
//                     boolean secure,
//                     boolean httpOnly) {

        public static Response logoutSuccessResponse() {
        try {
            WebResponseData webResponseData = new WebResponseData();
            webResponseData.getData().setSuccess(true);
            Gson gson = new Gson();
            String response = gson.toJson(webResponseData)  ;
            return Response.ok(response, MediaType.APPLICATION_JSON).cookie(new NewCookie(Constants.COOKIE_ID, null, null, null, 0,null, 0, new Date(0), false, false))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

    public static Response error(int errorCode, String errorMessage) {
        WebResponseData webResponseData = new WebResponseData();
        webResponseData.getData().setSuccess(false);
        webResponseData.getDiagnostics().setErrorCode(errorCode);
        webResponseData.getDiagnostics().setErrorMessage(errorMessage);
        Gson gson = new Gson();
        String response = gson.toJson(webResponseData);
        return Response.ok(response, MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
