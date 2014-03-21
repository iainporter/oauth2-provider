package com.porterhead.configuration;

import com.porterhead.mail.MailSenderService;
import com.porterhead.user.*;
import com.porterhead.user.resource.MeResource;
import com.porterhead.user.resource.PasswordResource;
import com.porterhead.user.resource.UserResource;
import com.porterhead.user.resource.VerificationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import javax.validation.Validator;

@Configuration
public class UserConfiguration {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private Validator validator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Bean
    public VerificationTokenService verificationTokenService() {
        return new VerificationTokenServiceImpl(userRepository, verificationTokenRepository, mailSenderService, validator, passwordEncoder);
    }
    
    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository, validator, passwordEncoder);
    } 
    
    @Bean
    public UserResource userResource() {
        return new UserResource(userService(), verificationTokenService(), tokenServices, passwordEncoder);
    }

    @Bean
    public PasswordResource passwordResource() {
        return new PasswordResource();
    }

    @Bean
    public VerificationResource verificationResource() {
        return new VerificationResource();
    }

    @Bean
    public MeResource meResource() {
        return new MeResource();
    }

}
