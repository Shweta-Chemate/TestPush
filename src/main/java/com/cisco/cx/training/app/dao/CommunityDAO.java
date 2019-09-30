package com.cisco.cx.training.app.dao;

import java.io.IOException;
import java.util.*;

import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.ElasticSearchResults;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityDAO {
	private static final Logger LOG = LoggerFactory.getLogger(CommunityDAO.class);

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
    private final String INDEX = "cxpp_training_enablement_communities";


    public Community insertCommunity(Community community){
        
        try {
			community = elasticSearchDAO.saveEntry(INDEX, community, Community.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return community;
    }
    
    public List<Community> getCommunities(){
    	
		List<Community> communityES = new ArrayList<Community>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<Community> results = elasticSearchDAO.query(INDEX, sourceBuilder, Community.class);

			results.getDocuments().forEach(community -> {
				communityES.add(community);
			});

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
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

		try {
			ElasticSearchResults<Community> results = elasticSearchDAO.query(INDEX, sourceBuilder, Community.class);

			results.getDocuments().forEach(community -> {
				communityES.add(community);
			});

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		}

		return communityES;

    }
}