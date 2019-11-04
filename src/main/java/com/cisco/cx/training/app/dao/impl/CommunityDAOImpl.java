package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.ElasticSearchResults;

@Repository
public class CommunityDAOImpl implements CommunityDAO {
	private static final Logger LOG = LoggerFactory.getLogger(CommunityDAOImpl.class);

	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	private static final String INDEX = "cxpp_training_enablement_communities";

	public Community insertCommunity(Community community) {

		try {
			community = elasticSearchDAO.saveEntry(INDEX, community, Community.class);
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
		return community;
	}

	public List<Community> getCommunities() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();

		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<Community> results = elasticSearchDAO.query(INDEX, sourceBuilder, Community.class);
			return results.getDocuments().stream().map(community -> community).collect(Collectors.toList());
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
	}
}