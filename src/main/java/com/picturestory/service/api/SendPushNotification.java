package com.picturestory.service.api;

import com.picturestory.service.request.SendPushNotificationRequest;
import com.picturestory.service.response.ResponseBuilder;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bankuru on 22/6/16.
 */
@Path("/sendPushNotification")
@Produces("application/json")
@Consumes("application/json")

public class SendPushNotification {
    public static final String API_KEY = "AIzaSyC_QWcqeMMH5OXqfCkscBYP6hgiwSlBd2A";

    @Inject
    public SendPushNotification() {
    }

    @POST
    public Response sendPushNotification(SendPushNotificationRequest sendPushNotificationRequest) {
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", sendPushNotificationRequest.getMessage());
            jData.put("image", sendPushNotificationRequest.getImage());
            // Where to send GCM message.
            String gcmId = sendPushNotificationRequest.getGcmId();
            if (gcmId != null) {
                jGcmData.put("to", sendPushNotificationRequest.getGcmId());
            } else {
                jGcmData.put("to", "/topics/global");
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
            return ResponseBuilder.successResponse();
        } catch (Exception e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
            return ResponseBuilder.error(101, e.getMessage());
        }
    }
}
