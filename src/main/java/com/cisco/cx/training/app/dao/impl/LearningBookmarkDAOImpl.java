package com.cisco.cx.training.app.dao.impl;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.entities.BookmarkCountsEntity;
import com.cisco.cx.training.app.repo.BookmarkCountsRepo;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest.Builder;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@SuppressWarnings({"squid:S1200", "java:S3776", "java:S3749", "java:S138"})
@Repository
public class LearningBookmarkDAOImpl implements LearningBookmarkDAO {

  private static final Logger LOG = LoggerFactory.getLogger(LearningBookmarkDAOImpl.class);

  private static final int CONN_TIMEOUT = 20;
  private static final int SOCKET_TIMEOUT = 20;

  private PropertyConfiguration propertyConfig;

  private BookmarkCountsRepo bookmarkCountsRepo;

  @Autowired
  public LearningBookmarkDAOImpl(
      PropertyConfiguration propertyConfig, BookmarkCountsRepo bookmarkCountsRepo) {
    this.propertyConfig = propertyConfig;
    this.bookmarkCountsRepo = bookmarkCountsRepo;
  }

  private static final String USERID_SUFFIX = "-academybookmark";

  private static final String USERID_KEY = "userid";
  private static final String BOOKMARK_KEY = "bookmark";
  private static final String TIMESTAMP_KEY = "timestamp";

  private DynamoDbClient dbClient;

  public DynamoDbClient getDbClient() {
    return dbClient;
  }

  public void setDbClient(DynamoDbClient dbClient) {
    this.dbClient = dbClient;
  }

  @PostConstruct
  public void init() {
    LOG.info(
        "Initializing LearningBookmarkDAOImpl for table :: {}",
        propertyConfig.getBookmarkTableName());
    final SdkHttpClient httpClient =
        UrlConnectionHttpClient.builder()
            .connectionTimeout(Duration.ofSeconds(CONN_TIMEOUT))
            .socketTimeout(Duration.ofSeconds(SOCKET_TIMEOUT))
            .build();

    Region region = Region.of(propertyConfig.getAwsRegion());
    DynamoDbClientBuilder dDbClientBuilder =
        DynamoDbClient.builder()
            .credentialsProvider(PropertyConfiguration.credentialProvider)
            .httpClient(httpClient);
    dDbClientBuilder.region(region);
    dbClient = dDbClientBuilder.build();
  }

  private long getTime() {
    final Instant now = Clock.systemUTC().instant();
    return now.toEpochMilli();
  }

