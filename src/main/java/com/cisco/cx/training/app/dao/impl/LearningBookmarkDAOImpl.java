package com.cisco.cx.training.app.dao.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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
import com.cisco.cx.training.app.entities.BookmarkCountsEntity;
import com.cisco.cx.training.app.entities.BookmarkCountsEntityPK;
import com.cisco.cx.training.app.repo.BookmarkCountsRepo;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class LearningBookmarkDAOImpl implements LearningBookmarkDAO {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private PropertyConfiguration propertyConfig;

	@Autowired
	private BookmarkCountsRepo bookmarkCountsRepo;

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
	
	private long getTime()
	{
		final Instant now = Clock.systemUTC().instant();
		long time = now.toEpochMilli();
		return time;
	}

	@Override
	public BookmarkResponseSchema createOrUpdate(
			BookmarkResponseSchema bookmarkResponseSchema, String puid) {
		LOG.info("Entering the createOrUpdate");
		long requestStartTime = System.currentTimeMillis();	
		Map<String, AttributeValue> itemValue = new HashMap<String, AttributeValue>();
		Set<String> currentBookMarks = new HashSet<String>();
		Map<String,Object> currentBookMarksMap = getBookmarksWithTime(bookmarkResponseSchema.getCcoid());
		if(null == currentBookMarksMap)
			currentBookMarksMap = new HashMap<String,Object>();
		if(bookmarkResponseSchema.isBookmark())
			currentBookMarksMap.put(bookmarkResponseSchema.getLearningid(), getTime());
		else					
			currentBookMarksMap.remove(bookmarkResponseSchema.getLearningid());	
		if(currentBookMarksMap.isEmpty()) currentBookMarks.add("");
		else
		{	
			currentBookMarksMap.forEach((k,v)->{
				Map<String,Object> oneBK = new HashMap<String,Object>();oneBK.put(k, v);
				try
				{
					currentBookMarks.add(mapper.writeValueAsString(oneBK));					
				} 
				catch (JsonProcessingException e)
				{
					LOG.error("Error during mark {} {}",bookmarkResponseSchema.getLearningid(),e);
				}
			});
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
			//update bookmark count in aurora
			BookmarkCountsEntity bookMarkCountsEntity = bookmarkCountsRepo.findByLearningItemIdAndPuid(bookmarkResponseSchema.getLearningid(), puid);
			if(bookMarkCountsEntity != null) {
				int count = bookmarkResponseSchema.isBookmark()?bookMarkCountsEntity.getCount()+1:bookMarkCountsEntity.getCount()-1;
				bookMarkCountsEntity.setCount(count);
			}
			else {
				if(bookmarkResponseSchema.isBookmark()) {
					bookMarkCountsEntity = new BookmarkCountsEntity();
					bookMarkCountsEntity.setLearningItemId(bookmarkResponseSchema.getLearningid());
					bookMarkCountsEntity.setPuid(puid);
					bookMarkCountsEntity.setCount(1);
				}
			}
			if(bookMarkCountsEntity!=null && bookMarkCountsEntity.getCount()>=0)
				bookmarkCountsRepo.save(bookMarkCountsEntity);

			BookmarkResponseSchema responseSchema = new BookmarkResponseSchema();
			responseSchema.setId(bookmarkResponseSchema.getId());
			return responseSchema;
		}else{
			return null;
		}
	}

	/*
	 * ["ACIDistNet1",...]
	 */
	@Override
	public Set<String> getBookmarks(String email){		
		Map<String, Object> userBookMarksMap = getBookmarksWithTime(email);	
		return userBookMarksMap.keySet();
	}

	/*
	 * {"ACIDistNet1":1620903592718, ...}	 */
	@Override
	public Map<String,Object> getBookmarksWithTime(String email){
		LOG.info("Entering the fetch bookmarks");
		Map<String, Object> userBookMarksMap = new HashMap<String,Object>();
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
	    	userBookMarks.forEach(str -> {
				try {
					Map<String,Object> ddbBookmark= mapper.readValue(str, Map.class);
					userBookMarksMap.putAll(ddbBookmark);						
				} catch (JsonProcessingException e) {
					LOG.error("Error during bkmap {} {}",str,e);
				}
			});	    	
	    }	    
	    LOG.info("final response in {}", (System.currentTimeMillis() - requestStartTime));
	    return userBookMarksMap;
	}

}
