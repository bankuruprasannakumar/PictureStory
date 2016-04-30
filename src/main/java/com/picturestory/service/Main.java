package com.picturestory.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.picturestory.service.database.SolrModule;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created by bankuru on 29/4/16.
 */
public class Main {
    private HttpServer server;
    public static final String BASE_URI = "http://appdemo.ops.ev1.inmobi.com:4055/";

    public static HttpServer startServer(){
        try {
            Injector injector = Guice.createInjector(new SolrModule());
            // create a resource config that scans for JAX-RS resources and providers
            // in com.example.rest package
//        final ResourceConfig rc = new ResourceConfig().packages("com.picturestory.service.api");
            ResourceConfig rc = new PackagesResourceConfig("com.picturestory.service.api");
            IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory(rc, injector);
            // create and start a new instance of grizzly http server
            // exposing the Jersey application at BASE_URI
            return GrizzlyServerFactory.createHttpServer(BASE_URI, rc, ioc);
        }catch (IOException e){
            System.out.println("unable to start server");
            e.printStackTrace();
            return null;
        }

    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            System.out.println("server has started..");
            Thread.currentThread().join();
        }catch (Exception e){
            System.out.println("unable to start server");
            e.printStackTrace();
            return ;
        }
    }
}
