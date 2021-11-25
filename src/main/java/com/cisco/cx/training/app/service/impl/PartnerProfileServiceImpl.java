package com.cisco.cx.training.app.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.constants.LoggerConstants;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PartnerProfileServiceImpl implements PartnerProfileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PartnerProfileServiceImpl.class);

	RestTemplate restTemplate = new RestTemplate();

	@Autowired
	PropertyConfiguration config;

	@Value("${cxpp.entitlement.user.profile.url}")
	public String entitlementUrl;

	private static final String X_MASHERY_HANSHAKE = "X-Mashery-Handshake";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public UserDetails fetchUserDetails(String xMasheryHandshake) {
		HttpHeaders headers = new HttpHeaders();
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
		//headers.set("Authorization", "Basic " + config.createCxpBasicAuthToken());
		addHeaders(headers);
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> result = restTemplate.exchange(entitlementUrl + "/" + userId, HttpMethod.GET, requestEntity, String.class);
		LOGGER.info("Entitlement url response = {}",  result.getStatusCode().value()!= HttpStatus.OK.value()?result.getBody():"call completed.");
		UserDetails userDetails = null;
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			userDetails = mapper.readValue(result.getBody(), UserDetails.class);
		} catch (IOException | HttpClientErrorException e) {
			LOGGER.error("Error while invoking the entitlement API", e);
		} 
		return userDetails;
	}
	
	@Override
	public UserDetailsWithCompanyList fetchUserDetailsWithCompanyList(String xMasheryHandshake) {

		UserDetailsWithCompanyList userDetails = null;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
//		requestHeaders.set("Authorization", "Basic " + config.createCxpBasicAuthToken());
		addHeaders(requestHeaders);
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
		ResponseEntity<String> result = restTemplate.exchange(config.getPartnerUserDetails(), HttpMethod.GET,requestEntity, String.class);
		LOGGER.info("Prtner user details URL response = {}",result.getStatusCode().value() != HttpStatus.OK.value() ? result.getBody() : "call completed.");
		if(result==null)
			throw new GenericException("user details api failed");
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			userDetails = mapper.readValue(result.getBody(), UserDetailsWithCompanyList.class);
		} catch (IOException | HttpClientErrorException e) {
			LOGGER.error("Error while invoking the user details  API", e);
		}
		return userDetails;
	}
	
	@Override
	public String getEntitlementUrl() {
		return entitlementUrl;
	}

	@Override
	public void setEntitlementUrl(String entitlementUrl) {
		this.entitlementUrl = entitlementUrl;
	}
	
	private void addHeaders(HttpHeaders requestHeaders)
	{
		//1.
		String xRequestId = MDC.get(LoggerConstants.REF_ID);
		LOGGER.info("PPS header...{}",xRequestId);
		if(xRequestId!=null)
		{			
			requestHeaders.add(LoggerConstants.X_REQUEST_ID, xRequestId);		
		}			
	}
}
