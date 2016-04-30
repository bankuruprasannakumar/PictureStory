package com.picturestory.service.network;

import com.picturestory.service.Constants;
import com.picturestory.service.response.ResponseData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bankuru on 30/4/16.
 */
public class NetworkConnection {
    public enum METHOD {
        GET,
        POST,
        PUT
    }

    public ResponseData makeConnection(String url, METHOD method, String data) {
        ResponseData response = new ResponseData();
        try {
            URLConnection connection = new URL(url).openConnection();
            if (method == METHOD.POST || method == METHOD.PUT) {
                connection.setRequestProperty(Constants.ACCEPT_CHARSET, Constants.CHARSET);
                connection.setRequestProperty("Content-Type", "application/json;charset=" + Constants.CHARSET);
            }
            System.out.println("Network  query " +data);
            connection.setDoOutput(true);
            OutputStream output1 = connection.getOutputStream();
            output1.write(data.getBytes(Constants.CHARSET));
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            if (httpConnection.getResponseCode() >= 400) {
                System.out.println("network failure");
                response.setSuccess(false);
                response.setErrorCode(httpConnection.getResponseCode());
                InputStream errorstream = httpConnection.getErrorStream();
                StringBuilder errorMessage = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorstream, Constants.CHARSET));
                for (String line; (line = reader.readLine()) != null; ) {
                    errorMessage.append(line);
                }
                response.setErrorMessage(errorMessage.toString());
            }else{
                System.out.println("network success");
                InputStream inputStream = connection.getInputStream();
                StringBuilder responseData = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Constants.CHARSET));
                for (String line; (line = reader.readLine()) != null; ) {
                    responseData.append(line);
                }
                response.setSuccess(true);
                response.setData(responseData.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorCode(Constants.ERRORCODE_IOEXCEPTION);
            response.setErrorMessage(Constants.IOEXCEPTION_ERRORMESSAGE);
        }
        return response;
    }

}
