package com.porterhead.user.api;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
@XmlRootElement
public class LostPasswordRequest {

    private String emailAddress;

    public LostPasswordRequest() {}

    public LostPasswordRequest(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @NotNull
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
