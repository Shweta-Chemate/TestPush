package com.cisco.cx.training.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.MasheryUser;

public class RequestInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String masheryHeader = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);
		try {
			if (!StringUtils.isEmpty(masheryHeader)) {
				String userId = MasheryUser.getInstance(masheryHeader).getCcoId();
				if (userId == null) {
					throw new Exception("Logged in User is invalid");
				} else {
					MDC.put("userId", userId);
				}
			} else {
				throw new Exception("Mashrey Header is mandatory");
			}
		} catch (Exception e) {
			MDC.put("userId", "unknown-user");
			throw new BadRequestException(e.getMessage());

		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		MDC.clear();
	}
}
