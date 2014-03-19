package com.porterhead.configuration;

import com.porterhead.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 24/04/2013
 */
@Profile("dev")
@Configuration
public class ApplicationMockConfiguration {

    @Bean(name = "userRepository")
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    } 
}