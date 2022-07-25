package com.cisco.cx.training.util;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.constants.Constants;
import com.cisco.services.common.restclient.RequestBuilder;
import com.cisco.services.common.restclient.RestClient;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class AuthorizationUtil {

  private static final Logger logger = LoggerFactory.getLogger(AuthorizationUtil.class);

  public static String invokeAuthAPI(
      String userId,
      String puid,
      String masheryHeader,
      PropertyConfiguration propertyConfiguration,
      RestClient restClient) {
    logger.info("Input Param  {0} ,{1}  " + userId + " , " + puid);
    String response = null;
    try {
      URI uri =
          UriComponentsBuilder.fromUriString(propertyConfiguration.getAuthUrl())
              .queryParam("userId", userId)
              .queryParam("puId", Integer.parseInt(puid))
              .buildAndExpand(Map.of())
              .toUri();

      RequestBuilder<String> requestBuilder =
          restClient
              .request(String.class)
              .get()
              .uri(uri)
              .accept(MediaType.APPLICATION_JSON)
              .header(Constants.X_REQUEST_ID, MDC.get(Constants.REF_ID))
              .header(Constants.MASHERY_HANDSHAKE_HEADER_NAME, masheryHeader);
      ResponseEntity<String> result = requestBuilder.send();
      if (result.getStatusCode() == HttpStatus.OK) {
        response = result.getBody();
      } else {
        logger.error(
            "URL "
                + propertyConfiguration.getAuthUrl()
                + " Returned Status Code "
                + result.getStatusCode()
                + " Expected 200 ok Response :"
                + result.getBody());
      }
    } catch (Exception e) { // NOSONAR
      logger.error(e.getMessage(), e);
    }
    return response;
  }

  public static String invokeAuthzAPI(
      String puid,
      String accessToken,
      PropertyConfiguration propertyConfiguration,
      RestClient restClient) {
    logger.info("Input Param  {0} ,{1}  " + puid + " , " + Constants.RESOURCE_ID_LEARNING);
    String response = null;
    try {
      URI uri =
          UriComponentsBuilder.fromUriString(propertyConfiguration.getAuthUrl())
              .queryParam("puId", Integer.parseInt(puid))
              .queryParam(Constants.RESOURCE_ID_PARAM, Constants.RESOURCE_ID_LEARNING)
              .buildAndExpand(Map.of())
              .toUri();
      RequestBuilder<String> requestBuilder =
          restClient
              .request(String.class)
              .get()
              .uri(uri)
              .accept(MediaType.APPLICATION_JSON)
              .header(Constants.X_REQUEST_ID, MDC.get(Constants.REF_ID))
              .header(Constants.AUTHORIZATION, accessToken);
      ResponseEntity<String> result = requestBuilder.send();
      if (result.getStatusCode() == HttpStatus.OK) {
        response = result.getBody();
      } else {
        logger.error(
            "URL "
                + propertyConfiguration.getAuthUrl()
                + " Returned Status Code "
                + result.getStatusCode()
                + " Expected 200 ok Response :"
                + result.getBody());
      }
    } catch (Exception e) { // NOSONAR
      logger.error(e.getMessage(), e);
    }
    return response;
  }

  public static boolean isRoleCheckRequired(String roleId, String roleIdsforCustomerCheck) {
    if (roleIdsforCustomerCheck.indexOf(roleId) != -1) {
      return true;
    }
    return false;
  }
}
