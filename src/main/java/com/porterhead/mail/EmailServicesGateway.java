package com.porterhead.mail;

import org.springframework.integration.annotation.Gateway;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public interface EmailServicesGateway {

    @Gateway(requestChannel = "emailVerificationRouterChannel")
    public void sendVerificationToken(EmailServiceTokenModel model);
}
