package com.porterhead.user;

import com.porterhead.user.api.ApiUser;
import com.porterhead.user.api.CreateUserResponse;
import com.porterhead.user.api.CreateUserRequest;

import java.security.Principal;

public interface UserService {

    public CreateUserResponse createUser(final CreateUserRequest createUserRequest, Principal principal);

    public ApiUser authenticate(String username, String password);

}
