package com.cisco.cx.training.app.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {
    private final static Logger LOG = LoggerFactory.getLogger(RestResponseStatusExceptionResolver.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int errorStatus;
        String errorMsg;
        String errorCode;

        if (ex instanceof BadRequestException) {
            errorStatus = 400;
            errorMsg = "Incorrect Request";
            errorCode = "API_INTERNAL_001";
        } else if (ex instanceof NotAllowedException) {
            errorStatus = 403;
            errorMsg = "Not Allowed";
            errorCode = "API_INTERNAL_002";
        } else if (ex instanceof NotFoundException) {
            errorStatus = 404;
            errorMsg = "Not Found";
            errorCode = "API_INTERNAL_003";
        } else if (ex instanceof HealthCheckException) {
            errorStatus = 500;
            errorMsg = "Health Check Error: " + Arrays.toString(((HealthCheckException) ex).getHealthStatus().entrySet().toArray());
            errorCode = "API_INTERNAL_004";
        } else {
            errorStatus = 500;
            errorMsg = "Server Error";
            errorCode = "API_INTERNAL_005";
        }

        response.setStatus(errorStatus);
        ErrorResponse errorBody = new ErrorResponse(errorStatus, ex.getMessage(), errorCode, errorMsg);

        LOG.error("Unexpected Error", ex);

        @SuppressWarnings("unchecked")
		ModelAndView mav = new ModelAndView("springboot-template-error", objectMapper.convertValue(errorBody, Map.class));
        mav.setView(new MappingJackson2JsonView());

        return mav;
    }
}
