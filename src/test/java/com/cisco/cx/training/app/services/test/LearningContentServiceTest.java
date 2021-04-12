package com.cisco.cx.training.app.services.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.impl.LearningContentServiceImpl;

@RunWith(SpringRunner.class)
public class LearningContentServiceTest {

	@Mock
	private NewLearningContentDAO learningContentDAO;
	
	@Mock
	private SuccessAcademyDAO successAcademyDAO;
	
	@InjectMocks
	private LearningContentService learningContentService=new LearningContentServiceImpl(); 
	
	@Test
	public void testFetchPIWs() {
		List<NewLearningContentEntity> result = new ArrayList<>();
		when(learningContentDAO.listPIWs(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(result);
	    assertNotNull(learningContentService.fetchPIWs("test","test","test","test:test","test"));
	}
	
	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> result = getLearningEntities();
		when(learningContentDAO.fetchSuccesstalks(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(result);
	    assertNotNull(learningContentService.fetchSuccesstalks("test","test","test:test","test"));
	}

	private List<NewLearningContentEntity> getLearningEntities() {
		List<NewLearningContentEntity> resp=new ArrayList<NewLearningContentEntity>();
		NewLearningContentEntity learningContentEntity=new NewLearningContentEntity();
		learningContentEntity.setId("test");
		learningContentEntity.setTitle("test");
		learningContentEntity.setDuration("test");
		learningContentEntity.setPresenterName("test");
		learningContentEntity.setRegistrationUrl("test");
		learningContentEntity.setRegion("test");
		learningContentEntity.setSessionStartDate(new Timestamp(System.currentTimeMillis()));
		resp.add(learningContentEntity);
		return resp;
	}
	
	@Test
	public void getIndexCounts() {
		when(learningContentDAO.getSuccessTalkCount()).thenReturn(2);
		when(successAcademyDAO.count()).thenReturn((long) 2);
		learningContentService.getIndexCounts();
	}
	
}

