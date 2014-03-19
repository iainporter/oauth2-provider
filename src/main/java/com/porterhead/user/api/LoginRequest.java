package com.porterhead.user.api;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
@XmlRootElement
public class LoginRequest {

    @Email
    @NotNull
    private String username;

    @Length(min = 8, max = 30)
    @NotNull
    private String password;

    public LoginRequest(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
