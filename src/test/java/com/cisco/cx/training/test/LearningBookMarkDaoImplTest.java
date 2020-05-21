package com.cisco.cx.training.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import com.cisco.cx.training.app.dao.impl.LearningBookmarkDAOImpl;
import com.cisco.cx.training.models.BookmarkResponseSchema;

@RunWith(SpringRunner.class)
public class LearningBookMarkDaoImplTest {
	
	@Mock
	DynamoDbClient dbClient;
	
	@InjectMocks
	private LearningBookmarkDAOImpl learningBookMarkImpl = new LearningBookmarkDAOImpl();

	
	@Test
	public void testGetBookmarks(){
		Map<String,AttributeValue> userBookmarks = new HashMap<String, AttributeValue>();
		Set<String> bookMar = new HashSet<String>();
		bookMar.add("1");
		AttributeValue attrValue = AttributeValue.builder().ss(bookMar).build();
		userBookmarks.put("bookmarks", attrValue);
		List<Map<String,AttributeValue>> attributeValues = new ArrayList<Map<String,AttributeValue>>();
		attributeValues.add(userBookmarks);
		QueryResponse response = QueryResponse.builder().items(attributeValues).build();
		Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(response);
		
		Set<String> bookMarks = learningBookMarkImpl.getBookmarks("");
		Assert.assertEquals(bookMarks.size(), 1);
	}
	
	@Test
	public void testCreateOrUpdate(){
		BookmarkResponseSchema responseSchema = new BookmarkResponseSchema();
		responseSchema.setEmail("email");
		Map<String,AttributeValue> userBookmarks = new HashMap<String, AttributeValue>();
		Set<String> bookMar = new HashSet<String>();
		bookMar.add("1");
		AttributeValue attrValue = AttributeValue.builder().ss(bookMar).build();
		userBookmarks.put("bookmarks", attrValue);
		List<Map<String,AttributeValue>> attributeValues = new ArrayList<Map<String,AttributeValue>>();
		attributeValues.add(userBookmarks);
		QueryResponse queryResponse = QueryResponse.builder().items(attributeValues).build();
		Mockito.when(dbClient.query(Mockito.any(QueryRequest.class))).thenReturn(queryResponse);
		SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(200).build();
		PutItemResponse response = Mockito.mock(PutItemResponse.class);
		Mockito.when(response.sdkHttpResponse()).thenReturn(httpResponse);
		Mockito.when(dbClient.putItem(Mockito.any(PutItemRequest.class))).thenReturn(response);
		
		BookmarkResponseSchema bookmarkResponseSchema = learningBookMarkImpl.createOrUpdate(responseSchema);
	}
}