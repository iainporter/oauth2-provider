package com.porterhead;

import com.porterhead.exception.AccessDeniedExceptionMapper;
import com.porterhead.filter.jersey.JerseyCrossOriginResourceSharingFilter;
import com.porterhead.resource.GenericExceptionMapper;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ws.rs.container.ContainerResponseFilter;

/**
 * Created by iainporter on 28/07/2014.
 */
public class RestResourceApplication extends ResourceConfig {

    public RestResourceApplication() {

        packages("com.porterhead.resource", "com.porterhead.user.resource",
        "com.porterhead.sample");

        register(RequestContextFilter.class);

        ApplicationContext rootCtx = ContextLoader.getCurrentWebApplicationContext();
        ContainerResponseFilter filter = rootCtx.getBean(JerseyCrossOriginResourceSharingFilter.class);
        register(filter);

        register(GenericExceptionMapper.class);
        register(AccessDeniedExceptionMapper.class);

        register(JacksonFeature.class);

    }
}