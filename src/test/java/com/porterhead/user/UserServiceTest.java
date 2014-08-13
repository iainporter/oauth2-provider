package com.porterhead.user;

import com.porterhead.exception.ValidationException;
import com.porterhead.user.api.ApiUser;
import com.porterhead.user.api.CreateUserRequest;
import com.porterhead.user.api.PasswordRequest;
import com.porterhead.user.api.UpdateUserRequest;
import com.porterhead.user.exception.AuthenticationException;
import com.porterhead.user.exception.DuplicateUserException;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Transactional
public class UserServiceTest {


    protected String emailAddress = "foobar@example.com";
    protected String password = "password";
    protected final String id = "12345";
    protected UserRepository userRepository = Mockito.mock(UserRepository.class);
    protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    protected DefaultTokenServices tokenServices;
    protected UserServiceImpl userService;
    protected TokenStore tokenStore;

    @Before
    public void setup() {
        reset(userRepository);
        tokenStore = mock(TokenStore.class);
        tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        userService = new UserServiceImpl(userRepository, validator, new StandardPasswordEncoder());
        final User user = new User(getApiUser(), password, Role.ROLE_USER);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        });
    }

    @Test
    public void createNewUserNonExisting() throws Exception {
        mockEmailNotFound();
        ApiUser user = createUserWithRandomUserName();
        assertThat(user, is(not(Matchers.<Object>nullValue())));
        verify(userRepository, times(1)).save(Mockito.any(User.class));
        verify(userRepository, times(1)).findByEmailAddress(Mockito.any(String.class));
    }

    @Test(expected = DuplicateUserException.class)
    public void duplicateUser() throws Exception {
        mockEmailNotFound();
        CreateUserRequest request = getDefaultCreateUserRequest();
        userService.createUser(request);
        when(userRepository.findByEmailAddress(Mockito.any(String.class))).thenReturn(new User());
        //do again with same request
        userService.createUser(request);

    }

    @Test(expected = ValidationException.class)
    public void nullPasswordRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUser(getApiUser());
        userService.createUser(request);
    }

    @Test(expected = ValidationException.class)
    public void badNameRequest() {
        CreateUserRequest request = new CreateUserRequest();
        ApiUser user = getApiUser();
        user.setFirstName(RandomStringUtils.randomAlphabetic(101));
        request.setUser(user);
        request.setPassword(new PasswordRequest());
        userService.createUser(request);
    }

    @Test(expected = ValidationException.class)
    public void nullEmailRequest() {
        CreateUserRequest request = new CreateUserRequest();
        ApiUser user = new ApiUser();
        user.setFirstName("Foo");
        user.setLastName("Bar");
        request.setUser(user);
        request.setPassword(new PasswordRequest("password"));
        userService.createUser(request);
    }

    @Test
    public void loadUserByUserName() {
        User user = getDefaultUser();
        when(userRepository.findByEmailAddress(emailAddress)).thenReturn(user);
        assertThat(user, is(userService.loadUserByUsername(emailAddress)));
    }

    @Test(expected = AuthenticationException.class)
    public void userNotFoundByEmailAddress() {
        User user = getDefaultUser();
        when(userRepository.findByEmailAddress(createRandomEmailAddress())).thenReturn(user);
        userService.loadUserByUsername(emailAddress);
    }

    private void mockEmailNotFound() {
        Mockito.when(userRepository.findByEmailAddress(emailAddress)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        });
    }

    private ApiUser createUserWithRandomUserName() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        return userService.createUser(request);
    }

    private CreateUserRequest getDefaultCreateUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUser(getApiUser());
        request.setPassword(new PasswordRequest(password));
        return request;
    }

    private ApiUser getApiUser() {
        ApiUser user = new ApiUser();
        user.setEmailAddress(emailAddress);
        return user;
    }

    private User getDefaultUser() {
        User user = new User(getApiUser(), "password", Role.ROLE_USER);
        return user;
    }

    protected String createRandomEmailAddress() {
        return RandomStringUtils.randomAlphabetic(8) + "@example.com";
    }


}
