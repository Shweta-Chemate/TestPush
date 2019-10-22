package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccessTalkSession.RegistrationStatusEnum;

@Repository
public class SuccessTalkDAOImpl implements SuccessTalkDAO{
	
	private static final Logger LOG = LoggerFactory.getLogger(SuccessTalkDAOImpl.class);
	
	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
    @Autowired
    private PropertyConfiguration config;
    
    public SuccessTalk insertSuccessTalk(SuccessTalk successTalk) {
        // save the entry to ES
        try {
        	successTalk = elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class);
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
        return successTalk;
    }
    
    public List<SuccessTalk> getAllSuccessTalks(){
    	
		List<SuccessTalk> successTalkES = new ArrayList<>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<SuccessTalk> results = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class);

			results.getDocuments().forEach(successTalk -> {
				successTalk.setImageUrl("https://www.cisco.com/web/fw/tools/ssue/cp/lifecycle/atx/images/ATX-DNA-Center-Wireless-Assurance.png");
				successTalk.setRecordingUrl("https://tklcs.cloudapps.cisco.com/tklcs/TKLDownloadServlet?nodeRef=workspace://SpacesStore/cf85fc26-78e0-488e-af04-390fb2c55ad4&activityId=2&fileId=122233");
				successTalk.setDuration(4500L);
				successTalkES.add(successTalk);
			});

		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		Collections.sort(successTalkES);
		return successTalkES;

    }
    
    public List<SuccessTalk> getFilteredSuccessTalks(String solution, String usecase){
    	
		List<SuccessTalk> successTalkES = new ArrayList<>();
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
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		return successTalkES;

    }
    
	@Override
	public String registerUser(String successTalkSessionId, String successTalkId) {
		try {
			SuccessTalk successTalk= elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class);
			List<SuccessTalkSession> successTalkSessions = successTalk.getSessions();
			successTalkSessions.forEach(session-> {
				if(session.getSessionId().equals(successTalkSessionId))
				{
					session.setRegistrationStatus(RegistrationStatusEnum.REGISTERED);
					this.insertSuccessTalk(successTalk);
				}
			});
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
		return successTalkId;
	}
	
	@Override
	public String cancelRegistration(String successTalkSessionId, String successTalkId) {
		try {
			SuccessTalk successTalk= elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class);
			List<SuccessTalkSession> successTalkSessions = successTalk.getSessions();
			successTalkSessions.forEach(session-> {
				if(session.getSessionId().equals(successTalkSessionId))
				{
					session.setRegistrationStatus(RegistrationStatusEnum.CANCELLED);
					this.insertSuccessTalk(successTalk);
				}
			});
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
		return successTalkId;
	}

}