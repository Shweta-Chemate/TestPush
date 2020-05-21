package com.cisco.cx.training.app.dao.impl;

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

@Repository
public class LearningBookmarkDAOImpl implements LearningBookmarkDAO {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private PropertyConfiguration propertyConfig;
	
	private static final String USERID_SUFFIX = "-academybookmark";
	
	private static final String BOOKMARK_KEY = "bookmarks";
	
	private DynamoDbClient dbClient;
	
	public DynamoDbClient getDbClient() {
		return dbClient;
	}

	public void setDbClient(DynamoDbClient dbClient) {
		this.dbClient = dbClient;
	}

	@PostConstruct
	public void init() {
		LOG.info("Initializing LearningBookmarkDAOImpl for table :: " + propertyConfig.getBookmarkTableName());
		LOG.info("Initializing LearningBookmarkDAOImpl with access key :: " + propertyConfig.getAwsAccessKey());
		Region region = Region.of(propertyConfig.getAwsRegion());
		DynamoDbClientBuilder dDbClientBuilder = DynamoDbClient.builder();
		dDbClientBuilder.region(region);
		AwsCredentials credentials = new AwsCredentials() {
			
			@Override
			public String secretAccessKey() {
				return propertyConfig.getAwsAccessSecret();
			}
			
			@Override
			public String accessKeyId() {
				return propertyConfig.getAwsAccessKey();
			}
		};
		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
		dDbClientBuilder.credentialsProvider(credentialsProvider);
		dbClient = dDbClientBuilder.build();
	}	

	@Override
	public BookmarkResponseSchema createOrUpdate(
			BookmarkResponseSchema bookmarkResponseSchema) {
		Map<String, AttributeValue> itemValue = new HashMap<String, AttributeValue>();
		Set<String> currentBookMarks = getBookmarks(bookmarkResponseSchema.getEmail());
		if(bookmarkResponseSchema.isBookmark()){
			if(null == currentBookMarks){
				currentBookMarks = new HashSet<String>();								
			}
			currentBookMarks.add(bookmarkResponseSchema.getLearningid());			
		}else{
			currentBookMarks.remove(bookmarkResponseSchema.getLearningid());
			if(currentBookMarks.isEmpty()){
				currentBookMarks.add("");
			}
		}		
	    itemValue.put("userid", AttributeValue.builder().s(bookmarkResponseSchema.getEmail().concat(USERID_SUFFIX)).build());
	    itemValue.put("bookmarks", AttributeValue.builder().ss(currentBookMarks).build());
	    Builder putItemReq = PutItemRequest.builder();
	    putItemReq.tableName(propertyConfig.getBookmarkTableName()).item(itemValue);
	    PutItemResponse response = dbClient.putItem(putItemReq.build());
	    if(response.sdkHttpResponse().isSuccessful()){
	    	BookmarkResponseSchema responseSchema = new BookmarkResponseSchema();
	    	responseSchema.setId(bookmarkResponseSchema.getId());
	    	return responseSchema;
	    }else{
	    	return null;
	    }
	}

	@Override
	public Set<String> getBookmarks(String email){
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
	    QueryResponse queryResult = dbClient.query(queryRequest);
	    List<Map<String,AttributeValue>> attributeValues = queryResult.items();	    
	    if(attributeValues.size()>0) {
	    	Map<String,AttributeValue> userBookmarks = attributeValues.get(0);
	    	AttributeValue bookMarkSet = userBookmarks.get(BOOKMARK_KEY);
	    	userBookMarks = new HashSet<String>(bookMarkSet.ss());	
	    	
	    }	    
	    return userBookMarks;
	}

}
