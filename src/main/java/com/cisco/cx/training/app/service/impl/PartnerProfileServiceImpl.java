package com.cisco.cx.training.app.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.UserDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
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
		headers.set("Authorization", "Basic " + config.createCxpBasicAuthToken());
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> result = restTemplate.exchange(entitlementUrl + "/" + userId, HttpMethod.GET, requestEntity, String.class);
		LOGGER.info("Entitlement url response : {}", result.getBody());
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
	public String getEntitlementUrl() {
		return entitlementUrl;
	}

	@Override
	public void setEntitlementUrl(String entitlementUrl) {
		this.entitlementUrl = entitlementUrl;
	}
}
