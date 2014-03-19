package com.porterhead.user.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
@XmlRootElement
public class CreateUserRequest {

    @NotNull
    @Valid
    private ApiUser user;

    @NotNull
    @Valid
    private PasswordRequest password;


    public CreateUserRequest() {
    }

    public CreateUserRequest(final ApiUser user, final PasswordRequest password) {
        this.user = user;
        this.password = password;
    }

    public ApiUser getUser() {
        return user;
    }

    public void setUser(ApiUser user) {
        this.user = user;
    }

    @NotNull
    @Valid
    public PasswordRequest getPassword() {
        return password;
    }

    public void setPassword(PasswordRequest password) {
        this.password = password;
    }
}
