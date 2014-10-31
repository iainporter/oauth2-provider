package com.porterhead.filter.spring;

import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iainporter on 31/10/2014.
 */
public class PayloadMappingAuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        LoginRequest loginRequest = getLoginRequest((HttpServletRequest)request);
        if(loginRequest != null) {
            Map<String, String[]> additionalParams = new HashMap<String, String[]>();
            additionalParams.put("username", new String[]{loginRequest.username});
            additionalParams.put("password", new String[]{loginRequest.password});
            additionalParams.put("grant_type", new String[]{ "password"});
            ModifiableRequestParametersWrapper wrapperRequest = new ModifiableRequestParametersWrapper((HttpServletRequest)request, additionalParams);
            chain.doFilter(wrapperRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * if there is a payload that maps then map it otherwise return null and rely on query parameters in the original request
     * @param request
     * @return
     */
    private LoginRequest getLoginRequest(HttpServletRequest request) {
        BufferedReader reader = null;
        LoginRequest loginRequest = null;
        try {
            reader = request.getReader();
            Gson gson = new Gson();
            loginRequest = gson.fromJson(reader, LoginRequest.class);
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                return null;
            }
        }
        return loginRequest;
    }

    private class LoginRequest {
        private String username;
        private String password;
    }

    @Override
    public void destroy() {

    }
}
