package com.picturestory.service.response;

import com.picturestory.service.Constants;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by bankuru on 21/8/15.
 */
public class ResponseBuilder {
    public static Response successResponse(String response) {
        try {
            return Response.ok(response, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

    public static Response successResponse() {
        try {
            JSONObject obj = new JSONObject();
            obj = new JSONObject();
            obj.put(Constants.SUCCESS, true);
            return Response.ok(obj.toString(),MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .build();
        }
    }

    public static Response error(int errorCode, String errorMessage) {
        ResponseData response = new ResponseData();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return Response.ok(response, MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
