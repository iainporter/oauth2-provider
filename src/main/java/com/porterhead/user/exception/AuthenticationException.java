package com.porterhead.user.exception;

import com.porterhead.exception.BaseWebApplicationException;


public class AuthenticationException extends BaseWebApplicationException {

    public AuthenticationException() {
        super(401, "Authentication Error", "Authentication Error. The username or password were incorrect");
    }
}