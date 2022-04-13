package com.cisco.cx.training.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntityPK;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.app.repo.PeerViewedRepo;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
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
	
	@Mock
	PeerViewedRepo peerViewedRepo;
	
	@Mock
	private HttpServletRequest request;
	
    @Mock 
    private ServletContext servletContext;
	
	@InjectMocks
	private ProductDocumentationService productDocumentationService;
	
	String learningTab = "Technology";

	
	@Test
	public void getAllLearningInfo()
	{		
		LearningRecordsAndFiltersModel a1 = productDocumentationService.getAllLearningInfo("mashery",null,null,"sortBy","sortOrder",learningTab);		
		assertEquals(0, a1.getLearningData().size());
		
		LearningRecordsAndFiltersModel a2 = productDocumentationService.getAllLearningInfo("mashery","searchToken",null,"sortBy","sortOrder",learningTab);		
		assertEquals(0, a2.getLearningData().size());
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		
		LearningRecordsAndFiltersModel a3 = productDocumentationService.getAllLearningInfo("mashery",null,aMock,"sortBy","sortOrder",learningTab);		
		assertEquals(0, a3.getLearningData().size());
		
		LearningRecordsAndFiltersModel a4 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"sortBy","sortOrder",learningTab);		
		assertEquals(0, a4.getLearningData().size());
		
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result); Set<String> hs = new HashSet<String>();hs.add("101");
		when(productDocumentationDAO.getAllNewCardIdsByCards(Mockito.anyString(),Mockito.anySet())).thenReturn(hs);
		
		LearningRecordsAndFiltersModel a5 = productDocumentationService.getAllLearningInfo("mashery","searchToken",aMock,"sortBy","sortOrder",learningTab);		
		assertEquals(0, a5.getLearningData().size());
		
		List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();
		LearningItemEntity learningItemEntity = new LearningItemEntity();
		learningItemEntity.setSortByDate("2016-02-03 00:00:00.0");
		dbCards.add(learningItemEntity);
		when(productDocumentationDAO.getAllLearningCardsByFilter(Mockito.anyString(),Mockito.anySet(),Mockito.any(Sort.class))).thenReturn(dbCards);
		LearningRecordsAndFiltersModel a6 = productDocumentationService.getAllLearningInfo("mashery",null,aMock,"sortBy","sortOrder",learningTab);		
		assertEquals(1, a6.getLearningData().size());
	}
	
	@Test
	public void getAllLearningFilters()
	{
		
		Map<String, Object> a1 = productDocumentationService.getAllLearningFilters(null,null,learningTab);			
		assertTrue(a1.size()>=1); //st=7
		
		Map<String, Object> a2 = productDocumentationService.getAllLearningFilters("searchToken",null,learningTab);		
		assertTrue(a2.size()>=1); //st=7		
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("For You", Arrays.asList(new String[]{"New"}));
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a3.size()>=1); //st=7
				
		Map<String, Object> a4 = productDocumentationService.getAllLearningFilters("searchToken",aMock,learningTab);		
		assertTrue(a4.size()>=1); //st=7
		
	}
	
	@Test
	public void testLGFilter()
	{
		HashMap<String, Object> aMock = new HashMap<String, Object>();	
		aMock.put("Language", Arrays.asList(new String[]{"English"}));
		 List<Map<String,Object>> dbListLG = new  ArrayList<Map<String,Object>>();
		 Map<String,Object> lgMap = new HashMap<String,Object>();lgMap.put("dbkey", "English");lgMap.put("dbvalue", "2");
		 dbListLG.add(lgMap);
		when(productDocumentationDAO.getAllLanguageWithCount(learningTab)).thenReturn(dbListLG);
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a3.size()>=1);
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
		
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a3.size()>=1); //st=7

		
		aMock.put("For You", Arrays.asList(new String[]{"New","Bookmarked","Sth"}));
		Map<String, Object> a32 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a32.size()>=1); //st=7
		
		
		when(learningContentRepo.findNew()).thenReturn(null);		
		Map<String, Object> a31 = productDocumentationService.getAllLearningFilters(null,null,learningTab);		
		assertTrue(a31.size()>=1); //st=7
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
		/*
		when(productDocumentationDAO.getAllStUcPsWithCount(Mockito.anyString())).thenReturn(mockDbST());
		when(productDocumentationDAO.getAllStUcPsWithCountByCards(Mockito.anyString(),Mockito.anySet())).thenReturn(mockDbST());
		*/
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a3.size()>=1); //st=7
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
	
	@Test
	public  void testSortSpecial()
	{
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(null);
		List<LearningItemEntity> aL = new ArrayList<LearningItemEntity>();
		LearningItemEntity le1 = new LearningItemEntity();
		LearningItemEntity le2 = new LearningItemEntity();
		le1.setTitle("abc");le2.setTitle("xyz");aL.add(le1); aL.add(le2);
		when(productDocumentationDAO.getAllLearningCards(Mockito.anyString(),Mockito.any(Sort.class))).thenReturn(aL);
		
		LearningRecordsAndFiltersModel a2t = productDocumentationService.getAllLearningInfo("mashery",null,null,"title","asc",learningTab);		
		assertEquals("abc", a2t.getLearningData().get(0).getTitle());
		
		LearningRecordsAndFiltersModel a2t2 = productDocumentationService.getAllLearningInfo("mashery",null,null,"title","desc",learningTab);		
		assertEquals("xyz", a2t2.getLearningData().get(0).getTitle());
	}
	
	@Test
	public void fetchMyPreferredLearnings() throws JsonProcessingException
	{
		HashMap<String, Object> preferences = new HashMap<String,Object>();
		List<String> roles= new ArrayList<String>();preferences.put("role", roles);
		roles.add("Customer Success Manager"); 
		List<String> ti = new ArrayList<String>(); preferences.put("timeinterval", ti);		
		Map<String,String> time = new HashMap<String,String>();
		time.put("startTime", "9:00 AM");time.put("endTime", "4:00 PM");time.put("timeZone", "PDT(UTC-07:30)"); 
		ti.add(new ObjectMapper().writeValueAsString(time));
		
		when(request.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
		
		Assertions.assertNotNull(productDocumentationService.fetchMyPreferredLearnings(
				"userId", null, null, "sortBy", "sortOrder", "puid", preferences, 25).getLearningData());
	}
	
	@Test
	public void addPeerLearnings() throws JsonProcessingException
	{
		when(request.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
		productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid");
		PeerViewedEntity en = new PeerViewedEntity();
		en.setCardId("cardId");en.setRole_name("role");en.setUpdatedTime(Timestamp.valueOf("2019-10-24 18:30:00"));
		Optional<PeerViewedEntity> enOp = Optional.of(en);
		when(peerViewedRepo.findById(Mockito.any(PeerViewedEntityPK.class))).thenReturn(enOp);
		productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid");
	}
	
	@Test
	public void codeCoverTest() throws JsonProcessingException
	{		
		when(request.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
		
		when(peerViewedRepo.save(Mockito.any(PeerViewedEntity.class))).thenThrow(new RuntimeException("Some test Exc"));
		productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid");
		
		HashMap<String, Object> preferences = new HashMap<String,Object>();
		List<String> ti = new ArrayList<String>(); preferences.put("timeinterval", ti);		
		Map<String,String> time = new HashMap<String,String>();
		time.put("startTime", "9:00 AM");time.put("endTime", "4:00 PM");time.put("timeZone", "PDT(UTC-7)"); 
		ti.add(new ObjectMapper().writeValueAsString(time));
		
		Assertions.assertNotNull(productDocumentationService.fetchMyPreferredLearnings(
				"userId", null, null, "sortBy", "sortOrder", "puid", preferences, 25));
	}
	
	@Test
	public void codeCovertest2()
	{
		
		List<PeerViewedEntity> a = new ArrayList<PeerViewedEntity>();
		for (int i=0;i<=55;i++)
		{
			PeerViewedEntity en = new PeerViewedEntity();
			en.setCardId(100+i + "");en.setRole_name("role101");en.setUpdatedTime(Timestamp.valueOf("2019-10-24 18:30:00"));
			a.add(en);
		}		
		List<LearningItemEntity> v= new ArrayList<LearningItemEntity>();
		for (int i=0;i<=55;i++)
		{
			LearningItemEntity ln = new LearningItemEntity();
			ln.setLearning_item_id(100+i + "");
			v.add(ln);
		}
		when(request.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
		when(productDocumentationDAO.getUserRole(Mockito.anyString())).thenReturn("role101");
		when(peerViewedRepo.findByRoleName(Mockito.anyString())).thenReturn(a);
		when(productDocumentationDAO.getAllLearningCardsByFilter(Mockito.anyString(), Mockito.anySet(), Mockito.any(Sort.class)))
		.thenReturn(v);
		Assertions.assertNotNull(productDocumentationService.fetchMyPreferredLearnings(
				"userId", null, null, "sortBy", "sortOrder", "puid", null, 5));

	}
	
	
	
	private List mockDbSTUCOnly()
	{
		List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
		Map<String, Object> oneRecord = new HashMap<String, Object>();
		oneRecord.put("successtrack", "Campus Network");
		oneRecord.put("usecase", "CSIM");		
		oneRecord.put("dbvalue", "10");
		value.add(oneRecord);
		return value;
	}
	
	private Map mockSTUCOnly()
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
	
	@Test
	public void testAllFiltersCount()
	{	
		NewLearningContentEntity n1 = new NewLearningContentEntity(); n1.setId("101");
		List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();result.add(n1);
		when(learningContentRepo.findNew()).thenReturn(result);
		
		when(productDocumentationDAO.getAllStUcWithCount(Mockito.anyString())).thenReturn(mockDbSTUCOnly());
		when(productDocumentationDAO.getAllStUcWithCountByCards(Mockito.anyString(),Mockito.anySet())).thenReturn(mockDbSTUCOnly());
		
		Map<String, Object> a3 = productDocumentationService.getAllLearningFilters(null,null,learningTab);		
		assertTrue(a3.size()>=1); //st=7
		
		HashMap<String, Object> aMock = new HashMap<String, Object>();		
		aMock.put("Success Tracks", mockSTUCOnly());
		Map<String, Object> a4 = productDocumentationService.getAllLearningFilters(null,aMock,learningTab);		
		assertTrue(a4.size()>=1); //st=7
	}
	
	@Test
	public void getRangeLWTest() throws JsonProcessingException
	{
		HashMap<String, Object> preferences = new HashMap<String,Object>();
		List<String> ti = new ArrayList<String>(); preferences.put("timeinterval", ti);		
		Map<String,String> time = new HashMap<String,String>();
		time.put("startTime", "12:00 AM");time.put("endTime", "12:30 AM");time.put("timeZone", "PDT(UTC-7)"); 
		ti.add(new ObjectMapper().writeValueAsString(time));
		
		List<LearningItemEntity> len = new ArrayList<LearningItemEntity>(); 
		LearningItemEntity ln = new LearningItemEntity();len.add(ln);
		ln.setLearning_item_id("101");ln.setSortByDate("2019-10-24 18:30:00");
		
		when(productDocumentationDAO.getUpcomingWebinars(Mockito.anyString())).thenReturn(len);
		
		when(request.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
		
		Assertions.assertNotNull(productDocumentationService.fetchMyPreferredLearnings(
				"userId", null, null, "sortBy", "sortOrder", "puid", preferences, 25));
	}
}



