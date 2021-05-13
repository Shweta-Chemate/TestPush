package com.cisco.cx.training.app.dao.impl;

import java.time.Duration;
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

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
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
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class LearningBookmarkDAOImpl implements LearningBookmarkDAO {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private PropertyConfiguration propertyConfig;
	
	private static final String USERID_SUFFIX = "-academybookmark";
	
	private static final String BOOKMARK_KEY = "bookmarks";
	
	private DynamoDbClient dbClient;
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public DynamoDbClient getDbClient() {
		return dbClient;
	}

	public void setDbClient(DynamoDbClient dbClient) {
		this.dbClient = dbClient;
	}

	@PostConstruct
	public void init() {
		LOG.info("Initializing LearningBookmarkDAOImpl for table :: {}", propertyConfig.getBookmarkTableName());
		SdkHttpClient httpClient = ApacheHttpClient.builder().
                connectionTimeout(Duration.ofSeconds(100))
                .socketTimeout(Duration.ofSeconds(100))
                .build();
		
		Region region = Region.of(propertyConfig.getAwsRegion());
		DynamoDbClientBuilder dDbClientBuilder = DynamoDbClient.builder().httpClient(httpClient);
		dDbClientBuilder.region(region);
		dbClient = dDbClientBuilder.build();
	}	

	@Override
	public BookmarkResponseSchema createOrUpdate(
			BookmarkResponseSchema bookmarkResponseSchema) {
		LOG.info("Entering the createOrUpdate");
		String CARD_KEY="cardId"; String TIME_KEY ="time";
		long requestStartTime = System.currentTimeMillis();	
		Map<String, AttributeValue> itemValue = new HashMap<String, AttributeValue>();
		Set<String> currentBookMarks = getBookmarks(bookmarkResponseSchema.getCcoid());
		Map<String,Object> newBookmark = new HashMap<String,Object>();
		newBookmark.put(CARD_KEY, bookmarkResponseSchema.getLearningid());
		newBookmark.put(TIME_KEY, bookmarkResponseSchema.getUpdated());
		if(bookmarkResponseSchema.isBookmark()){
			if(null == currentBookMarks){
				currentBookMarks = new HashSet<String>();								
			}
			try {
				currentBookMarks.add(mapper.writeValueAsString(newBookmark));
			} catch (JsonProcessingException e) {
				LOG.error("Error during mark {} {}",bookmarkResponseSchema.getLearningid(),e);
			}			
		}else{
			Set<String> toRemove = new HashSet<String>();
			currentBookMarks.forEach(str -> {
				try {
					Map<String,Object> previousBookmark= mapper.readValue(str, Map.class);
					if(previousBookmark!=null && previousBookmark.containsKey(CARD_KEY) 
							&& bookmarkResponseSchema.getLearningid().equals(previousBookmark.get(CARD_KEY)))
					{
						toRemove.add(str); // break;?
					}							
				} catch (JsonProcessingException e) {
					LOG.error("Error during unmark {} {}",str,e);
				}
			});
			currentBookMarks.removeAll(toRemove);
			if(currentBookMarks.isEmpty()){
				currentBookMarks.add("");
			}
		}		
	    itemValue.put("userid", AttributeValue.builder().s(bookmarkResponseSchema.getCcoid().concat(USERID_SUFFIX)).build());
	    itemValue.put("bookmarks", AttributeValue.builder().ss(currentBookMarks).build());
	    Builder putItemReq = PutItemRequest.builder();
	    LOG.info("Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
	    requestStartTime = System.currentTimeMillis();	
	    putItemReq.tableName(propertyConfig.getBookmarkTableName()).item(itemValue);
	    PutItemResponse response = dbClient.putItem(putItemReq.build());
	    LOG.info("response received in {} ", (System.currentTimeMillis() - requestStartTime));
	    if(response.sdkHttpResponse().isSuccessful()){
	    	BookmarkResponseSchema responseSchema = new BookmarkResponseSchema();
	    	responseSchema.setId(bookmarkResponseSchema.getId());
	    	return responseSchema;
	    }else{
	    	return null;
	    }
	}

	/**
	 * [{\"time\":1620903592718,\"card\":\"ACIDistNet1\"}, {\"time\":1620904805105,\"card\":\"ACIDistNet2\"}]"
	 */
	@Override
	public Set<String> getBookmarks(String email){
		LOG.info("Entering the fetch bookmarks");
		long requestStartTime = System.currentTimeMillis();	
		Set<String> userBookMarks = null;
		Map<String,String> expressionAttributesNames = new HashMap<>();
	    expressionAttributesNames.put("#userid","userid");
	    
	    Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
	    
	    expressionAttributeValues.put(":useridValue",AttributeValue.builder().s(email.concat(USERID_SUFFIX)).build());

	    QueryRequest queryRequest = QueryRequest.builder()
	        .tableName(propertyConfig.getBookmarkTableName())
	        .keyConditionExpression("#userid = :useridValue")
	        .expressionAttributeNames(expressionAttributesNames)
	        .expressionAttributeValues(expressionAttributeValues).build();
	    LOG.info("Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
	    requestStartTime = System.currentTimeMillis();	
	    QueryResponse queryResult = dbClient.query(queryRequest);
	    LOG.info("response received in {} ", (System.currentTimeMillis() - requestStartTime));
	    requestStartTime = System.currentTimeMillis();	
	    List<Map<String,AttributeValue>> attributeValues = queryResult.items();	    
	    if(attributeValues.size()>0) {
	    	Map<String,AttributeValue> userBookmarks = attributeValues.get(0);
	    	AttributeValue bookMarkSet = userBookmarks.get(BOOKMARK_KEY);
	    	userBookMarks = new HashSet<String>(bookMarkSet.ss());	
	    	
	    }	    
	    LOG.info("final response in {} ", (System.currentTimeMillis() - requestStartTime));
	    return userBookMarks;
	}

}
