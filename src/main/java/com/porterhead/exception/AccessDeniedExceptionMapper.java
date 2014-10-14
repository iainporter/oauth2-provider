package com.porterhead.exception;

import com.porterhead.api.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by iainporter on 14/10/2014.
 */
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("401", "You do not have the appropriate privileges to access this resource",
                        exception.getMessage()))
                .type(MediaType.APPLICATION_JSON).
                        build();
    }
}
