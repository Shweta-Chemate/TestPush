package com.cisco.cx.training.constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LoggerConstants {

	public static final Logger LOG = LoggerFactory.getLogger(LoggerConstants.class);

	public static final String REF_ID = "refId";

	public static final String USER_ID = "userId";
	public static final String USER_TYPE = "userType";
	public static final String PUID = "puid"; //customerId-req param, puid- header
	
	public static final String USER_ID_DEFAULT = "unknown user"; //mashery header ccoid
	public static final String USER_TYPE_DEFAULT = "Machine"; // "Machine"
	public static final String USER_TYPE_PERSON = "Person";
	public static final String PUID_DEFAULT = "no-puid"; // 	
	
	public static final String CLIENT_IP = "ClientIp";	
	public static final String TIME_TAKEN = "timeTaken";
	public static final String RESPONSE_STATUS = "responseStatus";
	
	public static final String STATUS_SUCCESS = "success";
	
	public static final String START_TIME = "startTime";	
	public static final String REQUEST_URI = "requestUri";	
	public static final String X_REQUEST_ID = "x-request-id";
	public static final String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";
	public static final String X_APIGW_REQUEST_ID = "x-apigw-request-id";
	

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

	/**
	 * 
	 * @param message
	 * @param hostOrClientIps
	 * @param status
	 * @param timeTakenMs
	 * @return
	 */
	public static String formatLog(String message,String status,long timeTakenMs)
	{	
		return message + ", "+ RESPONSE_STATUS +"=" + status + ", "+ TIME_TAKEN +"="+timeTakenMs;		
	}

}
