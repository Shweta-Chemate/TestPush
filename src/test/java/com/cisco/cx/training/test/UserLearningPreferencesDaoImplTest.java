package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.dao.impl.LearningBookmarkDAOImpl;
import com.cisco.cx.training.app.dao.impl.UserLearningPreferencesDAOImpl;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.UserLearningPreference;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@ExtendWith(SpringExtension.class)
public class UserLearningPreferencesDaoImplTest {
	
	@Mock
	DynamoDbClient dbClient;
	
	@Mock
	private PropertyConfiguration propertyConfig;
	
	@Mock
	ProductDocumentationDAO productDocumentationDAO;
	
	@InjectMocks
	private UserLearningPreferencesDAOImpl ulpDAOImpl = new UserLearningPreferencesDAOImpl();

	@Test
	public void testInit() {
		when(propertyConfig.getUlPreferencesTableName()).thenReturn("abc");
		when(propertyConfig.getAwsRegion()).thenReturn("abc");
		ulpDAOImpl.init();
		
		assertNotNull(ulpDAOImpl.getDbClient());
	}
	
	@Test
	public void testFetchULPs(){
		Map<String,AttributeValue> ulp = new HashMap<String, AttributeValue>();
		Set<String> role = new HashSet<String>();
		role.add("Customer Success manager");
		AttributeValue attrValue = AttributeValue.builder().ss(role).build();
		ulp.put("role", attrValue);
		List<Map<String,AttributeValue>> attributeValues = new ArrayList<Map<String,AttributeValue>>();
		attributeValues.add(ulp);		
		Set<String> ti = new HashSet<String>();
		ti.add("{\"startTime\":\"09:00\"}");
		AttributeValue attrValueTI = AttributeValue.builder().ss(ti).build();
		ulp.put("timeinterval", attrValueTI);
		QueryResponse response = QueryResponse.builder().items(attributeValues).build();
		Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(response);
		
		List<String> dbList = new ArrayList<String>();
		when(productDocumentationDAO.getAllTechnologyForPreferences()).thenReturn(dbList);
		when(productDocumentationDAO.getAllRegionForPreferences()).thenReturn(dbList);
		when(productDocumentationDAO.getAllLanguagesForPreferences()).thenReturn(dbList);
		 dbList.add("Customer Success manager");when(productDocumentationDAO.getAllRolesForPreferences()).thenReturn(dbList);
		Map<String, List<UserLearningPreference>> ulps = ulpDAOImpl.fetchUserLearningPreferences("user123");
		//System.out.println("ulps:"+ulps.get("technology").get(0).isSelected()+ ulps);
		assertEquals(5, ulps.size());
		assertTrue(ulps.get("role").get(0).isSelected());
		assertFalse(ulps.get("technology").get(0).isSelected());
	}
	
		
	@Test
	public void testCreateOrUpdate(){
		Map<String, List<UserLearningPreference>> ulps = new HashMap<String, List<UserLearningPreference>>();
		List<UserLearningPreference> roleList = new ArrayList<UserLearningPreference>();
		UserLearningPreference roleUP = new UserLearningPreference ();
		roleUP.setName("Customer Success manager");roleList.add(roleUP);
		ulps.put("role", roleList);
		List<UserLearningPreference> tiList = new ArrayList<UserLearningPreference>();
		UserLearningPreference tiUP = new UserLearningPreference ();
		tiUP.setTimeMap(new HashMap<String,String>());tiList.add(tiUP);
		ulps.put("timeinterval", tiList);
		
		Map<String,AttributeValue> ulp = new HashMap<String, AttributeValue>();
		Set<String> role = new HashSet<String>();
		role.add("Customer Success manager");
		AttributeValue attrValue = AttributeValue.builder().ss(role).build();
		ulp.put("role", attrValue);
		List<Map<String,AttributeValue>> attributeValues = new ArrayList<Map<String,AttributeValue>>();
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
		
		Map<String, List<UserLearningPreference>> ulpsDB = ulpDAOImpl.createOrUpdateULP("user123",ulps);
		ulps.clear();
		ulpsDB = ulpDAOImpl.createOrUpdateULP("user123",ulps);
	}
	
	@Test
	public void testGetULPPreferencesDDB(){
		Map<String,AttributeValue> ulp = new HashMap<String, AttributeValue>();
		Set<String> role = new HashSet<String>();
		role.add("Customer Success manager");
		AttributeValue attrValue = AttributeValue.builder().ss(role).build();
		ulp.put("role", attrValue);
		List<Map<String,AttributeValue>> attributeValues = new ArrayList<Map<String,AttributeValue>>();
		attributeValues.add(ulp);		
		Set<String> ti = new HashSet<String>();
		ti.add("{\"startTime\":\"09:00\"}");
		AttributeValue attrValueTI = AttributeValue.builder().ss(ti).build();
		ulp.put("timeinterval", attrValueTI);
		QueryResponse response = QueryResponse.builder().items(attributeValues).build();
		Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(response);
		
		Map<String, Object> ulps = ulpDAOImpl.getULPPreferencesDDB("user123");
	
	}
}
