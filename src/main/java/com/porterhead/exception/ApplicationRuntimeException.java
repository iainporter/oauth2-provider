package com.porterhead.exception;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 26/04/2013
 */
public class ApplicationRuntimeException extends BaseWebApplicationException {

    public ApplicationRuntimeException(String applicationMessage) {
        super(500, "Internal System error", applicationMessage);
    }
}
