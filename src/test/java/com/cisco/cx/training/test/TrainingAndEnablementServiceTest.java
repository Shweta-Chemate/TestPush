package com.cisco.cx.training.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.models.Community;

@RunWith(SpringRunner.class)
public class TrainingAndEnablementServiceTest {

	@MockBean
	private TrainingAndEnablementServiceImpl trainingAndEnablementService;

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
		trainingAndEnablementService.insertCommunity(community);
	}

	@Test
	public void getUsecasesTest() {
		trainingAndEnablementService.getUsecases();
	}

	@Test
	public void getLearningTest() {
		trainingAndEnablementService.getLearning();
	}

	@Test
	public void getAllCommunitiesTest() {
		trainingAndEnablementService.getAllCommunities();
	}

	@Test
	public void getFilteredCommunitiesTest() {
		trainingAndEnablementService.getFilteredCommunities("", "");
	}
}
