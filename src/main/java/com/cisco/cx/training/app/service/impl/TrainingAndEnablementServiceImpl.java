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
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.SuccessAcademyModel;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserDetails;
import com.smartsheet.api.SmartsheetException;

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
	private SmartsheetDAO smartsheetDAO;

	@Autowired
	private BookmarkDAO bookmarkDAO;
	
	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private PartnerProfileService partnerProfileService;

	@Override
	public List<SuccessAcademyModel> getAllSuccessAcademy() {
		return successAcademyDAO.getSuccessAcademy();
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
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getEmail(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING);
		try {
			// validate the registration details
			registration = this.fetchSuccessTalkRegistrationDetails(registration, userDetails);

			if (smartsheetDAO.checkRegistrationExists(registration)) {
				// No Operation as Success Talk is registered already
				LOG.info("No Operation as Success Talk is registered already");
			} else {
				// save a new row in the smartsheet for this registration
				//commenting out for now till workflow is finalized
				//smartsheetDAO.saveSuccessTalkRegistration(registration);
				LOG.info("Success Talk is not registered");
			}
			return successTalkDAO.saveSuccessTalkRegistration(registration);
		} catch (SmartsheetException se) {
			// log error if smartsheet throws exception and mark it Register_Failed for the ES index
			LOG.error("Error while saving SuccessTalk Registration in Smartsheet", se);
			registration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERFAILED);
			successTalkDAO.saveSuccessTalkRegistration(registration);
			throw new GenericException("Error while saving SuccessTalk Registration: " + se.getMessage(), se);
		}
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
			
			CountSchema successAcamedyCount = getSuccessAcademyCount();
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
		communityCount.setLabel("Cisco Communities");
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
	public CountSchema getSuccessAcademyCount() {

		SearchSourceBuilder successAcademySourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder successAcademyBoolQuery = new BoolQueryBuilder();
		QueryBuilder includeMonetizeQuery = QueryBuilders.matchPhraseQuery("parentFilter.keyword", "Monetize");
		QueryBuilder includeOperateQuery = QueryBuilders.matchPhraseQuery("parentFilter.keyword", "Operate");
		QueryBuilder includeOrganizeQuery = QueryBuilders.matchPhraseQuery("parentFilter.keyword", "Organize");
		successAcademyBoolQuery.mustNot(includeMonetizeQuery).mustNot(includeOperateQuery)
				.mustNot(includeOrganizeQuery);
		successAcademySourceBuilder.query(successAcademyBoolQuery);

		SearchSourceBuilder partnerModelSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder partnerModelBoolQuery = new BoolQueryBuilder();
		partnerModelBoolQuery.should(includeMonetizeQuery).should(includeOperateQuery).should(includeOrganizeQuery);
		partnerModelSourceBuilder.query(partnerModelBoolQuery);

		CountSchema successAcademyCount = new CountSchema();
		successAcademyCount.setLabel("Success Academy");
		try {
			ElasticSearchResults<SuccessAcademyLearning> successAcademyResults = elasticSearchDAO
					.query(config.getSuccessAcademyIndex(), successAcademySourceBuilder, SuccessAcademyLearning.class);
			ElasticSearchResults<SuccessAcademyLearning> partnerModelResults = elasticSearchDAO
					.query(config.getSuccessAcademyIndex(), partnerModelSourceBuilder, SuccessAcademyLearning.class);
			if (successAcademyResults != null && partnerModelResults != null) {
				Integer learningCount = successAcademyResults.getDocuments().stream()
						.map(successAcademyLearning -> successAcademyLearning.getLearning().size())
						.collect(Collectors.summingInt(Integer::intValue));
				Integer modelCount = partnerModelResults.getDocuments().stream()
						.map(partnerModel -> partnerModel.getLearning().size())
						.collect(Collectors.summingInt(Integer::intValue));
				successAcademyCount.setCount(learningCount.longValue() + modelCount.longValue());
			}
		} catch (IOException e) {
			LOG.error("Could not fetch index counts for Success Academy", e);
			throw new GenericException("Could not fetch index counts for Success Academy", e);
		}
		return successAcademyCount;
	}
}