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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RequestInterceptor implements HandlerInterceptor {
	private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String masheryHeader = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);
		
		LoggerConstants.setMdc();    
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
		

		/* logger starts */
				
        // set the request URI
        MDC.put(LoggerConstants.REQUEST_URI, request.getRequestURI());
        
		// set a referenceId to track this API request
        // refId is API GW request ID for UI calls
        if(request.getHeader(LoggerConstants.X_APIGW_REQUEST_ID)!=null){
            MDC.put(LoggerConstants.REF_ID, request.getHeader(LoggerConstants.X_APIGW_REQUEST_ID));
        } else if (request.getHeader(LoggerConstants.X_REQUEST_ID)!=null) {
            // refId is X-Request-ID for M2M calls
            MDC.put(LoggerConstants.REF_ID, request.getHeader(LoggerConstants.X_REQUEST_ID));
        } 
        
        String puid= request.getHeader(LoggerConstants.PUID)+"";        
        if(!StringUtils.isEmpty(puid))MDC.put(LoggerConstants.PUID,puid);
        
        //Map<String, String> map = new HashMap<>();
        List<String> requestHeaderKeys = new ArrayList<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            if(key.trim().equalsIgnoreCase(LoggerConstants.AUTHORIZATION)) continue;
            requestHeaderKeys.add(key);
            MDC.put(key, request.getHeader(key));
        }

        if(request.getHeader(LoggerConstants.X_ORIGINAL_FORWARDED_FOR) != null){
            MDC.put(LoggerConstants.X_ORIGINAL_FORWARDED_FOR, request.getHeader(LoggerConstants.X_ORIGINAL_FORWARDED_FOR));
        }

        if(request.getHeader(LoggerConstants.X_REQUEST_ID) != null){
            MDC.put(LoggerConstants.X_REQUEST_ID, request.getHeader(LoggerConstants.X_REQUEST_ID));
            // set the request_id in the response header
            response.setHeader(LoggerConstants.X_REQUEST_ID, MDC.get(LoggerConstants.X_REQUEST_ID));
        }

        LOG.info("START_REQUEST method={} path={}", request.getMethod(), MDC.get(LoggerConstants.REQUEST_URI));

        requestHeaderKeys.forEach(MDC::remove);       
        
        /* logger ends */

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LOG.info("END_REQUEST method={} path={} responseStatus={} timetaken={}", request.getMethod(), MDC.get(LoggerConstants.REQUEST_URI), response.getStatus(), (System.currentTimeMillis() - Long.parseLong(MDC.get(LoggerConstants.START_TIME))));
		MDC.clear();
	}
}
