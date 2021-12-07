package com.cisco.cx.training.constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import com.cisco.cx.training.models.MasheryObject;

public class LoggerConstants {

	public static final Logger LOG = LoggerFactory.getLogger(LoggerConstants.class);

	public static final String REF_ID = "refId";

	public static final String USER_ID = "userId";
	public static final String USER_TYPE = "userType";
	public static final String USER_ROLE_ID = "roleId";
	public static final String PUID = "puid"; //customerId-req param, puid- header
	
	public static final String USER_ID_DEFAULT = "unknown user"; //mashery header ccoid
	public static final String USER_TYPE_DEFAULT = "Machine"; // "Machine"
	public static final String USER_TYPE_PERSON = "Person";
	public static final String PUID_DEFAULT = "no-puid"; // 	
	
	public static final String CLIENT_IP = "clientIp";	
	public static final String TIME_TAKEN = "timeTaken";
	public static final String RESPONSE_STATUS = "responseStatus";
	
	public static final String STATUS_SUCCESS = "success";
	
	public static final String START_TIME = "startTime";	
	public static final String REQUEST_URI = "requestUri";	
	public static final String X_REQUEST_ID = "x-request-id";
	public static final String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";
	public static final String X_APIGW_REQUEST_ID = "x-apigw-request-id";
	
	public static final String AUTHORIZATION = "authorization";
	public static final String CUSTOMER_ID = "customerId";

	/**
	 * other : "policyName"; //privilege level
	 */
	public static void setMdc()
	{		 
		   //set a start timestamp 
		MDC.put(LoggerConstants.START_TIME, String.valueOf(System.currentTimeMillis()));
		
		MDC.put(REF_ID, UUID.randomUUID().toString());

		MDC.put(USER_ID,USER_ID_DEFAULT);
		MDC.put(USER_TYPE,USER_TYPE_DEFAULT);

		MDC.put(PUID,PUID_DEFAULT);
		
		MDC.put(CLIENT_IP,getSourceOrClientIps());
	}

	/**
	 * 
	 * @return
	 */
	public static String getSourceOrClientIps()
	{
		String Ips="";
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LOG.warn("Error occurred while getting clientIps...");
		}
		return Ips;
	}	


	public static void setLogData(HttpServletRequest request,HttpServletResponse response)
	{
		LoggerConstants.setMdc(); 		
		
		String xMasheryToken = request.getHeader(Constants.MASHERY_HANDSHAKE_HEADER_NAME);
		String userId = StringUtils.isEmpty(xMasheryToken)?"":MasheryObject.getInstance(xMasheryToken).getCcoId();
		String puidHeader = request.getHeader(Constants.PUID);
		String puidReqParam = request.getParameter(Constants.PUID);
		String puId = puidReqParam == null ? puidHeader : puidReqParam;		
		String customerIdReqParam = request.getParameter(LoggerConstants.CUSTOMER_ID);		
		
		if(!StringUtils.isEmpty(userId))
		{
			MDC.put(LoggerConstants.USER_ID, userId);
			MDC.put(USER_TYPE,USER_TYPE_PERSON);
		}
		if(!StringUtils.isEmpty(puId)) {MDC.put(LoggerConstants.PUID, puId);}
		if(!StringUtils.isEmpty(customerIdReqParam)) {MDC.put(LoggerConstants.CUSTOMER_ID, customerIdReqParam);}
		
		
		/* logger starts */

		// set the request URI
		MDC.put(LoggerConstants.REQUEST_URI, request.getRequestURI());

		// set a referenceId to track this API request

		// refId is API GW request ID for UI calls
		if(request.getHeader(LoggerConstants.X_APIGW_REQUEST_ID)!=null)
		{
			MDC.put(LoggerConstants.REF_ID, request.getHeader(LoggerConstants.X_APIGW_REQUEST_ID));
		}
		else if (request.getHeader(LoggerConstants.X_REQUEST_ID)!=null) 
		{
			// refId is X-Request-ID for M2M calls
			MDC.put(LoggerConstants.REF_ID, request.getHeader(LoggerConstants.X_REQUEST_ID));
		} 
		else
		{
			MDC.put(LoggerConstants.REF_ID, UUID.randomUUID().toString());
		}

		String puid= request.getHeader(LoggerConstants.PUID)+"";        
		if(!StringUtils.isEmpty(puid)) {MDC.put(LoggerConstants.PUID,puid);}

		//Map<String, String> map = new HashMap<>();
		List<String> requestHeaderKeys = new ArrayList<>();
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			if(key.trim().equalsIgnoreCase(LoggerConstants.AUTHORIZATION)) {continue;}
			if(key.trim().equalsIgnoreCase(LoggerConstants.X_REQUEST_ID)) {continue;}
			requestHeaderKeys.add(key);
			MDC.put(key, request.getHeader(key));
		}

		if(request.getHeader(LoggerConstants.X_ORIGINAL_FORWARDED_FOR) != null){
			MDC.put(LoggerConstants.CLIENT_IP, request.getHeader(LoggerConstants.X_ORIGINAL_FORWARDED_FOR));
		}       
		response.setHeader(LoggerConstants.X_REQUEST_ID, MDC.get(LoggerConstants.REF_ID));     

		LOG.info("START_REQUEST method={} path={}", request.getMethod(), MDC.get(LoggerConstants.REQUEST_URI));        
		requestHeaderKeys.forEach(MDC::remove);       


		/* logger ends */
	}

}
