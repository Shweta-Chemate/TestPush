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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.RestResponseStatusExceptionResolver;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.constants.LoggerConstants;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.util.AuthorizationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

@Component
public class RBACFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private PropertyConfiguration propertyConfiguration;

	@Autowired
	ObjectMapper objectMapper;

	private Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider())
			.jsonProvider(new JacksonJsonProvider()).build();

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		logger.debug("Initializing RBAC Filter");
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain chain) throws IOException, ServletException {
		long requestStartTime = System.currentTimeMillis();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String path = request.getRequestURI();
		if (!StringUtils.isBlank(path) && StringUtils.indexOfAny(path,
				propertyConfiguration.getRbacExcludedEndPoints().split(Constants.COMMA)) == -1) {
			logger.debug("This operation requires RBAC check. RBAC filter started.");

		String xMasheryToken = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);
		if (StringUtils.isBlank(xMasheryToken)) {
			throw new BadRequestException("Mashery handshake is missing");
		} else {
			String puId = request.getHeader(Constants.PUID);
			if (StringUtils.isBlank(puId)) {
				throw new BadRequestException("PUID is missing in input request");
			}
			logger.debug("puId: " + puId);
			String userId = MasheryObject.getInstance(xMasheryToken).getCcoId();
			try {
				
				long apiStartTime = System.currentTimeMillis();
				String authResult;
				if(StringUtils.indexOfAny(path,"v1/partner/training/communities")>0)
				{
					String accessToken = request.getHeader(Constants.ACCESS_TOKEN);
					if (StringUtils.isBlank(accessToken)) {
						throw new BadRequestException("JWT Token is missing in input request");
					}
					logger.info("Invoking AuthZ API to authorize the user");
					authResult = AuthorizationUtil.invokeAuthzAPI(userId, puId, accessToken, propertyConfiguration,
							restTemplate);	
				}
				else
				{
					authResult = AuthorizationUtil.invokeAuthAPI(userId, puId, xMasheryToken, propertyConfiguration,
							restTemplate);
				}
				
				logger.info("TIME TAKEN | USER AUTHORIZE API RESPONSE = {}" , (System.currentTimeMillis() - apiStartTime));
				if (authResult != null) {

					logger.debug("Response from auth api : " + authResult);

					boolean requestValid = JsonPath.using(conf).parse(authResult).read("$.response");
					logger.debug("Is Request valid : " + requestValid);

					if (requestValid) {
						logger.info("User {} is performing the request {} on {}" ,userId, request.getMethod(), path);

					} else {
						logger.error("AUTH API Returned invalid response for >> " + request.getRequestURI());
						throw new NotAllowedException("Not Authorized ");
					}
				} else {
					logger.error("Result from AUth api is null for >> " + request.getRequestURI());
					throw new NotAllowedException("Not Authorized");
				}
				// if request is authorized, pass control to request chain
				logger.debug("RBAC filter completed: User is allowed access this resource.");
				chain.doFilter(servletRequest, servletResponse);

			} catch (Exception e) {

				Throwable cause = (e instanceof ServletException && e.getCause() != null) ? e.getCause() : e;
				ErrorResponse errorResponse = RestResponseStatusExceptionResolver.createErrorResponse(cause);
				response.setStatus(errorResponse.getStatus());
				response.setContentType(Constants.APPLICATION_JSON);
				response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
				response.getWriter().flush();
				response.getWriter().close();
			} finally {
				logger.debug("PERF_TIME_TAKEN REQUEST | " + request.getRequestURL() + " | "
						+ (System.currentTimeMillis() - requestStartTime) + " milli seconds");
			}
		}
		} else {
			logger.debug("No RBAC check required for this operation.");
		}
	}

	@Override
	public void destroy() {
		logger.warn("Destroying Auth filter");
	}

}
