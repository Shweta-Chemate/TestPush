package com.cisco.cx.training.test;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.ElasticSearchConfig;
import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;
import com.cisco.cx.training.models.Community;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { PropertyConfiguration.class, ElasticSearchConfig.class, CommunityDAOImpl.class })
public class CommunitytDAOTest {

	@TestConfiguration
	static class CommunityDAOImplTestContextConfiguration {

		@Bean
		public CommunityDAO communityDAO() {
			return new CommunityDAOImpl();
		}

		@Bean
		public RestHighLevelClient elasticRestClient() {
			return new ElasticSearchConfig().client();
		}
	}

	// @Autowired private RestHighLevelClient elasticRestClient;

	@Autowired
	private CommunityDAO communityDAO;

	@Test
	public void insertCommunityTest() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		communityDAO.insertCommunity(community);
	}

	@Test
	public void getAllCommunitiesTest() {
		communityDAO.getCommunities();
	}

	@Test
	public void getFilteredCommunitiesTest() {
		communityDAO.getFilteredCommunities("", "");
	}
}
