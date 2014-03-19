package com.porterhead.user;

import com.porterhead.user.api.ApiUser;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Created by iainporter on 18/03/2014.
 */
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    public UserAuthenticationProvider(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getPrincipal() != null ? authentication.getPrincipal().toString() : null;
        String password = authentication.getCredentials() != null ? authentication.getCredentials().toString() : null;
        try {
            // create an authentication request
            final ApiUser apiUser = this.userService.authenticate(username, password);
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority("ROLE_USER")));
            token.setDetails(apiUser);
            return token;

        } catch (Exception e) {
            throw new OAuth2Exception(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
