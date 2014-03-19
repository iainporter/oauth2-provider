package com.porterhead.user.api;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
public class PasswordRequestTest {

    private static Validator validator;

    @Before
    public void setUp() {
       validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validPassword() {
        PasswordRequest request = new PasswordRequest("password");
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(0));
    }

    @Test
    public void passwordTooShort() {
        PasswordRequest request = new PasswordRequest(RandomStringUtils.randomAlphanumeric(7));
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(1));
        ConstraintViolation<?> constraintViolation = constraints.iterator().next();
        assertThat(constraintViolation.getPropertyPath().toString(), is("password"));
        assertThat(constraintViolation.getMessage(), is("length must be between 8 and 30"));
    }

    @Test
    public void passwordTooLong() {
        PasswordRequest request = new PasswordRequest(RandomStringUtils.randomAlphanumeric(36));
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(1));
        ConstraintViolation<?> constraintViolation = constraints.iterator().next();
        assertThat(constraintViolation.getPropertyPath().toString(), is("password"));
        assertThat(constraintViolation.getMessage(), is("length must be between 8 and 30"));
    }
}
