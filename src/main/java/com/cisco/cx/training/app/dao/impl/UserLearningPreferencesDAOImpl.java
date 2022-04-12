package com.cisco.cx.training.app.dao.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest.Builder;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.dao.UserLearningPreferencesDAO;
import com.cisco.cx.training.models.UserLearningPreference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"squid:S134","squid:S1200"})
@Repository
public class UserLearningPreferencesDAOImpl implements UserLearningPreferencesDAO {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	private static final int CONN_TIMEOUT = 20;
	private static final int SOCKET_TIMEOUT = 20;
	
	@Autowired
	private PropertyConfiguration propertyConfig;	
	@Autowired
	ProductDocumentationDAO productDocumentationDAO;
	
	private static final String USERID_SUFFIX = "";//_ulp
	private static final String USERID_KEY="userid";	
	private static enum PREFERENCES_KEYS {role,technology,language,region,timeinterval};  //NOSONAR 
	private static final String PREFERENCE_SUFFIX = "";//"_ulp"
	private DynamoDbClient dbClient;	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public DynamoDbClient getDbClient() {
		return dbClient;
	}

	public void setDbClient(DynamoDbClient dbClient) {
		this.dbClient = dbClient;
	}

	@PostConstruct
	public void init() //throws URISyntaxException
	{
		LOG.info("Initializing ULP for table :: {}", propertyConfig.getUlPreferencesTableName());
		SdkHttpClient httpClient = ApacheHttpClient.builder().
                connectionTimeout(Duration.ofSeconds(CONN_TIMEOUT))
                .socketTimeout(Duration.ofSeconds(SOCKET_TIMEOUT))
                .build();
		
		Region region = Region.of(propertyConfig.getAwsRegion());
		DynamoDbClientBuilder dDbClientBuilder = DynamoDbClient.builder().httpClient(httpClient);
				//.endpointOverride(new URI("http://localhost:8000")); //NOSONAR
		dDbClientBuilder.region(region);
		dbClient = dDbClientBuilder.build();
	}

