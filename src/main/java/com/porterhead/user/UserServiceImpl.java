package com.porterhead.user;

import com.porterhead.service.BaseService;
import com.porterhead.user.api.ApiUser;
import com.porterhead.user.api.CreateUserRequest;
import com.porterhead.user.api.CreateUserResponse;
import com.porterhead.user.exception.AuthenticationException;
import com.porterhead.user.exception.DuplicateUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.util.Assert.notNull;



@Service
public class UserServiceImpl extends BaseService implements UserService, UserDetailsService {

    private Logger LOG = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private DefaultTokenServices tokenServices;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, Validator validator, DefaultTokenServices tokenServices,
                           PasswordEncoder passwordEncoder) {
        super(validator);
        this.userRepository = userRepository;
        this.tokenServices = tokenServices;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return locateUser(username);
    }

    public CreateUserResponse createUser(final CreateUserRequest createUserRequest) {

        LOG.info("Validating user request.");
        validate(createUserRequest);
        final String emailAddress = createUserRequest.getUser().getEmailAddress().toLowerCase();
        if (userRepository.findByEmailAddress(emailAddress) == null) {
            LOG.info("User does not already exist in the data store - creating a new user [{}].",
                    emailAddress);
            User newUser = insertNewUser(createUserRequest);
            String hashedPassword = passwordEncoder.encode(createUserRequest.getPassword().getPassword());
            UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(emailAddress,
			hashedPassword, Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.toString())));
            DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest("app-mobile", Arrays.asList("read", "write"));
            authorizationRequest.setApproved(true);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, userAuthentication);
            OAuth2AccessToken token = tokenServices.createAccessToken(oAuth2Authentication);
            LOG.debug("Created new user [{}].", newUser.getEmailAddress());
            return new CreateUserResponse(newUser, token);
        } else {
            LOG.info("Duplicate user located, exception raised with appropriate HTTP response code.");
            throw new DuplicateUserException();
        }
    }

    @Override
    public ApiUser authenticate(String username, String password) {
        Assert.notNull(username);
        Assert.notNull(password);
        User user = locateUser(username);
        if(!passwordEncoder.encode(password).equals(user.getHashedPassword())) {
            throw new AuthenticationException();
        }
        return new ApiUser(user);
    }

    /**
     * Locate the user and throw an exception if not found.
     *
     * @param username
     * @return a User object is guaranteed.
     * @throws AuthenticationException if user not located.
     */
    private User locateUser(final String username) {
        notNull(username, "Mandatory argument 'username' missing.");
        User user = userRepository.findByEmailAddress(username.toLowerCase());
        if (user == null) {
            LOG.debug("Credentials [{}] failed to locate a user - hint, username.", username.toLowerCase());
            throw new AuthenticationException();
        }
        return user;
    }

    private User insertNewUser(final CreateUserRequest createUserRequest) {
        String hashedPassword = passwordEncoder.encode(createUserRequest.getPassword().getPassword());
        User newUser = new User(createUserRequest.getUser(), hashedPassword, Role.ROLE_USER);
        return userRepository.save(newUser);
    }

}