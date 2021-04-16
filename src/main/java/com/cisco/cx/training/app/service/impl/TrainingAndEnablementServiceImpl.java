package com.cisco.cx.training.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.PartnerPortalLookupDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.PartnerPortalLookUpEntity;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserProfile;
import com.cisco.cx.training.util.SuccessAcademyMapper;

@Service
public class TrainingAndEnablementServiceImpl implements TrainingAndEnablementService {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private CommunityDAO communityDAO;

	@Autowired
	private SuccessTalkDAO successTalkDAO;

	@Autowired
	private SuccessAcademyDAO successAcademyDAO;
	
	@Autowired
	private PartnerPortalLookupDAO partnerPortalLookupDAO;

	@SuppressWarnings("unused")
	@Autowired
	private SmartsheetDAO smartsheetDAO;

	@Autowired
	private BookmarkDAO bookmarkDAO;
	
	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private PartnerProfileService partnerProfileService;
	
	@Autowired
	private LearningBookmarkDAO learningDAO;
	
	@Autowired
	private ProductDocumentationService productDocumentationService;

	@Autowired
	private NewLearningContentDAO learningContentDAO;

	
	private static final String CXPP_UI_TAB_PREFIX = "CXPP_UI_TAB_";
	
	@Override
	public List<SuccessAcademyLearning> getAllSuccessAcademyLearnings(String xMasheryHandshake) {		
		LOG.info("Entering the getAllSuccessAcademyLearnings");
		long requestStartTime = System.currentTimeMillis();
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		LOG.info("Received user details in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();
		List<SuccessAcademyLearningEntity> entities = successAcademyDAO.findAll();
		LOG.info("Fetched all learning in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();
		Set<String> userBookmarks = null;
		if(null != userDetails){
			userBookmarks = learningDAO.getBookmarks(userDetails.getCecId());
		}
		LOG.info("Fetched user bookmarks in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();
		List<SuccessAcademyLearning> learnings = new ArrayList<>();
		for(SuccessAcademyLearningEntity entity : entities){
			SuccessAcademyLearning learning = SuccessAcademyMapper.getLearningsFromEntity(entity);
			if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
					&& userBookmarks.contains(entity.getRowId())){
				learning.setIsBookMarked(true);
			}
			learnings.add(learning);
		}
		LOG.info("Sending reponse in {} ", (System.currentTimeMillis() - requestStartTime));
		return learnings;
	}

	@Override
	public List<Community> getAllCommunities() {
		return communityDAO.getCommunities();
	}

	@Override
	public SuccessTalkResponseSchema getAllSuccessTalks() {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getAllSuccessTalks());
		return successTalkResponseSchema;
	}

	@Override
	public SuccessTalkResponseSchema getUserSuccessTalks(String xMasheryHandshake) {
		long startTime = System.currentTimeMillis();
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		long timeElapsed = System.currentTimeMillis() - startTime;
		LOG.info("Partner Profile Service Time Elapsed: " + timeElapsed);
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getUserSuccessTalks(userDetails.getCecId()));
		return successTalkResponseSchema;
	}

