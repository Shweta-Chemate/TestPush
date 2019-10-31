package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.impl.PartnerProfileServiceImpl;
import com.cisco.cx.training.models.UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
public class PartnerProfileServiceTest {
	@Mock
	private PropertyConfiguration config;

	@Mock
	RestTemplate restTemplate;

	@InjectMocks
	private PartnerProfileService partnerProfileService = new PartnerProfileServiceImpl();

	private static final String X_MASHERY_HANSHAKE = "X-Mashery-Handshake";

	@Test
	public void fetchUserDetails() throws JsonProcessingException {
		partnerProfileService.setEntitlementUrl("");
		when(config.createCxpBasicAuthToken()).thenReturn("");
		HttpHeaders headers = new HttpHeaders();
		String xMasheryHandshake = "";
		headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
		headers.set("Authorization", "Basic " + "");
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> result = new ResponseEntity<>(getUserDetails(), HttpStatus.OK);
		when(restTemplate.exchange("", HttpMethod.GET, requestEntity, String.class)).thenReturn(result);
		partnerProfileService.fetchUserDetails(xMasheryHandshake);
		when(restTemplate.exchange("", HttpMethod.GET, requestEntity, String.class)).thenReturn(result);
	}

	private String getUserDetails() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("cecId");
		userDetails.setCity("city");
		userDetails.setCompany("company");
		userDetails.setCountry("country");
		userDetails.setEmail("email");
		userDetails.setFirstName("firstName");
		userDetails.setLastName("lastName");
		userDetails.setLevel("level");
		userDetails.setPhone("phone");
		userDetails.setPicture("picture");
		userDetails.setRole("role");
		userDetails.setState("state");
		userDetails.setStreet("street");
		userDetails.setTitle("title");
		userDetails.setZipcode("zipcode");

		return mapper.writeValueAsString(userDetails);
	}

}
