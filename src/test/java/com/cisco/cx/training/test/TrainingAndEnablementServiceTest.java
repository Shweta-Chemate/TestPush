package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.SuccessTrackAndUseCases;

@RunWith(SpringRunner.class)
public class TrainingAndEnablementServiceTest {
	
	@Mock
	private CommunityDAO communityDAO;
	
	@Mock
	private SuccessTalkDAO successTalkDAO;

	@InjectMocks
	private TrainingAndEnablementService trainingAndEnablementService = new TrainingAndEnablementServiceImpl() ;


	@Test
	public void insertCommunityTest() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		
		when(communityDAO.insertCommunity(community)).thenReturn(community);
		trainingAndEnablementService.insertCommunity(community);
	}

	@Test
	public void getUsecasesTest() {
		Map<String, List<String>> useCases = new HashMap<String, List<String>>();
		useCases.put("IBN", new ArrayList<>(Arrays.asList("Campus Network Assurance", "Network Device Onboarding",
				"Campus Software Image management", "Campus Network Segmentation", "Scalable Access Policy")));
		SuccessTrackAndUseCases successTrackAndUseCases = new SuccessTrackAndUseCases();
		successTrackAndUseCases.setUseCases(useCases);
		
		trainingAndEnablementService.getUsecases();
	}

	@Test
	public void testGetLearnings() {
		trainingAndEnablementService.getLearning();
	}

	@Test
	public void getAllCommunitiesTest() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getCommunities()).thenReturn(communities);
		trainingAndEnablementService.getAllCommunities();
	}

	@Test
	public void getFilteredCommunitiesTest() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getFilteredCommunities("IBN", "usecase")).thenReturn(communities);
		trainingAndEnablementService.getFilteredCommunities("IBN", "usecase");
	}
}
