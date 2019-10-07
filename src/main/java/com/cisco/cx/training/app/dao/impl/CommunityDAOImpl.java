package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.*;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityDAOImpl implements CommunityDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommunityDAOImpl.class);
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RestHighLevelClient elasticRestClient;
	
    private final String INDEX = "cxpp_training_enablement_communities";


    public Community insertCommunity(Community community){
        try {
        	if (StringUtils.isBlank(community.getDocId())) {
    			// if entity id is blank, set a UUID as the id
        		community.setDocId(UUID.randomUUID().toString());
    		}

    		UpdateRequest request = new UpdateRequest(INDEX, community.getDocId())
    								.doc(objectMapper.writeValueAsString(community), XContentType.JSON)
    								.fetchSource(true)
    								.docAsUpsert(true);

    		community = objectMapper.convertValue(elasticRestClient.update(request, RequestOptions.DEFAULT).getGetResult().getSource(), Community.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return community;
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
	
    public List<Community> getCommunities(){
    	
		List<Community> communityES = new ArrayList<Community>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		ElasticSearchResults<Community> esQueryResponse = null;

		long queryStartTime = System.currentTimeMillis();
		try {
			if (this.doesIndexExist(INDEX)) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(INDEX);
				searchRequest.source(sourceBuilder);

				SearchResponse response = elasticRestClient.search(searchRequest, RequestOptions.DEFAULT);

				SearchHits hits = response.getHits();
				SearchHit[] searchHit = hits.getHits();
				final ElasticSearchResults<Community> resultsCollector = new ElasticSearchResults<Community>(searchHit.length, hits.getTotalHits().value);

				if (searchHit.length > 0) {
					LOG.debug("Hits:{}", searchHit.length);
					Arrays.stream(searchHit).forEach(hit -> {
						Community curObj = objectMapper.convertValue(hit.getSourceAsMap(), Community.class);
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
				LOG.warn("Index {} does not exist. Nothing to query. No Results.", INDEX);
			}
			esQueryResponse.getDocuments().forEach(community -> { communityES.add(community); });
		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API"); 
		} finally {
			// return empty response object if no search results
			esQueryResponse = (esQueryResponse == null) ? new ElasticSearchResults<Community>(0,0) : esQueryResponse;
			esQueryResponse.getDocuments().forEach(community -> { communityES.add(community); });
			LOG.info("PERF_TIME_TAKEN ELASTICSEARCH | " + INDEX + " | " + (System.currentTimeMillis() - queryStartTime));
		}
		

		return communityES;
    }
    
    public List<Community> getFilteredCommunities(String solution, String usecase){
    	
		List<Community> communityES = new ArrayList<Community>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", solution);
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", usecase);
		
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		
		ElasticSearchResults<Community> esQueryResponse = null;

		long queryStartTime = System.currentTimeMillis();

		try {
			
			if (this.doesIndexExist(INDEX)) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(INDEX);
				searchRequest.source(sourceBuilder);

				SearchResponse response = elasticRestClient.search(searchRequest, RequestOptions.DEFAULT);

				SearchHits hits = response.getHits();
				SearchHit[] searchHit = hits.getHits();
				final ElasticSearchResults<Community> resultsCollector = new ElasticSearchResults<Community>(searchHit.length, hits.getTotalHits().value);

				if (searchHit.length > 0) {
					LOG.debug("Hits:{}", searchHit.length);
					Arrays.stream(searchHit).forEach(hit -> {
						Community curObj = objectMapper.convertValue(hit.getSourceAsMap(), Community.class);
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
				LOG.warn("Index {} does not exist. Nothing to query. No Results.", INDEX);
			}
			esQueryResponse = (esQueryResponse == null) ? new ElasticSearchResults<Community>(0,0) : esQueryResponse;
			esQueryResponse.getDocuments().forEach(community -> {
				communityES.add(community);
			});

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		} finally {
			esQueryResponse = (esQueryResponse == null) ? new ElasticSearchResults<Community>(0,0) : esQueryResponse;
			esQueryResponse.getDocuments().forEach(community -> { communityES.add(community); });
			LOG.info("PERF_TIME_TAKEN ELASTICSEARCH | " + INDEX + " | " + (System.currentTimeMillis() - queryStartTime));
		}

		return communityES;

    }
}