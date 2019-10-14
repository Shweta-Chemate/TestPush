package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.ElasticSearchResults;

@RunWith(SpringRunner.class)
public class CommunityDAOTest {

	private final String INDEX = "cxpp_training_enablement_communities";

	@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@InjectMocks
	private CommunityDAO communityDAO = new CommunityDAOImpl();

	@Test
	public void insertSuccessTalk() throws IOException {
		Community community = getCommunity();
		communityDAO.insertCommunity(community);
		when(elasticSearchDAO.saveEntry(INDEX, community, Community.class)).thenReturn(community);
		communityDAO.insertCommunity(community);
	}

	@Test
	public void getAllSuccessTalks() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		Community community = getCommunity();
		ElasticSearchResults<Community> results = new ElasticSearchResults<>();
		results.addDocument(community);
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Community.class)).thenReturn(results);
		communityDAO.getCommunities();
	}

	@Test
	public void getFilteredCommunities() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", "IBN");
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", "usecase");
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		Community community = getCommunity();
		ElasticSearchResults<Community> results = new ElasticSearchResults<>();
		results.addDocument(community);
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Community.class)).thenReturn(results);
		communityDAO.getFilteredCommunities("IBN", "usecase");
	}

	private Community getCommunity() {
		Community community = new Community();
		community.setDescription("description");
		community.setDocId("id");
		community.setName("name");
		community.setSolution("solution");
		community.setUrl("IBN");
		community.setUsecase("usecase");
		return community;
	}
}
