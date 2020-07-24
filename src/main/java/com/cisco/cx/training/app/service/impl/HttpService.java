package com.cisco.cx.training.app.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HttpService {
	private final Logger LOG = LoggerFactory.getLogger(HttpService.class);

	private RestTemplate restTemplate = new RestTemplate();
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 *
	 * @param url - the http endpoint
	 * @param reqParams - any request parameters to be sent in the request
	 * @param headers - any request headers
	 * @param responseType - the class model for the object structure of the json response from the api
	 * @return - an object instance of the response body
	 * @throws RestClientException - thrown if any network or http error happens.
	 */
	public <T> T makeHttpGetCall(String url, Map<String, Object> reqParams, HttpHeaders headers, Class<T> responseType) throws
			RestClientException {
		long httpStartTime = System.currentTimeMillis();
		try {
			LOG.debug("Making HTTP GET call to {} with parameters {} and headers {}", url, reqParams, headers);

			return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType, reqParams).getBody();
		} finally {
			LOG.info("PERF_TIME_TAKEN HTTP_GET | " + url + " | " + (System.currentTimeMillis() - httpStartTime));
		}
	}

	/**
	 * makeHttpPostCallUrlEncoded - make an HTTP post call and pass Url encoded parameters
	 * @param url - the http endpoint
	 * @param bodyParams - a map of the body parameters to be sent in request
	 * @param headers - any request headers
	 * @param responseType - the class model for the object structure of the json response from the api
	 * @param urlParams - a map of the url parameters to be replaced in the url
	 * @return - an object instance of the response body
	 * @throws RestClientException - thrown if any network or http error happens.
	 */
	public <T> T makeHttpPostCallUrlEncoded(String url, MultiValueMap<String, String> bodyParams, HttpHeaders headers, Class<T> responseType, Map<String, Object> urlParams)
			throws RestClientException {
		long httpStartTime = System.currentTimeMillis();
		try {
			headers = (headers == null) ? new HttpHeaders() : headers;
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			LOG.debug("Making HTTP POST call to {} with URL Encoded params {} with headers {}", url, bodyParams, headers);

			return restTemplate.postForEntity(url, new HttpEntity<>(bodyParams, headers), responseType, urlParams).getBody();
		} finally {
			LOG.info("PERF_TIME_TAKEN HTTP_POST_URLENCODED | " + url + " | " + (System.currentTimeMillis() - httpStartTime));
		}
	}

	/**
	 * makeHttpPostCallWithBody - make an HTTP post call and pass a JSON request body
	 * @param url - the http endpoint
	 * @param requestObject - the pojo object to be sent as payload
	 * @param headers - any request headers
	 * @param responseType - the class model for the object structure of the json response from the api
	 * @return - an object instance of the response body
	 * @throws RestClientException - thrown if any network or http error happens.
	 */
	public <R, T> T makeHttpPostCallWithBody(String url, R requestObject, HttpHeaders headers, Class<T> responseType) throws RestClientException {
		long httpStartTime = System.currentTimeMillis();
		try {
			headers = (headers == null) ? new HttpHeaders() : headers;
			headers.setContentType(MediaType.APPLICATION_JSON);

			String requestBody = null;
			if (requestObject != null) {
				try {
					// converting object to json using jackson (thanks James (jjithin) for the tip)
					requestBody = mapper.writeValueAsString(requestObject);
				} catch (Exception e) {
					LOG.warn("Could not serialize object to JSON using ObjectMapper, using toString instead.", e);
					// if any error, default to toString
					requestBody = requestObject.toString();
				}
			}

			LOG.debug("Making HTTP POST call to {} with body {} and headers {}", url, requestBody, headers);

			return restTemplate.postForEntity(url, new HttpEntity<>(requestBody, headers), responseType).getBody();
		} finally {
			LOG.info("PERF_TIME_TAKEN HTTP_POST_BODY | " + url + " | " + (System.currentTimeMillis() - httpStartTime));
		}
	}

	/**
	 * makeHttpPutCallWithBody - make an HTTP PUT call and pass a JSON request body
	 * @param url - the http endpoint
	 * @param requestObject - the pojo object to be sent as payload
	 * @param headers - any request headers
	 * @param responseType - the class model for the object structure of the json response from the api
	 * @return - an object instance of the response body
	 * @throws RestClientException - thrown if any network or http error happens.
	 */
	public <R, T> T makeHttpPutCallWithBody(String url, R requestObject, HttpHeaders headers, Class<T> responseType) throws RestClientException {
		long httpStartTime = System.currentTimeMillis();
		try {
			headers = (headers == null) ? new HttpHeaders() : headers;
			headers.setContentType(MediaType.APPLICATION_JSON);

			String requestBody = null;
			if (requestObject != null) {
				try {
					requestBody = mapper.writeValueAsString(requestObject);
				} catch (Exception e) {
					LOG.warn("Could not serialize object to JSON using ObjectMapper, using toString instead.", e);
					// if any error, default to toString
					requestBody = requestObject.toString();
				}
			}

			LOG.debug("Making HTTP PUT call to {} with body {} and headers {}", url, requestBody, headers);

			return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(requestBody, headers), responseType).getBody();
		} finally {
			LOG.info("PERF_TIME_TAKEN HTTP_PUT_BODY | " + url + " | " + (System.currentTimeMillis() - httpStartTime));
		}
	}
}