package com.porterhead.user.api;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
public class CreateUserRequestTest extends BaseApiTest {

    @Test
    public void validRequest() {
        ApiUser user = getValidUser();
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.randomAlphanumeric(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(0));
    }

    @Test
    public void nullUser() {
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.randomAlphabetic(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(null, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(1));
    }

    @Test
    public void invalidPassword() {
        ApiUser user = getValidUser();
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.randomAlphabetic(6));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(1));
    }

    private ApiUser getValidUser() {
        ApiUser user = new ApiUser();
        user.setEmailAddress(RandomStringUtils.randomAlphabetic(8) + "@example.com");
        user.setFirstName("Foo");
        user.setLastName("Bar");
        return user;
    }


}