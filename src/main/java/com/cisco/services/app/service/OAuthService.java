package com.cisco.services.app.service;

import com.cisco.services.app.config.PropertyConfiguration;
import com.cisco.services.models.OAuthBearerToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class OAuthService {
	private final Logger LOG = LoggerFactory.getLogger(OAuthService.class);

	@Autowired
	private PropertyConfiguration config;

	public OAuthBearerToken getCiscoOAuthToken(String client_id, String client_secret) throws RestClientException {
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("grant_type", "client_credentials");
		requestBody.add("client_id", client_id);
		requestBody.add("client_secret", client_secret);

		return this.getOAuthToken(config.getCiscoOauthTokenUrl(), requestBody);
	}

	public OAuthBearerToken getOAuthToken(String tokenUrl, MultiValueMap<String, String> requestBody) throws RestClientException {
		long oauthStartTime = System.currentTimeMillis();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			ResponseEntity<OAuthBearerToken> response = new RestTemplate()
					.postForEntity(tokenUrl, new HttpEntity<>(requestBody, headers), OAuthBearerToken.class);

			return response.getBody();
		} finally {
			LOG.info("PERF_TIME_TAKEN GET_OAUTH_TOKEN | " + tokenUrl + " | " + (System.currentTimeMillis() - oauthStartTime));
		}
	}
}
