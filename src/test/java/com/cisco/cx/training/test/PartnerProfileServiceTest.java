package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.impl.PartnerProfileServiceImpl;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.PLSResponse;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
public class PartnerProfileServiceTest {
  @Mock private PropertyConfiguration config;

  @Autowired ResourceLoader resourceLoader;

  @Mock RestTemplate restTemplate;

  @InjectMocks
  private PartnerProfileService partnerProfileService =
      new PartnerProfileServiceImpl(restTemplate, config);

  private static final String X_MASHERY_HANSHAKE = "X-Mashery-Handshake";

  @Test
  void fetchUserDetails() throws IOException {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    // headers.set("Authorization", "Basic " + "");
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);

    ResponseEntity<String> result = new ResponseEntity<>(getUserDetails(), HttpStatus.OK);
    when(restTemplate.exchange(
            "/sntccbr5@hotmail.com", HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    assertNotNull(partnerProfileService.fetchUserDetails(xMasheryHandshake));
    assertNotNull(partnerProfileService.getEntitlementUrl());
  }

  @Test
  void fetchUserDetailsWithCompanyList() throws IOException {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
    ResponseEntity<String> result =
        new ResponseEntity<>(getUserDetailsWithCompanyList(), HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPartnerUserDetails(), HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    assertNotNull(partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake));
    assertNotNull(partnerProfileService.getEntitlementUrl());
  }

  @Test
  void fetchUserDetailsWithCompanyListMappingError() throws IOException {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
    ResponseEntity<String> result = new ResponseEntity<>("test", HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPartnerUserDetails(), HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    assertNull(partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake));
  }

  @Test
  void fetchUserDetailsJsonMappingError() throws IOException {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    // headers.set("Authorization", "Basic " + "");
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);

    ResponseEntity<String> result = new ResponseEntity<>("", HttpStatus.OK);
    when(restTemplate.exchange(
            "/sntccbr5@hotmail.com", HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    UserDetails response = partnerProfileService.fetchUserDetails(xMasheryHandshake);
    assertNull(response);
  }

  @Test
  void fetchUserDetailsJsonParseError() throws IOException {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    // headers.set("Authorization", "Basic " + "");
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);

    ResponseEntity<String> result = new ResponseEntity<>("some @ data", HttpStatus.OK);
    when(restTemplate.exchange(
            "/sntccbr5@hotmail.com", HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    UserDetails response = partnerProfileService.fetchUserDetails(xMasheryHandshake);
    assertNull(response);
  }

  @Test
  void testisPLSActive() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
    when(config.getPlsURL()).thenReturn("http:/test.com/{puid}");
    ResponseEntity<String> result = new ResponseEntity<>(getplsresponse(), HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPlsURL().replace("{puid}", "101"),
            HttpMethod.GET,
            requestEntity,
            String.class))
        .thenReturn(result);
    Assertions.assertTrue(partnerProfileService.isPLSActive(xMasheryHandshake, "101"));
    Assert.isTrue(partnerProfileService.isPLSActive(xMasheryHandshake, "101"));

    ResponseEntity<String> result1 =
        new ResponseEntity<>(getplsresponseforinactive(), HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPlsURL().replace("{puid}", "101"),
            HttpMethod.GET,
            requestEntity,
            String.class))
        .thenReturn(result1);
    Assertions.assertFalse(partnerProfileService.isPLSActive(xMasheryHandshake, "101"));
  }

  @Test
  void testisPLSActiveError() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
    when(config.getPlsURL()).thenReturn("http:/test.com/{puid}");
    ResponseEntity<String> result = new ResponseEntity<>("test", HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPlsURL().replace("{puid}", "101"),
            HttpMethod.GET,
            requestEntity,
            String.class))
        .thenReturn(result);
    Assertions.assertThrows(
        BadRequestException.class,
        () -> partnerProfileService.isPLSActive(xMasheryHandshake, "101"));
  }

  @Test
  void testGetHcaasStatusForPartner() throws Exception {
    partnerProfileService.setEntitlementUrl("");
    when(config.createCxpBasicAuthToken()).thenReturn("");
    HttpHeaders headers = new HttpHeaders();
    String xMasheryHandshake =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    headers.set(X_MASHERY_HANSHAKE, xMasheryHandshake);
    HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
    ResponseEntity<String> result =
        new ResponseEntity<>(getUserDetailsWithCompanyList(), HttpStatus.OK);
    when(restTemplate.exchange(
            config.getPartnerUserDetails(), HttpMethod.GET, requestEntity, String.class))
        .thenReturn(result);
    boolean hcaasStatus = partnerProfileService.getHcaasStatusForPartner(xMasheryHandshake);
    Assertions.assertTrue(hcaasStatus);
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

  private String getUserDetailsWithCompanyList() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    UserDetailsWithCompanyList userDetails = new UserDetailsWithCompanyList();
    UserProfile ciscoUserProfileSchema = new UserProfile();
    ciscoUserProfileSchema.setEmailId("test");
    Company company = new Company();
    company.setDemoAccount(false);
    company.setHcaas(true);
    company.setPuid("123");
    userDetails.setCiscoUserProfileSchema(ciscoUserProfileSchema);
    userDetails.setCompanyList(Arrays.asList(company));

    return mapper.writeValueAsString(userDetails);
  }

  private String loadFromFile(String filePath) throws IOException {
    return new String(
        Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
  }

  private String getplsresponse() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    PLSResponse plsResponse = new PLSResponse();
    plsResponse.setStatus(true);
    return mapper.writeValueAsString(plsResponse);
  }

  private String getplsresponseforinactive() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    PLSResponse plsResponse = new PLSResponse();
    plsResponse.setStatus(false);
    plsResponse.setGracePeriod(false);
    return mapper.writeValueAsString(plsResponse);
  }
}
