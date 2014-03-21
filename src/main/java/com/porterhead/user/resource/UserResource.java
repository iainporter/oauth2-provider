package com.porterhead.user.resource;

import com.porterhead.resource.BaseResource;
import com.porterhead.user.Role;
import com.porterhead.user.User;
import com.porterhead.user.UserService;
import com.porterhead.user.VerificationTokenService;
import com.porterhead.user.api.ApiUser;
import com.porterhead.user.api.CreateUserRequest;
import com.porterhead.user.api.CreateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

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
    private DefaultTokenServices tokenServices;
    private PasswordEncoder passwordEncoder;

    public UserResource(){}

    @Autowired
    public UserResource(final UserService userService, final VerificationTokenService verificationTokenService,
                        final DefaultTokenServices defaultTokenServices,
                        final PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.tokenServices = defaultTokenServices;
        this.passwordEncoder = passwordEncoder;
    }

    @PermitAll
    @POST
    public Response signupUser(final CreateUserRequest request, @Context SecurityContext sc) {
        ApiUser user = userService.createUser(request);
        CreateUserResponse createUserResponse = new CreateUserResponse(user, createTokenForNewUser(
                request, sc.getUserPrincipal().getName()));
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

    private OAuth2AccessToken createTokenForNewUser(CreateUserRequest createUserRequest, String clientId) {
        String hashedPassword = passwordEncoder.encode(createUserRequest.getPassword().getPassword());
        UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(
                createUserRequest.getUser().getEmailAddress(),
                hashedPassword, Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.toString())));
        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(clientId, Arrays.asList("read", "write"));
        authorizationRequest.setApproved(true);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, userAuthentication);
        return tokenServices.createAccessToken(oAuth2Authentication);
    }

}