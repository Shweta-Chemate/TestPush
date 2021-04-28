package com.cisco.cx.training.test;


import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;

import org.junit.Assert;

@RunWith(SpringRunner.class)
public class ProductDocumentationServiceTest {	
	@Mock
    private PropertyConfiguration config;
	
	@Mock
	private LearningBookmarkDAO learningDAO;
	
	@Mock
	private NewLearningContentRepo learningContentRepo;
	
	@Mock
	private ProductDocumentationDAO productDocumentationDAO;
	
	@Mock
	private PartnerProfileService partnerProfileService;
	
	@InjectMocks
	private ProductDocumentationService productDocumentationService;

	
	@Test
	public void getAllLearningInfo()
	{		
		LearningRecordsAndFiltersModel a1 = productDocumentationService.getAllLearningInfo("mashery",null,null,"sortBy","sortOrder");		
		Assert.assertEquals(0, a1.getLearningData().size());
		
		LearningRecordsAndFiltersModel a2 = productDocumentationService.getAllLearningInfo("mashery","searchToken",null,"sortBy","sortOrder");		
		Assert.assertEquals(0, a2.getLearningData().size());
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		
		LearningRecordsAndFiltersModel a3 = productDocumentationService.getAllLearningInfo("mashery",null,aMock,"sortBy","sortOrder");		
		Assert.assertEquals(0, a3.getLearningData().size());
		
		LearningRecordsAndFiltersModel a4 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"sortBy","sortOrder");		
		Assert.assertEquals(0, a4.getLearningData().size());
		
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result);
		
		LearningRecordsAndFiltersModel a5 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"sortBy","sortOrder");		
		Assert.assertEquals(0, a5.getLearningData().size());
		
		List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();dbCards.add(new LearningItemEntity());
		when(productDocumentationDAO.getAllLearningCardsByFilter(Mockito.anySet(),Mockito.any(Sort.class))).thenReturn(dbCards);
		LearningRecordsAndFiltersModel a6 = productDocumentationService.getAllLearningInfo("mashery",null,aMock,"sortBy","sortOrder");		
		Assert.assertEquals(1, a6.getLearningData().size());
	}
	
	@Test
	public void getAllLearningFilters()
	{
		
		HashMap<String, Object> a1 = productDocumentationService.getAllLearningFilters(null,null);		
		Assert.assertTrue(a1.size()>=6); //st=7
		
		HashMap<String, Object> a2 = productDocumentationService.getAllLearningFilters("searchToken",null);		
		Assert.assertTrue(a2.size()>=6); //st=7		
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		HashMap<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a3.size()>=6); //st=7
				
		HashMap<String, Object> a4 = productDocumentationService.getAllLearningFilters("searchToken",aMock);		
		Assert.assertTrue(a4.size()>=6); //st=7
		
	}
	
}



