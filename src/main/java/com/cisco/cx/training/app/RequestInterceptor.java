package com.cisco.cx.training.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.constants.LoggerConstants;
import com.cisco.cx.training.models.MasheryUser;


public class RequestInterceptor implements HandlerInterceptor {
	private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String masheryHeader = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);
		try {
			if (!StringUtils.isEmpty(masheryHeader)) {
				String userId = MasheryUser.getInstance(masheryHeader).getCcoId();
				if (userId == null) {
					throw new Exception("Logged in User is invalid");
				} else {
					MDC.put(LoggerConstants.USER_ID, userId);
					MDC.put(LoggerConstants.USER_TYPE, LoggerConstants.USER_TYPE_PERSON);
				}
			} else {
				throw new Exception("Mashrey Header is mandatory");
			}
		} catch (Exception e) {
			MDC.put(LoggerConstants.USER_ID, LoggerConstants.USER_ID_DEFAULT);
			throw new BadRequestException(e.getMessage());

		}		
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LOG.info("END_REQUEST method={} path={} responseStatus={} timetaken={}", request.getMethod(), MDC.get(LoggerConstants.REQUEST_URI), response.getStatus(), (System.currentTimeMillis() - Long.parseLong(MDC.get(LoggerConstants.START_TIME))));
		MDC.clear();
	}
}
