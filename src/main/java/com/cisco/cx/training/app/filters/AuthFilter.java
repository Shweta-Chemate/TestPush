package com.cisco.cx.training.app.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cisco.cx.training.app.exception.NotAllowedException;

@Component
public class AuthFilter implements Filter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    
    private static final String READY_URI = "/ready";
    
    private static final String LIVE_URI = "/live";
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOG.info("Initializing Auth Filter");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        long requestStartTime = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        if ((path.endsWith(LIVE_URI))||
        		(path.endsWith(READY_URI))) {
        	chain.doFilter(request, response);
            return;
        }
        try {
            LOG.info("BEGIN_REQUEST: {}", req.getRequestURI());
            // check if request is authorized
            this.isAuthorized(req);
            // if request is authorized, pass control to request chain
            chain.doFilter(request, response);
        } finally {
            LOG.info("PERF_TIME_TAKEN REQUEST | " + req.getRequestURL() + " | " + (System.currentTimeMillis() - requestStartTime));
        }
    }

    /**
     * isAuthorized - authorizes the http request
     * @param req - the http request object
     * @throws NotAllowedException - exception to reject the request
     */
    private void isAuthorized(HttpServletRequest req) throws NotAllowedException {
        LOG.info("Start AUTH_FILTER: ##### Add logic here to authorize your request: " + req.getRequestURI());
        long authStartTime = System.currentTimeMillis();
        try {
            //TODO add authorization logic here

        } finally {
            LOG.info("PERF_TIME_TAKEN AUTH_FILTER | " + req.getRequestURI() + " | " + (System.currentTimeMillis() - authStartTime));
        }
    }


    @Override
    public void destroy() {
        LOG.warn("Destroying Auth filter");
    }
}
