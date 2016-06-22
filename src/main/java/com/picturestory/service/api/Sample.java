package com.picturestory.service.api; /**
 * Created by bankuru on 29/4/16.
 */
import com.picturestory.service.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

@Path("myResource")
@Produces(MediaType.APPLICATION_JSON)
public class Sample {
    @GET
    public Response getIt() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.SUCCESS, false);
        return Response.ok(jsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

}