	@Override
	public SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, Long eventStartDate,
			String xMasheryHandshake, String puid) throws IOException {
		UserDetailsWithCompanyList userDetails = partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake);

		List<Company> companies = userDetails.getCompanyList();
		Optional<Company> matchingObject = companies.stream()
				.filter(c -> (c.getPuid().equals(puid) && c.isDemoAccount())).findFirst();
		Company company = matchingObject.isPresent() ? matchingObject.get() : null;
		if (company != null)
			throw new NotAllowedException("Not Allowed for DemoAccount");

		// form a schema object for the input (set transaction type to Canceled)
		SuccesstalkUserRegEsSchema cancelledRegistration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getCiscoUserProfileSchema().getUserId(),
				SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED);

		try {
			// find and mark registration as Canceled in the Smartsheet
			// commenting out for now till workflow is finalized
			// smartsheetDAO.cancelUserSuccessTalkRegistration(cancelledRegistration);
			return successTalkDAO.saveSuccessTalkRegistration(cancelledRegistration);
		} catch (Exception se) {
			// log error if smartsheet throws exception and mark it Cancel_Failed for the ES
			// index
			LOG.error("Error while cancelling Success Talk Registration in Smartsheet", se);
			cancelledRegistration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELFAILED);
			successTalkDAO.saveSuccessTalkRegistration(cancelledRegistration);
			throw new GenericException("Error while cancelling Success Talk Registration: " + se.getMessage(), se);
		}

	}

	@Override
	public SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, Long eventStartDate,
			String xMasheryHandshake, String puid) throws Exception {
		UserDetailsWithCompanyList userDetails = partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake);

		List<Company> companies = userDetails.getCompanyList();
		Optional<Company> matchingObject = companies.stream()
				.filter(c -> (c.getPuid().equals(puid) && c.isDemoAccount())).findFirst();
		Company company = matchingObject.isPresent() ? matchingObject.get() : null;
		if (company != null)
			throw new NotAllowedException("Not Allowed for DemoAccount");

		// form a schema object for the input (set transaction type to Pending)
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getCiscoUserProfileSchema().getUserId(),
				SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);

		// validate the registration details
		registration = this.fetchSuccessTalkRegistrationDetails(registration, userDetails.getCiscoUserProfileSchema());
		return successTalkDAO.saveSuccessTalkRegistration(registration);
	}

	@Override
	public SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration, UserProfile userDetails) throws NotFoundException, NotAllowedException {
		SuccessTalk successTalk = null;

		try {
			successTalk = successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate());
		} catch (IOException ioe) {
			LOG.error("Could not fetch SuccessTalk details", ioe);
			throw new GenericException("Could not verify if SuccessTalk is valid", ioe);
		}

		if (successTalk != null) {
			Optional<SuccessTalkSession> optionalSession = successTalk.getSessions().stream().findFirst();
			if (optionalSession.isPresent()) {
				registration.setTitle(successTalk.getTitle());
				SuccessTalkSession successTalkSession = optionalSession.get();
				registration.setEventStartDate(successTalkSession.getSessionStartDate());
				registration.setCcoid(userDetails.getUserId());
				registration.setRegistrationDate(new Date().getTime());
			}
		} else {
			throw new NotFoundException("Invalid SuccessTalk Details: " + registration.getTitle());
		}

		return registration;
	}

	@Override
	public BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema,
			String xMasheryHandshake) {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();

		BeanUtils.copyProperties(bookmarkRequestSchema, bookmarkResponseSchema);

		bookmarkResponseSchema.setCcoid(userDetails.getCecId());

		bookmarkResponseSchema = bookmarkDAO.createOrUpdate(bookmarkResponseSchema);

		return bookmarkResponseSchema;
	}
	
	@Override
	public CountResponseSchema getIndexCounts() {
		LOG.info("Entering the getIndexCounts");
		long requestStartTime = System.currentTimeMillis();		
		List<CountSchema> indexCounts = new ArrayList<>();
		CountResponseSchema countResponse = new CountResponseSchema();
		try {

			CountSchema communityCount= getCommunityCount();
			LOG.info("Received Community count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(communityCount);

			requestStartTime = System.currentTimeMillis();	
			CountSchema successTalkCount = getSuccessTalkCount();
			LOG.info("Received Success talks count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(successTalkCount);
			
			CountSchema successAcamedyCount = new CountSchema();
			successAcamedyCount.setLabel("CX Learning");	
			requestStartTime = System.currentTimeMillis();	
			successAcamedyCount.setCount(successAcademyDAO.count());
			LOG.info("Received Success Academy count in {} ", (System.currentTimeMillis() - requestStartTime));

			indexCounts.add(successAcamedyCount);

			countResponse.setLearningStatus(indexCounts);

		} catch (Exception e) {
			LOG.error("Could not fetch index counts", e);
			throw new GenericException("Could not fetch index counts", e);

		}

		return countResponse;
	}
	
	@Override
	public CountSchema getCommunityCount() {

		CountSchema communityCount = new CountSchema();
		communityCount.setLabel("Cisco Community");
		// Community Count is currently hardcoded to 1
		communityCount.setCount(1L);
		return communityCount;
	}
	
	@Override
	public CountSchema getSuccessTalkCount() {

		CountSchema successTalkCount = new CountSchema();
		// Success Talks count - Adding filter to exculde cancelled SuccessTalks
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder includeCancelledQuery = QueryBuilders.matchPhraseQuery("status.keyword",
				SuccessTalk.SuccessTalkStatusEnum.CANCELLED);
		boolQuery.mustNot(includeCancelledQuery);
		sourceBuilder.query(boolQuery);

		successTalkCount.setLabel("Success Talks");
		try {
			successTalkCount
					.setCount(elasticSearchDAO.countRecordsWithFilter(config.getSuccessTalkIndex(), sourceBuilder));
		} catch (IOException e) {
			LOG.error("Could not fetch index counts for Success Talks", e);
			throw new GenericException("Could not fetch index counts for Success Talks", e);
		}
		return successTalkCount;
	}

	@Override
	public List<SuccessAcademyFilter> getSuccessAcademyFilters() {
		LOG.info("Entering the getSuccessAcademyFilters");
		long requestStartTime = System.currentTimeMillis();	
		Map<String, List<String>> mapData = new HashMap<String, List<String>>();
		List<SuccessAcademyFilter> filters = new ArrayList<SuccessAcademyFilter>();
		List<Object[]> filterData = successAcademyDAO.getLearningFilters();
		LOG.info("Received filtered data in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();	
		List<PartnerPortalLookUpEntity> tabLocationEntities = partnerPortalLookupDAO.getTabLocations();	
		LOG.info("Received lookup entity data in {} ", (System.currentTimeMillis() - requestStartTime));
		requestStartTime = System.currentTimeMillis();
		Map<String, String> lookupValues = getLookUpMapFromEntity(tabLocationEntities);
		for(Object[] objectData : filterData){
			List<String> subFilters = new ArrayList<String>();
			if(null != mapData.get(objectData[0])){	
				subFilters = mapData.get(objectData[0]);
			}
			subFilters.add((objectData[1]).toString());
			mapData.put((objectData[0]).toString(), subFilters);
		}
		for(String key : mapData.keySet()){			
			SuccessAcademyFilter filter = new SuccessAcademyFilter();					
			filter.setName(key);
			filter.setFilters(mapData.get(key));					
			filter.setTabLocationOnUI(lookupValues.get(key.toLowerCase().replaceAll(" ", "")));
			filters.add(filter);
		}
		LOG.info("Sending final response in {} ", (System.currentTimeMillis() - requestStartTime));
		return filters;
	}

	@Override
	public BookmarkResponseSchema bookmarkLearningForUser(
			BookmarkRequestSchema bookmarkRequestSchema,
			String xMasheryHandshake) {
		LOG.info("Entering the getSuccessAcademyFilters");
		long requestStartTime = System.currentTimeMillis();	
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		LOG.info("Fetched user data in {} ", (System.currentTimeMillis() - requestStartTime));
		if(null == userDetails){
			throw new BadRequestException("Error from Entitlement System");
		}else{
			BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();
			bookmarkResponseSchema.setCcoid(userDetails.getCecId());
			bookmarkResponseSchema.setLearningid(bookmarkRequestSchema.getLearningid());
			bookmarkResponseSchema.setBookmark(bookmarkRequestSchema.isBookmark());
			requestStartTime = System.currentTimeMillis();
			learningDAO.createOrUpdate(bookmarkResponseSchema);	
			LOG.info("Updated bookmark in {} ", (System.currentTimeMillis() - requestStartTime));
			return bookmarkResponseSchema;
		}
	}
	
	
	private Map<String,String> getLookUpMapFromEntity(List<PartnerPortalLookUpEntity> entityList){
		HashMap<String, String> lookUpValues = new HashMap<String, String>();
		for(PartnerPortalLookUpEntity entity : entityList){
			String key = entity.getPartnerPortalKey().replaceAll(CXPP_UI_TAB_PREFIX, "");
			lookUpValues.put(key.toLowerCase(), entity.getPartnerPortalKeyValue());
		}		
		return lookUpValues;
	}

	@Override
	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake,String searchToken,String filters,
			String sortBy, String sortOrder) 
	{
			return productDocumentationService.getAllLearningInfo(xMasheryHandshake,searchToken,filters,sortBy, sortOrder);
	}
	
	@Override
	public HashMap<String, Object> getAllLearningFilters(String searchToken,String filters){		
		return productDocumentationService.getAllLearningFilters(searchToken,filters);
	}

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(String filter) {
		List<NewLearningContentEntity> learningContentList = new ArrayList<>();
		Map<String, List<String>> query_map = new LinkedHashMap<>();
		if (!StringUtils.isBlank(filter)) {
			filter = filter.replaceAll("%3B", ";");
			filter = filter.replaceAll("%3A", ":");
			filter = filter.replaceAll("%2C", ",");
			String[] columnFilter = filter.split(";");
			for (int colFilterIndex = 0; colFilterIndex < columnFilter.length; colFilterIndex++) {
				String[] valueFilter = columnFilter[colFilterIndex].split(":");
				String fieldName = valueFilter[0];
				String[] fieldValues = valueFilter[1].split(",");
				query_map.put(fieldName, Arrays.asList(fieldValues));
			}
		}
		learningContentList = learningContentDAO.fetchNewLearningContent(query_map);
		return learningContentList;
	}

	@Override
	public LearningRecordsAndFiltersModel getAllLearningInfoPost(String xMasheryHandshake, String searchToken,
			HashMap<String, Object> filters, String sortBy, String sortOrder) {
		
		return productDocumentationService.getAllLearningInfo(xMasheryHandshake,searchToken,filters,sortBy, sortOrder);
	}

	@Override
	public HashMap<String, Object> getAllLearningFiltersPost(String searchToken, HashMap<String, Object> filters) {
		return productDocumentationService.getAllLearningFilters(searchToken,filters);
	}
}

