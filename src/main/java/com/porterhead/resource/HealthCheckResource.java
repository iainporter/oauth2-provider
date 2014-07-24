package com.porterhead.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("v1.0/healthcheck")
@Component
@Produces({MediaType.TEXT_PLAIN})
public class HealthCheckResource {

    @Value("${application.version}")
    String version;


    @PermitAll
    @GET
    public Response ping() {
        return Response.ok().entity("Running version " + version).build();
    }
}
