/*
 * This filter provides CORS support allowing resources to be requested from other domains
 * This is important if your client and your server do not reside on the same server or are running on different ports
 */
package com.porterhead.filter.jersey;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "X-HTTP-Method-Override, Content-Type, x-requested-with");
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }


}
