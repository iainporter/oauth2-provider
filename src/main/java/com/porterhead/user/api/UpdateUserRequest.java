package com.porterhead.user.api;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by iainporter on 13/08/2014.
 */
@XmlRootElement
public class UpdateUserRequest {

    private String firstName;
    private String lastName;

    @Email
    @NotNull
    private String emailAddress;

    public UpdateUserRequest(){}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}