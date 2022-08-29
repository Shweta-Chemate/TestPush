package com.cisco.cx.training.app.service.impl;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.PLSResponse;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.services.common.restclient.RequestBuilder;
import com.cisco.services.common.restclient.RestClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PartnerProfileServiceImpl implements PartnerProfileService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PartnerProfileServiceImpl.class);

  PropertyConfiguration config;

  @Autowired private RestClient restClient;

  @Autowired
  public PartnerProfileServiceImpl(PropertyConfiguration config) {
    this.config = config;
  }

  @Value("${cxpp.entitlement.user.profile.url}")
  public String entitlementUrl;

  private static final String X_MASHERY_HANSHAKE = "X-Mashery-Handshake";

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public UserDetails fetchUserDetails(String xMasheryHandshake) {
    String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
    URI uri =
        UriComponentsBuilder.fromUriString(entitlementUrl + "/" + userId)
            .buildAndExpand(Map.of())
            .toUri();
    ResponseEntity<String> result =
        request(xMasheryHandshake).method(HttpMethod.GET).uri(uri).send();

    LOGGER.info(
        "Entitlement url response = {}",
        result.getStatusCode().value() != HttpStatus.OK.value()
            ? result.getBody()
            : "call completed.");
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
    URI uri =
        UriComponentsBuilder.fromUriString(config.getPartnerUserDetails())
            .buildAndExpand(Map.of())
            .toUri();
    ResponseEntity<String> result =
        request(xMasheryHandshake).method(HttpMethod.GET).uri(uri).send();
    LOGGER.info("Prtner user details URL response body = {}", result.getBody());
    LOGGER.info(
        "Prtner user details URL response = {}",
        result.getStatusCode().value() != HttpStatus.OK.value()
            ? result.getBody()
            : "call completed.");
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

  @Override
  public boolean isPLSActive(String xMasheryHandshake, String partnerId) throws Exception {
    URI uri =
        UriComponentsBuilder.fromUriString(config.getPlsURL().replace("{puid}", partnerId))
            .buildAndExpand(Map.of())
            .toUri();
    ResponseEntity<String> result =
        request(xMasheryHandshake).method(HttpMethod.GET).uri(uri).send();
    LOGGER.info(
        "PLS url response = {}",
        result.getStatusCode().value() == HttpStatus.OK.value()
            ? result.getBody()
            : "pls response successful.");
    PLSResponse plsResponse = null;
    try {
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      plsResponse = mapper.readValue(result.getBody(), PLSResponse.class);
      LOGGER.info(
          "PLS status: {} ,gracePeriod: {}", plsResponse.isStatus(), plsResponse.isGracePeriod());
      if (plsResponse.isStatus() || plsResponse.isGracePeriod()) {
        return true;
      } else {
        return false;
      }
    } catch (IOException | HttpClientErrorException e) {
      throw new BadRequestException("Error while invoking the PLS API" + e);
    }
  }

  @Override
  public boolean getHcaasStatusForPartner(String xMasheryHandshake) {
    UserDetailsWithCompanyList userDetails = fetchUserDetailsWithCompanyList(xMasheryHandshake);
    LOGGER.info("user details response - {} - {}", userDetails, xMasheryHandshake);
    return userDetails.getCompanyList().stream()
        .filter(company -> company.isHcaas())
        .findFirst()
        .isPresent();
  }

  private RequestBuilder<String> request(String xMasheryHandshake) {
    return restClient
        .request(String.class)
        .accept(MediaType.APPLICATION_JSON)
        .header(X_MASHERY_HANSHAKE, xMasheryHandshake);
  }
}
