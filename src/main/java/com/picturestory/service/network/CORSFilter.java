package com.picturestory.service.network;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
/**
 * Created by bankuru on 2/7/16.
 */

public class CORSFilter implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest request,
                                    ContainerResponse response) {

        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
//        response.getHttpHeaders().add("Access-Control-Allow-Headers",
//                "origin, content-type, accept, authorization");
//        response.getHttpHeaders().add("Access-Control-Allow-Methods",
//                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        return response;
    }
}