	@Override
	public Map<String, List<UserLearningPreference>> createOrUpdateULP(String userId,
			Map<String, List<UserLearningPreference>> ulPreferences) {		
		LOG.info("Entering the createOrUpdateULP");
		long requestStartTime = System.currentTimeMillis();		
		Map<String, List<UserLearningPreference>> currentULPs = ulPreferences;//getULPs not required
		
		Map<String, AttributeValue> itemValue = new HashMap<String, AttributeValue>();
		itemValue.put(USERID_KEY, AttributeValue.builder().s(userId.concat(USERID_SUFFIX)).build());
		if(currentULPs==null || currentULPs.isEmpty())
		{
			LOG.info("User cleared prefs.");
		}
		else {
		Arrays.asList(PREFERENCES_KEYS.values()).forEach( preferenceKey ->{
			if(ulPreferences.containsKey(preferenceKey.name()) )
			{
				Set<String> preferenceNames = new HashSet<>();
				List<UserLearningPreference> ulpList = ulPreferences.get(preferenceKey.name());
				//LOG.info("preferenceKey {} ulpList {}",preferenceKey, ulpList);  //NOSONAR
				if(ulpList!=null && !ulpList.isEmpty())
				{
					ulpList.forEach(up ->{
						if(preferenceKey.equals(PREFERENCES_KEYS.timeinterval))
						{
								try {
									String oneTI = mapper.writeValueAsString(up.getTimeMap());
									preferenceNames.add(oneTI);
								} catch (JsonProcessingException e) { //NOSONAR
									LOG.warn("Invalid TI in create {} {}", up, e.getMessage()); 
								}							
							
						}
						else if(up.isSelected()) { 
							preferenceNames.add(up.getName());
						}
					});
				}
				
				if(!preferenceNames.isEmpty()) {
					itemValue.put(preferenceKey.name().concat(PREFERENCE_SUFFIX), AttributeValue.builder().ss(preferenceNames).build());
				}
			}					
		});
	}
		LOG.info("Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();	
		Builder putItemReq = PutItemRequest.builder();		
		putItemReq.tableName(propertyConfig.getUlPreferencesTableName()).item(itemValue);
		PutItemResponse response = dbClient.putItem(putItemReq.build());LOG.info("response {}",response);
		LOG.info("response received in {} ", (System.currentTimeMillis() - requestStartTime));
		if(response.sdkHttpResponse().isSuccessful()){		
			return ulPreferences;
		}else{
			return null;
		}
	}
	
	
	public QueryResponse fetchULPPreferencesDDB(String userId) 
	{			
		LOG.info("Entering DDB ULPs");
		long requestStartTime = System.currentTimeMillis();		
		Map<String,String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#userid",USERID_KEY);	    
		Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();	    
		expressionAttributeValues.put(":useridValue",AttributeValue.builder().s(userId.concat(USERID_SUFFIX)).build());
		QueryRequest queryRequest = QueryRequest.builder()
				.tableName(propertyConfig.getUlPreferencesTableName())
				.keyConditionExpression("#userid = :useridValue")
				.expressionAttributeNames(expressionAttributesNames)
				.expressionAttributeValues(expressionAttributeValues).build();
		LOG.info("ULP Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();	
		QueryResponse queryResult = dbClient.query(queryRequest);
		LOG.info("ULP response received in {} ", (System.currentTimeMillis() - requestStartTime));	
		return queryResult;
	}
	
	@Override
	public HashMap<String, Object> getULPPreferencesDDB(String userId) 
	{			
		LOG.info("Entering get DDB ULPs");
		long requestStartTime = System.currentTimeMillis();	
		HashMap<String, Object> prefFilters = new HashMap<>();
		QueryResponse queryResult = fetchULPPreferencesDDB(userId);		
		List<Map<String,AttributeValue>> attributeValues = queryResult.items();		
		if(!attributeValues.isEmpty()) {
			Map<String,AttributeValue> userLearningPreferences = attributeValues.get(0);			
			Arrays.asList(PREFERENCES_KEYS.values()).forEach( preferenceKey ->{
				AttributeValue preferenceSet = userLearningPreferences.get(preferenceKey.name());				
				if(preferenceSet != null)
				{
					List<Object> ulps = new ArrayList<>(preferenceSet.ss());
					prefFilters.put(preferenceKey.name(), ulps);
				}
			});
		}		
		LOG.info("PD-DDB done in {} ", (System.currentTimeMillis() - requestStartTime));
		return prefFilters;
	}

	@Override
	public Map<String, List<UserLearningPreference>> fetchUserLearningPreferences(String userId) {
		LOG.info("Entering the fetch ULPs");
		Map<String, List<UserLearningPreference>> ulpMap = new HashMap<>();
		ulpMap.putAll(getAllLatestPreferencesCategories());
		QueryResponse queryResult = fetchULPPreferencesDDB(userId);		
		long requestStartTime = System.currentTimeMillis();	
		List<Map<String,AttributeValue>> attributeValues = queryResult.items();		
		//LOG.info("attributeValues {} , {}", attributeValues, attributeValues.size());  //NOSONAR
		if(attributeValues.size()>0) {
			Map<String,AttributeValue> userLearningPreferences = attributeValues.get(0);
			Arrays.asList(PREFERENCES_KEYS.values()).forEach( preferenceKey ->{
				AttributeValue preferenceSet = userLearningPreferences.get(preferenceKey.name());
				if(preferenceSet != null)
				{
					Set<String> ulps = new HashSet<>(preferenceSet.ss());
					//LOG.info(" preferenceKey {} ulps {}",preferenceKey, ulps);  //NOSONAR
					if(preferenceKey.equals(PREFERENCES_KEYS.timeinterval))
					{
						List<UserLearningPreference> listTI = new ArrayList<>();
						ulps.forEach(timeInterval -> {							
							try {
								UserLearningPreference timeULP = new UserLearningPreference();
								timeULP.setSelected(true);
								timeULP.setTimeMap(mapper.readValue(timeInterval, Map.class));
								listTI.add(timeULP);
							} catch (JsonProcessingException e) {  //NOSONAR
								LOG.warn("Invalid TimeInterval preference {} err={}",timeInterval, e.getMessage());							
							}						
						});	
						ulpMap.put(PREFERENCES_KEYS.timeinterval.name(), listTI);
					}
					else
					{
						List<UserLearningPreference> preDBList = ulpMap.get(preferenceKey.name());					
						if(preDBList!=null && !preDBList.isEmpty()) {
							preDBList.forEach(up -> {
								if(ulps.contains(up.getName())) {
									up.setSelected(true);
								}
							});
						}
					}					
				}			
			});			
		}
		LOG.info("final response in {}", (System.currentTimeMillis() - requestStartTime));
		return ulpMap;
	}

	private Map<? extends String, ? extends List<UserLearningPreference>> getAllLatestPreferencesCategories() {
		Map<String, List<UserLearningPreference>> dbMap = new HashMap<>();
		List<String> roles = productDocumentationDAO.getAllRolesForPreferences();
		dbMap.put(PREFERENCES_KEYS.role.name(),setULP(roles));
		List<String> technologies = productDocumentationDAO.getAllTechnologyForPreferences();
		dbMap.put(PREFERENCES_KEYS.technology.name(),setULP(technologies));
		List<String> regions = productDocumentationDAO.getAllRegionForPreferences();
		dbMap.put(PREFERENCES_KEYS.region.name(),setULP(regions));	
		List<String> languages = productDocumentationDAO.getAllLanguagesForPreferences();
		dbMap.put(PREFERENCES_KEYS.language.name(),setULP(languages));	
		return dbMap;
	}
	
	private List<UserLearningPreference> setULP(List<String> dbPrefList)
	{
		List<UserLearningPreference> dbPrefs = new ArrayList<>();
		dbPrefList.forEach(dbPref -> {
			UserLearningPreference ulp= new UserLearningPreference();
			ulp.setName(dbPref);ulp.setSelected(false);
			dbPrefs.add(ulp);
		});
		return dbPrefs;
	}

}
