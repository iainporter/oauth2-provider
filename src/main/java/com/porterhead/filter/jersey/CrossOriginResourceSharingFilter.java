/*
 * This filter provides CORS support allowing resources to be requested from other domains
 * This is important if your client and your server do not reside on the same server or are running on different ports
 */
package com.porterhead.filter.jersey;


import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.ext.Provider;

@Provider
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        response.getHttpHeaders().putSingle("Access-Control-Allow-Origin", "*");
        response.getHttpHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.getHttpHeaders().putSingle("Access-Control-Allow-Headers", "X-HTTP-Method-Override, Content-Type, x-requested-with");
        response.getHttpHeaders().putSingle("Access-Control-Max-Age", "3600");
        return response;
    }

}
