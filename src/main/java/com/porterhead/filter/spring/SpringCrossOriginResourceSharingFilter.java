package com.porterhead.filter.spring;

import com.porterhead.filter.BaseCORSFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by iainporter on 26/07/2014.
 */
@Component
public class SpringCrossOriginResourceSharingFilter extends BaseCORSFilter implements Filter {

    @Value("${cors.allowed.origins}")
    String allowedOriginsString;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if(req instanceof HttpServletRequest) {
            if (((HttpServletRequest) req).getHeader("Origin") != null) {
                String origin = ((HttpServletRequest) req).getHeader("Origin");
                if (getAllowedOrigins(allowedOriginsString).contains(origin)) {
                    HttpServletResponse response = (HttpServletResponse) res;
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

                }
            }
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}