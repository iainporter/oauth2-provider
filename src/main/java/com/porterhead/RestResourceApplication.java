package com.porterhead;

import com.porterhead.filter.jersey.CrossOriginResourceSharingFilter;
import com.porterhead.resource.GenericExceptionMapper;
import com.porterhead.resource.HealthCheckResource;
import com.porterhead.user.resource.UserResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * Created by iainporter on 28/07/2014.
 */
public class RestResourceApplication extends ResourceConfig {

    public RestResourceApplication() {

        packages("com.porterhead.resource", "com.porterhead.user.resource");

        register(RequestContextFilter.class);
        register(CrossOriginResourceSharingFilter.class);

        register(GenericExceptionMapper.class);

        register(JacksonFeature.class);

    }
}