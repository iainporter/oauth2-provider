package com.porterhead.security;

import com.google.gson.Gson;
import com.porterhead.exception.BadClientCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by iainporter on 16/10/2014.
 */
@Component
public class OAuthRestEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Gson gson = new Gson();
        String errorEntity = gson.toJson(new BadClientCredentialsException("Bad client credentials"));
        response.getOutputStream().print(errorEntity);
    }
}
