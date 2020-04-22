package com.cisco.cx.training.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessAcademyModel;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserDetails;
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

	public List<SuccessAcademyLearning> getAllSuccessAcademyLearningsFromDB() {		
		LOG.info("DB UserName", config.getDbUsername());
		LOG.info("Db Pwd", config.getDbPwd());
		List<SuccessAcademyLearningEntity> entities = successAcademyDAO.findAll();
		List<SuccessAcademyLearning> learnings = new ArrayList<>();
		for(SuccessAcademyLearningEntity entity : entities){
			SuccessAcademyLearning learning = SuccessAcademyMapper.getLearningsFromEntity(entity);
			learnings.add(learning);
		}
		return learnings;		
	}
	
	@Override
	public List<SuccessAcademyLearning> getAllSuccessAcademyLearnings() {		
		List<SuccessAcademyLearning> learnings = new ArrayList<>();
		SuccessAcademyLearning learning = new SuccessAcademyLearning();
		learning.setAssetFacet("Renewals Manager");
		learning.setAssetGroup("Test 1");
		learning.setAssetModel("Role");
		learning.setDescription("This is test description");
		learning.setIsBookMarked(false);
		learning.setLink("https://salesconnect.cisco.com/open.html?l=SC_LMS_592");
		learning.setPostDate("14/11/19");
		learning.setSupportedFormats("PPT/PDF");
		learning.setTitle("Understanding Account Team & Working Models");
		learnings.add(learning);
		
		learning = new SuccessAcademyLearning();
		learning.setAssetFacet("Customer Success Manager");
		learning.setAssetGroup("Foundational Training");
		learning.setAssetModel("Role");
		learning.setDescription("This is test description");
		learning.setIsBookMarked(true);
		learning.setLink("https://salesconnect.cisco.com/open.html?l=SC_LMS_592");
		learning.setPostDate("14/11/19");
		learning.setSupportedFormats("COLT,PPT/PDF,VOD");
		learning.setTitle("1. Customer Experience and the Customer Lifecycle");
		learnings.add(learning);
		
		
		learning = new SuccessAcademyLearning();
		learning.setAssetFacet("Customer Success Manager");
		learning.setAssetGroup("Advanced Training");
		learning.setAssetModel("Role");
		learning.setDescription("This is test description");
		learning.setIsBookMarked(false);
		learning.setLink("https://salesconnect.cisco.com/open.html?l=SC_LMS_592");
		learning.setPostDate("14/11/19");
		learning.setSupportedFormats("Email");
		learning.setTitle("1. Customer's Financial Picture and Cisco's Value Proposition");
		learnings.add(learning);
		
		
		learning = new SuccessAcademyLearning();
		learning.setAssetFacet("Enterprise Networking");
		learning.setAssetGroup("Test 1");
		learning.setAssetModel("Technology");
		learning.setDescription("This is test description");
		learning.setIsBookMarked(true);
		learning.setLink("https://salesconnect.cisco.com/open.html?c=56398fcb-19f5-426a-9f58-9fc7acda5ed0");
		learning.setPostDate("01/05/19");
		learning.setSupportedFormats("VOD");
		learning.setTitle("Protocols and Standards");
		learnings.add(learning);
		
		
		learning = new SuccessAcademyLearning();
		learning.setAssetFacet("Operate");
		learning.setAssetGroup("Renewals, Roles & Blueprints");
		learning.setAssetModel("Model");
		learning.setDescription("This is test description");
		learning.setIsBookMarked(false);
		learning.setLink("https://salesconnect.cisco.com/open.html?l=SC_LMS_592");
		learning.setPostDate("14/11/19");
		learning.setSupportedFormats("PPT/PDF");
		learning.setTitle("Lifecycle Partner Model - Renewal and Roles Blueprint");
		learnings.add(learning);
		
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
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getUserSuccessTalks(userDetails.getEmail()));
		return successTalkResponseSchema;
	}

	@Override
	public SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, Long eventStartDate,
			String xMasheryHandshake) throws IOException {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		// form a schema object for the input (set transaction type to Canceled)
		SuccesstalkUserRegEsSchema cancelledRegistration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getEmail(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED);
		try {
			// find and mark registration as Canceled in the Smartsheet
			//commenting out for now till workflow is finalized
			//smartsheetDAO.cancelUserSuccessTalkRegistration(cancelledRegistration);
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
	public SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, Long eventStartDate, String xMasheryHandshake) throws Exception {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		// form a schema object for the input (set transaction type to Pending)
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getEmail(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);

		// validate the registration details
		registration = this.fetchSuccessTalkRegistrationDetails(registration, userDetails);
		return successTalkDAO.saveSuccessTalkRegistration(registration);
	}

	@Override
	public SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration, UserDetails userDetails) throws NotFoundException, NotAllowedException {
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
				registration.setEmail(userDetails.getEmail());
				registration.setFirstName(userDetails.getFirstName());
				registration.setLastName(userDetails.getLastName());
				registration.setUserTitle(userDetails.getTitle());
				registration.setPhone(userDetails.getPhone());
				registration.setCompany(userDetails.getCompany());
				registration.setCountry(userDetails.getCountry());
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

		bookmarkResponseSchema.setEmail(userDetails.getEmail());

		bookmarkResponseSchema = bookmarkDAO.createOrUpdate(bookmarkResponseSchema);

		return bookmarkResponseSchema;
	}
	
	@Override
	public CountResponseSchema getIndexCounts() {
		List<CountSchema> indexCounts = new ArrayList<>();
		CountResponseSchema countResponse = new CountResponseSchema();
		try {

			CountSchema communityCount= getCommunityCount();
			indexCounts.add(communityCount);

			CountSchema successTalkCount = getSuccessTalkCount();
			indexCounts.add(successTalkCount);
			
			CountSchema successAcamedyCount = new CountSchema();
			successAcamedyCount.setLabel("My Learning");
			successAcamedyCount.setCount(4l);
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
		List<SuccessAcademyFilter> filters = new ArrayList<SuccessAcademyFilter>();
		SuccessAcademyFilter 

		filter = new SuccessAcademyFilter();
		filter.setName("Model");
		List<String> subFilters = new ArrayList<String>();
		subFilters.add("Monetise");
		subFilters.add("Operate");
		subFilters.add("Organize");
		filter.setFilters(subFilters);
		filter.setTabLocationOnUI("1");
		filters.add(filter);
		
		filter = new SuccessAcademyFilter();
		filter.setName("Role");
		subFilters = new ArrayList<String>();
		subFilters.add("Customer Success Manager");
		subFilters.add("Renewals Manager");
		filter.setFilters(subFilters);
		filter.setTabLocationOnUI("2");
		filters.add(filter);
		
		filter = new SuccessAcademyFilter();
		filter.setName("Product");
		subFilters = new ArrayList<String>();
		subFilters.add("IBN");
		subFilters.add("BCS");
		filter.setFilters(subFilters);
		filter.setTabLocationOnUI("3");
		filters.add(filter);
		
		filter = new SuccessAcademyFilter();
		filter.setName("Technology");
		subFilters = new ArrayList<String>();
		subFilters.add("Enterprise Networking");
		subFilters.add("LAN");
		subFilters.add("Mobility");	
		subFilters.add("WAN");
		subFilters.add("Security");
		subFilters.add("Data Center");	
		subFilters.add("Collaboration");
		subFilters.add("IOT Overview");
		subFilters.add("Cloud");	
		filter.setFilters(subFilters);
		filter.setTabLocationOnUI("4");
		filters.add(filter);	
		
		return filters;
	}
}