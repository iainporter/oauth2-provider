package com.porterhead.user.resource;

import com.porterhead.resource.BaseResource;
import com.porterhead.user.User;
import com.porterhead.user.api.ApiUser;
import com.porterhead.user.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 31/05/2013
 */
@Path("/v1.0/me")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class MeResource extends BaseResource {

    @RolesAllowed({"ROLE_USER"})
    @GET
    public ApiUser getUser(final @Context SecurityContext securityContext) {
        User requestingUser = loadUserFromSecurityContext(securityContext);
        if(requestingUser == null) {
            throw new UserNotFoundException();
        }
        return new ApiUser(requestingUser);
    }
}
