package com.cisco.cx.training.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;
import com.cisco.cx.training.models.Community;

@RunWith(SpringRunner.class)
public class CommunitytDAOTest {

	@MockBean
	private CommunityDAOImpl communityDAO;

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
