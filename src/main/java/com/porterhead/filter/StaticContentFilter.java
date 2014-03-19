package com.porterhead.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 22/05/2013
 */
public class StaticContentFilter implements Filter {

    private RequestDispatcher requestDispatcher;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        requestDispatcher = filterConfig.getServletContext().getNamedDispatcher("spring");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        requestDispatcher.forward(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
