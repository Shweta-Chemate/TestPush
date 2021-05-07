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
		
		LearningRecordsAndFiltersModel a4 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"sortBy","asc");		
		Assert.assertEquals(0, a4.getLearningData().size());
		
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result);
		
		LearningRecordsAndFiltersModel a5 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"date","sortOrder");		
		Assert.assertEquals(0, a5.getLearningData().size());
		
		LearningItemEntity li = new LearningItemEntity(); li.setSortByDate("2021-04-20 00:00:00");
		List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();dbCards.add(li);
		when(productDocumentationDAO.getAllLearningCardsByFilter(Mockito.anySet(),Mockito.any(Sort.class))).thenReturn(dbCards);
		LearningRecordsAndFiltersModel a6 = productDocumentationService.getAllLearningInfo("mashery",new String(),aMock,null,null);		
		Assert.assertEquals(1, a6.getLearningData().size());
		
	}
	
	@Test
	public void getAllLearningFilters()
	{
		
		Map<String, Object> a1 = productDocumentationService.getAllLearningFilters(null,null);			
		Assert.assertTrue(a1.size()>=1); //st=7
		
		Map<String, Object> a2 = productDocumentationService.getAllLearningFilters("searchToken",null);		
		Assert.assertTrue(a2.size()>=1); //st=7		
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a3.size()>=1); //st=7
				
		Map<String, Object> a4 = productDocumentationService.getAllLearningFilters("searchToken",aMock);		
		Assert.assertTrue(a4.size()>=1); //st=7
		
	}
	
	@Test
	public void testLGFilter()
	{
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("Language", Arrays.asList(new String[]{"English"}));
		 List<Map<String,Object>> dbListLG = new  ArrayList<Map<String,Object>>();
		 Map<String,Object> lgMap = new HashMap<String,Object>();lgMap.put("dbkey", "English");lgMap.put("dbvalue", "2");
		 dbListLG.add(lgMap);
		when(productDocumentationDAO.getAllLanguageWithCount()).thenReturn(dbListLG);
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a3.size()>=1);
	}
	
	@Test
	public void testYouFilter()
	{
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		aMock.put("Language", Arrays.asList(new String[]{"English"}));
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result);
		
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a3.size()>=1); //st=7

		
		aMock.put("For You", Arrays.asList(new String[]{"New","Bookmarked","Sth"}));
		Map<String, Object> a32 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a32.size()>=1); //st=7
		
		
		when(learningContentRepo.findNew()).thenReturn(null);		
		Map<String, Object> a31 = productDocumentationService.getAllLearningFilters(null,null);		
		Assert.assertTrue(a31.size()>=1); //st=7
	}
	
	@Test
	public void testAllFilters()
	{
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		aMock.put("Language", Arrays.asList(new String[]{"English"}));
		aMock.put("Technology", Arrays.asList(new String[]{"Enterprise Network"}));
		aMock.put("Documentation", Arrays.asList(new String[]{"Device setup"}));
		aMock.put("Live Events", Arrays.asList(new String[]{"APAC"}));
		aMock.put("Content Type", Arrays.asList(new String[]{"PPT"}));
		aMock.put("Success Tracks", mockST());
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result);
		
		when(productDocumentationDAO.getAllStUcPsWithCount()).thenReturn(mockDbST());
		when(productDocumentationDAO.getAllStUcPsWithCountByCards(Mockito.anySet())).thenReturn(mockDbST());
		
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock);		
		Assert.assertTrue(a3.size()>=1); //st=7
	}
	
	private List mockDbST()
	{
		List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
		Map<String, Object> oneRecord = new HashMap<String, Object>();
		oneRecord.put("successtrack", "Campus Network");
		oneRecord.put("usecase", "CSIM");
		oneRecord.put("pitstop", "Implement");
		oneRecord.put("dbvalue", "10");
		value.add(oneRecord);
		return value;
	}
	
	private Map mockST()
	{
		Map<String,Map<String,List<String>>> st = new HashMap<String,Map<String,List<String>>>();
		//map.put("Success Tracks", st);
		
		Map<String,List<String>> ucCN = new HashMap<String,List<String>>();   
		List<String> csimPS = new ArrayList<String>(); csimPS.add("Onboard");csimPS.add("Implement");ucCN.put("CSIM", csimPS);
		List<String> xyzPS = new ArrayList<String>(); xyzPS.add("Use");ucCN.put("XYZ", xyzPS);
		st.put("Campus Network", ucCN);
		
		Map<String,List<String>> ucSY = new HashMap<String,List<String>>();
		List<String> sy1PS = new ArrayList<String>(); sy1PS.add("Anti-Virus");sy1PS.add("Firewall");ucSY.put("Security1", sy1PS);
		List<String> abcPS = new ArrayList<String>(); abcPS.add("Umbrella");ucSY.put("ABC", abcPS);
		st.put("Security", ucSY);
		
		return st;
	}
	
}



