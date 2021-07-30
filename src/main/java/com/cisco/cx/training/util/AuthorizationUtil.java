package com.cisco.cx.training.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.constants.Constants;

public class AuthorizationUtil {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationUtil.class);

	public static String invokeAuthAPI(String userId, String puid, String masheryHeader,
			PropertyConfiguration propertyConfiguration, RestTemplate restTemplate) {
		logger.info("Input Param  {0} ,{1}  " + userId + " , " + puid);
		String response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(Constants.MASHERY_HANDSHAKE_HEADER_NAME, masheryHeader);
			if (propertyConfiguration.createCxpBasicAuthToken() != null)
				headers.set("Authorization", "Basic " + propertyConfiguration.createCxpBasicAuthToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity requestEntity = new HttpEntity(null, headers);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(propertyConfiguration.getAuthUrl())
					.queryParam("userId", userId).queryParam("puId", Integer.parseInt(puid));
			ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity,
					String.class);
			logger.info("result  " + result);
			if (result.getStatusCode() == HttpStatus.OK)
				response = result.getBody();
			else {
				logger.error("URL " + propertyConfiguration.getAuthUrl() + " Returned Status Code "
						+ result.getStatusCode() + " Expected 200 ok Response :" + result.getBody());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}
	
	public static String invokeAuthzAPI(String puid, String accessToken,
			PropertyConfiguration propertyConfiguration, RestTemplate restTemplate) {
		logger.info("Input Param  {0} ,{1}  " + puid + " , " + Constants.RESOURCE_ID_LEARNING);
		String response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			if (propertyConfiguration.createCxpBasicAuthToken() != null)
				headers.set("Authorization", "Basic " + propertyConfiguration.createCxpBasicAuthToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(Constants.ACCESS_TOKEN, accessToken);
			HttpEntity requestEntity = new HttpEntity(null, headers);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(propertyConfiguration.getAuthUrl())
					.queryParam("puId", Integer.parseInt(puid)).queryParam(Constants.RESOURCE_ID_PARAM, Constants.RESOURCE_ID_LEARNING);
			ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity,
					String.class);
			logger.info("result  " + result);
			if (result.getStatusCode() == HttpStatus.OK)
				response = result.getBody();
			else {
				logger.error("URL " + propertyConfiguration.getAuthUrl() + " Returned Status Code "
						+ result.getStatusCode() + " Expected 200 ok Response :" + result.getBody());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}

	public static boolean isRoleCheckRequired(String roleId, String roleIdsforCustomerCheck) {
		if (roleIdsforCustomerCheck.indexOf(roleId) != -1)
			return true;
		return false;
	}
}