package com.porterhead.user.api;

import com.porterhead.user.Gender;
import com.porterhead.user.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.security.Principal;

import static org.springframework.util.Assert.notNull;

@XmlRootElement
public class ApiUser implements Principal {

    @Email
    @NotNull
    private String emailAddress;

    private String firstName;
    private String lastName;
    private Integer age;
    private String id;

    public ApiUser() {
    }

    public ApiUser(User user) {
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.id = user.getId();
        this.age = user.getAge();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        notNull(emailAddress, "Mandatory argument 'emailAddress missing.'");
        this.emailAddress = emailAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        notNull(id, "Mandatory argument 'id missing.'");
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(final String firstName) {
        notNull(firstName, "Mandatory argument 'firstName missing.'");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        notNull(lastName, "Mandatory argument 'lastName missing.'");
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String getName() {
        return this.getEmailAddress();
    }
}