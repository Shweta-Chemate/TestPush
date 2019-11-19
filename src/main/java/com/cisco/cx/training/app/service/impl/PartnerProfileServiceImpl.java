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
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.service.PartnerProfileService;
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
		headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
		headers.set("Authorization", "Basic " + config.createCxpBasicAuthToken());
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
		
		UserDetails userDetails;
		try {
			ResponseEntity<String> result = restTemplate.exchange(entitlementUrl, HttpMethod.GET, requestEntity, String.class);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			userDetails = mapper.readValue(result.getBody(), UserDetails.class);
		} catch (JsonParseException e) {
			throw new GenericException(e.getMessage());
		} catch (JsonMappingException e) {
			throw new GenericException(e.getMessage());
		} catch (IOException e) {
			throw new GenericException(e.getMessage());
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
