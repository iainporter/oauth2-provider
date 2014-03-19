package com.porterhead.user.api;

import org.junit.Before;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
public class BaseApiTest {

    protected Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
