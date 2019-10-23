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
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccessTrackAndUseCases;

@RunWith(SpringRunner.class)
public class TrainingAndEnablementServiceTest {

	@Mock
	private CommunityDAO communityDAO;

	@Mock
	private SuccessTalkDAO successTalkDAO;

	@InjectMocks
	private TrainingAndEnablementService trainingAndEnablementService = new TrainingAndEnablementServiceImpl();

	@Test
	public void insertCommunityTest() {
		Community community = getCommunity();
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

//	@Test
//	public void testGetLearnings() {
//		trainingAndEnablementService.getAllLearning();
//	}

	@Test
	public void getAllCommunitiesTest() {
		Community community = getCommunity();
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getCommunities()).thenReturn(communities);
		trainingAndEnablementService.getAllCommunities();
	}

	@Test
	public void getFilteredCommunitiesTest() {
		Community community = getCommunity();
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getFilteredCommunities("IBN", "usecase")).thenReturn(communities);
		trainingAndEnablementService.getFilteredCommunities("IBN", "usecase");
	}

	@Test
	public void getAllSuccessTalksTest() {
		SuccessTalk successTalk = getSuccessTask();
		when(successTalkDAO.getAllSuccessTalks()).thenReturn(Arrays.asList(successTalk));
		trainingAndEnablementService.getAllSuccessTalks();
	}
	
	@Test
	public void getFilteredSuccessTalksTest() {
		SuccessTalk successTalk = getSuccessTask();
		when(successTalkDAO.getFilteredSuccessTalks("IBN", "usecase")).thenReturn(Arrays.asList(successTalk));
		trainingAndEnablementService.getFilteredSuccessTalks("IBN", "usecase");
	}
	
	@Test
	public void insertSuccessTalksTest() {
		SuccessTalk successTalk = getSuccessTask();
		when(successTalkDAO.insertSuccessTalk(successTalk)).thenReturn(successTalk);
		trainingAndEnablementService.insertSuccessTalk(successTalk);
	}
	
	@Test
	public void registerUserToSuccessTalkSession() {
		String sessionId = "sessionId";
		String successTalkId = "successTalkId";
		when(successTalkDAO.registerUser("sessionId", "successTalkId")).thenReturn("successTalkId");
		trainingAndEnablementService.registerUserToSuccessTalkSession(sessionId, successTalkId);
	}
	
	@Test
	public void cancelUserToSuccessTalkSession() {
		String sessionId = "sessionId";
		String successTalkId = "successTalkId";
		when(successTalkDAO.cancelRegistration("sessionId", "successTalkId")).thenReturn("successTalkId");
		trainingAndEnablementService.cancelUserToSuccessTalkSession(sessionId, successTalkId);
	}

	private Community getCommunity() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		return community;
	}
	
	private SuccessTalk getSuccessTask() {
		SuccessTalk successTalk = new SuccessTalk();
		successTalk.setBookmark(true);
		successTalk.setDescription("");
		successTalk.setDocId("id");
		successTalk.setDuration(10L);
		successTalk.setImageUrl("");
		successTalk.setRecordingUrl("");
		List<SuccessTalkSession> sessions = new ArrayList<>();
		SuccessTalkSession session = new SuccessTalkSession();
		session.setDocId("");
		session.setPresenterName("John Doe");
		session.setRegion("region");
		session.setRegistrationUrl("");
		session.setScheduled(true);
		session.setSessionId("");
		session.setSessionStartDate("");
		Arrays.asList(session);
		successTalk.setSessions(sessions);
		return successTalk;
	}
}
