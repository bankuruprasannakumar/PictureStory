package com.picturestory.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.picturestory.service.database.SolrModule;
import com.picturestory.service.network.CORSFilter;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import java.io.IOException;

/**
 * Created by bankuru on 29/4/16.
 */
public class Main {
//    private static final String BASE_URI = "http://10.14.125.134:4055/";
    public static HttpServer startServer(){
        try {
            Injector injector = Guice.createInjector(new SolrModule());
            // create a resource config that scans for JAX-RS resources and providers
            // in com.example.rest package
//        final ResourceConfig rc = new ResourceConfig().packages("com.picturestory.service.api");
            ResourceConfig rc = new PackagesResourceConfig("com.picturestory.service.api");
//            rc.getProperties().put(
//                    "com.sun.jersey.spi.container.ContainerResponseFilters",
//                    "com.sun.jersey.api.container.filter.LoggingFilter;com.myprogram.CrossDomainFilter"
//            );
            rc.getContainerResponseFilters().add(new CORSFilter());
            IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory(rc, injector);
            // create and start a new instance of grizzly http server
            // exposing the Jersey application at BASE_URI

            return GrizzlyServerFactory.createHttpServer(getBaseURI(), rc, ioc);
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

    public static String getBaseURI(){
        String localIp = System.getenv("AWS_PRIVATE_IPV4");
        String base_uri = "http://"+localIp+":4055/";
        System.out.println(base_uri);
        return base_uri;
    }
}