  @Override
  public BookmarkResponseSchema createOrUpdate(
      BookmarkResponseSchema bookmarkResponseSchema, String puid) {
    LOG.info("Entering the createOrUpdate");
    long requestStartTime = System.currentTimeMillis();
    Map<String, AttributeValue> itemValue = new HashMap<>();
    Map<String, Object> currentBookMarksMap =
        getBookmarksWithTime(bookmarkResponseSchema.getCcoid());
    boolean opSuccess = false;
    String learningId = bookmarkResponseSchema.getLearningid();
    itemValue.put(
        USERID_KEY,
        AttributeValue.builder()
            .s(bookmarkResponseSchema.getCcoid().concat(USERID_SUFFIX))
            .build());
    if (bookmarkResponseSchema.isBookmark()) {
      if (currentBookMarksMap.keySet().contains(learningId)) {
        LOG.info("PREVIOUS entry found - for insert."); // should not be real scenario
        itemValue.put(
            TIMESTAMP_KEY,
            AttributeValue.builder()
                .n((String) currentBookMarksMap.get(learningId))
                .build()); // old time
        DeleteItemRequest.Builder getDeleteItemReq = DeleteItemRequest.builder();
        getDeleteItemReq.key(itemValue);
        getDeleteItemReq.tableName(propertyConfig.getBookmarkTableName());
        LOG.info(
            "get del Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
        requestStartTime = System.currentTimeMillis();
        DeleteItemResponse getDelResponse = dbClient.deleteItem(getDeleteItemReq.build());
        LOG.info(
            "get del response received in {} ", (System.currentTimeMillis() - requestStartTime));
        if (getDelResponse.sdkHttpResponse().isSuccessful()) {
          LOG.info("PREVIOUS entry deleted.");
        }
      }
      itemValue.put(
          TIMESTAMP_KEY, AttributeValue.builder().n(Long.toString(getTime())).build()); // new time
      itemValue.put(BOOKMARK_KEY, AttributeValue.builder().s(learningId).build());
      Builder putItemReq = PutItemRequest.builder();
      putItemReq.tableName(propertyConfig.getBookmarkTableName()).item(itemValue);
      LOG.info("put Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
      requestStartTime = System.currentTimeMillis();
      PutItemResponse putResponse = dbClient.putItem(putItemReq.build());
      LOG.info("put response received in {} ", (System.currentTimeMillis() - requestStartTime));
      if (putResponse.sdkHttpResponse().isSuccessful()) {
        opSuccess = true;
      }
    } else {
      if (currentBookMarksMap.keySet().contains(learningId)) {
        LOG.info("PREVIOUS entry found - for delete.");
        itemValue.put(
            TIMESTAMP_KEY,
            AttributeValue.builder().n((String) currentBookMarksMap.get(learningId)).build());
        DeleteItemRequest.Builder deleteItemReq = DeleteItemRequest.builder();
        deleteItemReq.key(itemValue);
        deleteItemReq.tableName(propertyConfig.getBookmarkTableName());
        LOG.info("del Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
        requestStartTime = System.currentTimeMillis();
        DeleteItemResponse delResponse = dbClient.deleteItem(deleteItemReq.build());
        LOG.info("del response received in {} ", (System.currentTimeMillis() - requestStartTime));
        if (delResponse.sdkHttpResponse().isSuccessful()) {
          opSuccess = true;
        }
      }
    }
    if (opSuccess) {
      // update bookmark count in aurora
      BookmarkCountsEntity bookMarkCountsEntity =
          bookmarkCountsRepo.findByLearningItemIdAndPuid(
              bookmarkResponseSchema.getLearningid(), puid);
      if (bookMarkCountsEntity != null) {
        int count =
            bookmarkResponseSchema.isBookmark()
                ? bookMarkCountsEntity.getCount() + 1
                : bookMarkCountsEntity.getCount() - 1;
        bookMarkCountsEntity.setCount(count);
      } else {
        if (bookmarkResponseSchema.isBookmark()) {
          bookMarkCountsEntity = new BookmarkCountsEntity();
          bookMarkCountsEntity.setLearningItemId(bookmarkResponseSchema.getLearningid());
          bookMarkCountsEntity.setPuid(puid);
          bookMarkCountsEntity.setCount(1);
        }
      }
      if (bookMarkCountsEntity != null && bookMarkCountsEntity.getCount() >= 0) {
        bookmarkCountsRepo.save(bookMarkCountsEntity);
      }
      BookmarkResponseSchema responseSchema = new BookmarkResponseSchema();
      responseSchema.setId(bookmarkResponseSchema.getId());
      return responseSchema;
    } else {
      return null;
    }
  }

  /*
   * ["ACIDistNet1",...]
   */
  @Override
  public Set<String> getBookmarks(String email) {
    Map<String, Object> userBookMarksMap = getBookmarksWithTime(email);
    return userBookMarksMap.keySet();
  }

  /*
   * {"ACIDistNet1":1620903592718, ...}	 */
  // NOSONAR
  @Override
  public Map<String, Object> getBookmarksWithTime(String email) {
    LOG.info("Entering the fetch bookmarks");
    Map<String, Object> userBookMarksMap = new HashMap<>();
    long requestStartTime = System.currentTimeMillis();

    Map<String, String> expressionAttributesNames = new HashMap<>();
    expressionAttributesNames.put("#userid", USERID_KEY);

    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

    expressionAttributeValues.put(
        ":useridValue", AttributeValue.builder().s(email.concat(USERID_SUFFIX)).build());

    QueryRequest queryRequest =
        QueryRequest.builder()
            .tableName(propertyConfig.getBookmarkTableName())
            .keyConditionExpression("#userid = :useridValue")
            .expressionAttributeNames(expressionAttributesNames)
            .expressionAttributeValues(expressionAttributeValues)
            .build();
    LOG.info("Preprocessing done in {} ", (System.currentTimeMillis() - requestStartTime));
    requestStartTime = System.currentTimeMillis();
    QueryResponse queryResult = dbClient.query(queryRequest);
    LOG.info("response received in {} ", (System.currentTimeMillis() - requestStartTime));
    requestStartTime = System.currentTimeMillis();
    List<Map<String, AttributeValue>> attributeValues = queryResult.items();
    if (attributeValues.size() > 0) {
      attributeValues.forEach(
          bkRecord -> {
            String bookmark = bkRecord.get(BOOKMARK_KEY).s();
            String timestamp = bkRecord.get(TIMESTAMP_KEY).n();
            userBookMarksMap.put(bookmark, timestamp);
          });
    }
    LOG.info("Fetched bookmarks {} ", userBookMarksMap);
    LOG.info("final response in {}", (System.currentTimeMillis() - requestStartTime));
    return userBookMarksMap;
  }
}
