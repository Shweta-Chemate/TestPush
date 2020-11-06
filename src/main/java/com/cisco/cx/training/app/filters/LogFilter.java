package com.cisco.cx.training.app.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cisco.cx.training.constants.LoggerConstants;


@Component
public class LogFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing LogFilter");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			final FilterChain chain) throws IOException, ServletException {

		logger.info("inside LogFilter");
		LoggerConstants.setLogData((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse);
		chain.doFilter(servletRequest, servletResponse);
		logger.info("Completed LogFilter");
	}

	@Override
	public void destroy() {
		logger.info("Destroying LogFilter");
	}

}
