package com.porterhead.sample;

import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by iainporter on 13/10/2014.
 */
@Path("/v1.0/samples")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class SampleResource {

    @RolesAllowed({"ROLE_GUEST"})
    @GET
    public Response getSample() {
        return Response.ok().entity("{\"message\":\"You are authorized to access\"}").build();
    }
}
