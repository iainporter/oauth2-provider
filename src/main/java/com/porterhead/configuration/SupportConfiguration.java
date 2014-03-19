package com.porterhead.configuration;

import com.porterhead.resource.GenericExceptionMapper;
import com.porterhead.resource.HealthCheckResource;
import com.porterhead.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class SupportConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Bean
    @Scope(value = "singleton")
    public GenericExceptionMapper genericExceptionMapper() {
        return new GenericExceptionMapper();
    }

    @Bean
    public HealthCheckResource healthCheckResource() {
        return new HealthCheckResource();
    }
}
