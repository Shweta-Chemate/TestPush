package com.cisco.cx.training.app.dao;


import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Refer https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.2/java-rest-high-supported-apis.html
 */
@Repository
public class ElasticSearchDAO {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticSearchDAO.class);
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RestHighLevelClient elasticRestClient;

	public boolean isElasticSearchRunning() throws Exception {
		return elasticRestClient.ping(RequestOptions.DEFAULT);
	}

	/**
	 * doesIndexExist - self explanatory :)
	 * @param index - the index name
	 * @return - true or false
	 * @throws Exception - IOException if there is a network error
	 */
	public boolean doesIndexExist(String index) throws IOException {
		GetIndexRequest request = new GetIndexRequest(index);
		return elasticRestClient.indices().exists(request, RequestOptions.DEFAULT);
	}

	/**
	 * saveEntry - saves a document to ES index. upserts the document. creates the document if it doesnt exist, or updates existing document
	 * @param index - the name of index
	 * @param entity - the object to be saved
	 * @param type - the Class type of the object (E.g Employee.class)
	 * @return - the saved object
	 * @throws Exception
	 */
	public <T extends HasId> T saveEntry(String index, T entity, Class<T> type) throws IOException {
		if (StringUtils.isBlank(entity.getDocId())) {
			// if entity id is blank, set a UUID as the id
			entity.setDocId(UUID.randomUUID().toString());
		}

		UpdateRequest request = new UpdateRequest(index, entity.getDocId())
								.doc(objectMapper.writeValueAsString(entity), XContentType.JSON)
								.fetchSource(true)
								.docAsUpsert(true);

		return objectMapper.convertValue(elasticRestClient.update(request, RequestOptions.DEFAULT).getGetResult().getSource(), type);
	}

	/**
	 * getDocument - fetches a single document from index by its document id
	 * @param index - the index name
	 * @param documentId - the document id
	 * @param type - a model class of the document structure
	 * @return - an instance of the model class as the document
	 * @throws IOException - if there is a network error
	 */
	public <T> T getDocument(String index, String documentId, Class<T> type) throws IOException {
		if (this.doesIndexExist(index)) {
			GetResponse getResponseDocument = elasticRestClient.get(new GetRequest(index, documentId), RequestOptions.DEFAULT);
			return objectMapper.readValue(getResponseDocument.getSourceAsString(), type);
		} else {
			LOG.warn("Index {} does not exist. No documents to retrieve.", index);
			return null;
		}
	}

	/**
	 * query - queries the ES index using the SourceBuilder passed to it
	 * @param index - the name of the index to query
	 * @param sourceBuilder - the sourcebuilder object prepared before calling this method with all query params and options
	 * @param type - the Class type of the object (E.g Employee.class)
	 * @return - a list of objects matching the query, empty list if no records found
	 * @throws IOException
	 */
	public <T extends HasId> ElasticSearchResults<T> query(String index, SearchSourceBuilder sourceBuilder, Class<T> type) throws IOException {
		ElasticSearchResults<T> esQueryResponse = null;

		long queryStartTime = System.currentTimeMillis();
		try {
			if (this.doesIndexExist(index)) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(index);
				searchRequest.source(sourceBuilder);

				SearchResponse response = elasticRestClient.search(searchRequest, RequestOptions.DEFAULT);

				SearchHits hits = response.getHits();
				SearchHit[] searchHit = hits.getHits();
				final ElasticSearchResults<T> resultsCollector = new ElasticSearchResults<T>(searchHit.length, hits.getTotalHits().value);

				if (searchHit.length > 0) {
					LOG.debug("Hits:{}", searchHit.length);
					Arrays.stream(searchHit).forEach(hit -> {
						T curObj = objectMapper.convertValue(hit.getSourceAsMap(), type);
						// set the document id from ES if its not set on the object
						if (StringUtils.isBlank(curObj.getDocId())) {
							curObj.setDocId(hit.getId());
						}

						resultsCollector.addDocument(curObj);
					});

					esQueryResponse = resultsCollector;
				} else {
					LOG.debug("No Hits!!");
				}
			} else {
				LOG.warn("Index {} does not exist. Nothing to query. No Results.", index);
			}
		} finally {
			// return empty response object if no search results
			esQueryResponse = (esQueryResponse == null) ? new ElasticSearchResults<T>(0,0) : esQueryResponse;
			LOG.info("PERF_TIME_TAKEN ELASTICSEARCH | " + index + " | " + (System.currentTimeMillis() - queryStartTime));
		}

		return esQueryResponse;
	}

	/**
	 * delete - deletes a document from the index
	 * @param index - the name of the index
	 * @param documentId - the id of the document to delete
	 * @return - a Result object with the status of the operation (Result.DELETED or Result.NOT_FOUND)
	 * @throws Exception
	 */
	public DocWriteResponse.Result deleteOne(String index, String documentId) throws IOException {
		if (this.doesIndexExist(index)) {
			DeleteRequest request = new DeleteRequest(index, documentId);
			return elasticRestClient.delete(request, RequestOptions.DEFAULT).getResult();
		} else {
			LOG.warn("Index {} does not exist. Nothing to delete.", index);
			return DocWriteResponse.Result.NOOP;
		}
	}

	/**
	 * deleteByQuery - deletes multiple documents from an index given a query
	 * @param index - the name of the index to delete documents from
	 * @param deleteQuery - the QueryBuilder object with query conditions
	 * @return - number of records deleted
	 * @throws Exception
	 */
	public long deleteByQuery(String index, QueryBuilder deleteQuery) throws IOException {
		if (this.doesIndexExist(index)) {
			DeleteByQueryRequest request = new DeleteByQueryRequest(index).setQuery(deleteQuery);
			return elasticRestClient.deleteByQuery(request, RequestOptions.DEFAULT).getDeleted();
		} else {
			LOG.warn("Index {} does not exist. No documents to delete.", index);
			return 0L;
		}
	}
	

	
	public long countRecords(String index) throws IOException
	{
		CountRequest countRequest = new CountRequest();
		countRequest.indices(index);
		CountResponse countResponse = elasticRestClient.count(countRequest, RequestOptions.DEFAULT);
		return countResponse.getCount();
	}
	
	public long countRecordsWithFilter(String index, SearchSourceBuilder sourceBuilder) throws IOException
	{
		CountRequest countRequest = new CountRequest();
		countRequest.indices(index);
		countRequest.source(sourceBuilder);
		CountResponse countResponse = elasticRestClient.count(countRequest, RequestOptions.DEFAULT);
		return countResponse.getCount();
	}
}