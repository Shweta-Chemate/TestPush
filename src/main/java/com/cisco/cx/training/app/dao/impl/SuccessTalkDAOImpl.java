package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessTalk;

@Repository
public class SuccessTalkDAOImpl implements SuccessTalkDAO{
	
	private static final Logger LOG = LoggerFactory.getLogger(SuccessTalkDAOImpl.class);

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
    @Autowired
    private PropertyConfiguration config;
    
    public SuccessTalk insertSuccessTalk(SuccessTalk successTalk) {
        // save the entry to ES
        try {
        	successTalk = elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return successTalk;
    }
    
    public List<SuccessTalk> getAllSuccessTalks(){
    	
		List<SuccessTalk> successTalkES = new ArrayList<SuccessTalk>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<SuccessTalk> results = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class);

			results.getDocuments().forEach(successTalk -> {
				successTalkES.add(successTalk);
			});

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		}

		return successTalkES;

    }
    
    public List<SuccessTalk> getFilteredSuccessTalks(String solution, String usecase){
    	
		List<SuccessTalk> successTalkES = new ArrayList<SuccessTalk>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", solution);
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", usecase);
		
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<SuccessTalk> results = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class);

			results.getDocuments().forEach(successTalk -> {
				successTalkES.add(successTalk);
			});

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		}

		return successTalkES;

    }

}