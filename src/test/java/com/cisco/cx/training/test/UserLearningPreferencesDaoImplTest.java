package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.dao.impl.UserLearningPreferencesDAOImpl;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.UserLearningPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@ExtendWith(SpringExtension.class)
public class UserLearningPreferencesDaoImplTest {

  private static final String MASHERY_TEST = "test-mashery";

  @Mock DynamoDbClient dbClient;

  @Mock private PropertyConfiguration propertyConfig;

  @Mock ProductDocumentationDAO productDocumentationDAO;

  @Mock private HttpServletRequest request;

  @Mock private PartnerProfileService partnerProfileService;

  @InjectMocks
  private UserLearningPreferencesDAOImpl ulpDAOImpl =
      new UserLearningPreferencesDAOImpl(
          propertyConfig, productDocumentationDAO, request, partnerProfileService);

  @BeforeEach
  public void initCommon() {
    ServletContext context = Mockito.mock(ServletContext.class);
    when(request.getServletContext()).thenReturn(context);
    when(context.getAttribute(Constants.MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn(MASHERY_TEST);
  }

  @Test
  void testInit() {
    when(propertyConfig.getUlPreferencesTableName()).thenReturn("abc");
    when(propertyConfig.getAwsRegion()).thenReturn("abc");
    ulpDAOImpl.init();

    assertNotNull(ulpDAOImpl.getDbClient());
  }

  @Test
  void testFetchULPs() {
    Map<String, AttributeValue> ulp = new HashMap<String, AttributeValue>();
    Set<String> role = new HashSet<String>();
    role.add("Customer Success manager");
    AttributeValue attrValue = AttributeValue.builder().ss(role).build();
    ulp.put("role", attrValue);
    List<Map<String, AttributeValue>> attributeValues =
        new ArrayList<Map<String, AttributeValue>>();
    attributeValues.add(ulp);
    Set<String> ti = new HashSet<String>();
    ti.add("{\"startTime\":\"09:00\"}");
    AttributeValue attrValueTI = AttributeValue.builder().ss(ti).build();
    ulp.put("timeinterval", attrValueTI);
    QueryResponse response = QueryResponse.builder().items(attributeValues).build();
    Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(response);
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    List<String> dbList = new ArrayList<String>();
    when(productDocumentationDAO.getAllTechnologyForPreferences("true")).thenReturn(dbList);
    when(productDocumentationDAO.getAllRegionForPreferences("true")).thenReturn(dbList);
    when(productDocumentationDAO.getAllLanguagesForPreferences("true")).thenReturn(dbList);
    dbList.add("Customer Success manager");
    when(productDocumentationDAO.getAllRolesForPreferences("true")).thenReturn(dbList);
    Map<String, List<UserLearningPreference>> ulps =
        ulpDAOImpl.fetchUserLearningPreferences("user123");
    // System.out.println("ulps:"+ulps.get("technology").get(0).isSelected()+ ulps);
    assertEquals(5, ulps.size());
    assertTrue(ulps.get("role").get(0).isSelected());
    assertFalse(ulps.get("technology").get(0).isSelected());
  }

  @Test
  void testCreateOrUpdate() {
    Map<String, List<UserLearningPreference>> ulps =
        new HashMap<String, List<UserLearningPreference>>();
    List<UserLearningPreference> roleList = new ArrayList<UserLearningPreference>();
    UserLearningPreference roleUP = new UserLearningPreference();
    roleUP.setName("Customer Success manager");
    roleList.add(roleUP);
    ulps.put("role", roleList);
    List<UserLearningPreference> tiList = new ArrayList<UserLearningPreference>();
    UserLearningPreference tiUP = new UserLearningPreference();
    tiUP.setTimeMap(new HashMap<String, String>());
    tiList.add(tiUP);
    ulps.put("timeinterval", tiList);

    Map<String, AttributeValue> ulp = new HashMap<String, AttributeValue>();
    Set<String> role = new HashSet<String>();
    role.add("Customer Success manager");
    AttributeValue attrValue = AttributeValue.builder().ss(role).build();
    ulp.put("role", attrValue);
    List<Map<String, AttributeValue>> attributeValues =
        new ArrayList<Map<String, AttributeValue>>();
    attributeValues.add(ulp);
    Set<String> ti = new HashSet<String>();
    ti.add("{\"startTime\":\"09:00\"}");
    AttributeValue attrValueTI = AttributeValue.builder().ss(ti).build();
    ulp.put("timeinterval", attrValueTI);
    QueryResponse queryResponse = QueryResponse.builder().items(attributeValues).build();
    Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(queryResponse);
    SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(200).build();
    PutItemResponse response = Mockito.mock(PutItemResponse.class);
    Mockito.when(response.sdkHttpResponse()).thenReturn(httpResponse);
    Mockito.when(dbClient.putItem(Mockito.any(PutItemRequest.class))).thenReturn(response);

    Map<String, List<UserLearningPreference>> ulpsDB =
        ulpDAOImpl.createOrUpdateULP("user123", ulps);
    ulps.clear();
    ulpsDB = ulpDAOImpl.createOrUpdateULP("user123", ulps);
    Assertions.assertEquals(0, ulpsDB.size());
  }

  @Test
  void testGetULPPreferencesDDB() {
    Map<String, AttributeValue> ulp = new HashMap<String, AttributeValue>();
    Set<String> role = new HashSet<String>();
    role.add("Customer Success manager");
    AttributeValue attrValue = AttributeValue.builder().ss(role).build();
    ulp.put("role", attrValue);
    List<Map<String, AttributeValue>> attributeValues =
        new ArrayList<Map<String, AttributeValue>>();
    attributeValues.add(ulp);
    Set<String> ti = new HashSet<String>();
    ti.add("{\"startTime\":\"09:00\"}");
    AttributeValue attrValueTI = AttributeValue.builder().ss(ti).build();
    ulp.put("timeinterval", attrValueTI);
    QueryResponse response = QueryResponse.builder().items(attributeValues).build();
    Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(response);

    Map<String, Object> ulps = ulpDAOImpl.getULPPreferencesDDB("user123");
    Assertions.assertEquals(2, ulps.size());
  }
}
