package com.cisco.cx.training.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.User;

public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String masheryHeader = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);

        try {
            if (!StringUtils.isEmpty(masheryHeader)) {
                MDC.put("userId", User.getInstance(masheryHeader).getCcoId());
            } else {
                throw new Exception("No Header Value");
            }
        } catch (Exception e) {
            MDC.put("userId", "unknown-user");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
