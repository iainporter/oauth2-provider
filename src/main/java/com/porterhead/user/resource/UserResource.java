package com.porterhead.user.resource;

import com.porterhead.resource.BaseResource;
import com.porterhead.user.User;
import com.porterhead.user.UserService;
import com.porterhead.user.VerificationTokenService;
import com.porterhead.user.api.ApiUser;
import com.porterhead.user.api.CreateUserRequest;
import com.porterhead.user.api.CreateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/v1.0/user")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class UserResource extends BaseResource {

    private final static String USER_PATH = "v1.0/user/";
    @Context
    private UriInfo uriInfo;
    private UserService userService;
    private VerificationTokenService verificationTokenService;

    public UserResource(){}

    @Autowired
    public UserResource(final UserService userService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PermitAll
    @POST
    public Response signupUser(final CreateUserRequest request, @Context SecurityContext sc) {
        CreateUserResponse createUserResponse = userService.createUser(request, sc.getUserPrincipal());
        verificationTokenService.sendEmailRegistrationToken(createUserResponse.getApiUser().getId());
        URI location = uriInfo.getAbsolutePathBuilder().path(createUserResponse.getApiUser().getId()).build();
        return Response.created(location).entity(createUserResponse).build();
    }


    @RolesAllowed({"ROLE_USER"})
    @Path("{id}")
    @GET
    public ApiUser getUser(final @PathParam("id") String userId, final @Context SecurityContext securityContext) {
        User requestingUser = ensureUserIsAuthorized(securityContext, userId);
        return new ApiUser(requestingUser);
    }

}