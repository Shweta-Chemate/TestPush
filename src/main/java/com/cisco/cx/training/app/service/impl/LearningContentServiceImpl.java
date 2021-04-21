package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.PIW;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.util.SuccessAcademyMapper;

@Service
public class LearningContentServiceImpl implements LearningContentService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private NewLearningContentDAO learningContentDAO;
	
	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;

	@Autowired
	private SuccessAcademyDAO successAcademyDAO;
	
	@Autowired
	private PartnerProfileService partnerProfileService;

	@Autowired
	private LearningStatusRepo learningStatusRepo;

	@Override
	public SuccessTalkResponseSchema fetchSuccesstalks(String ccoid, String puid, String sortField, String sortType,
			String filter, String search) {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			List<NewLearningContentEntity> successTalkEntityList = new ArrayList<NewLearningContentEntity>();
			successTalkEntityList = learningContentDAO.fetchSuccesstalks(sortField, sortType, query_map, search);
			List<SuccessTalk> successtalkList = new ArrayList<>();
			successTalkResponseSchema.setItems(successtalkList);	
			//populate bookmark and registration info
			Set<String> userBookmarks = null;
			if(null != ccoid){
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
			for (NewLearningContentEntity entity : successTalkEntityList) {
				SuccessTalk learningItem = mapLearningEntityToSuccesstalk(entity);
				if (null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.contains(learningItem.getSuccessTalkId())) {
					learningItem.setBookmark(true);
				}
				LearningStatusEntity userRegistration = userRegistrations.stream()
						.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
								.equalsIgnoreCase(learningItem.getSuccessTalkId()))
						.findFirst().orElse(null);
				if (userRegistration != null && userRegistration.getRegStatus() != null) {
					learningItem.setStatus(userRegistration.getRegStatus());
				}
				successtalkList.add(learningItem);
			}
		}
		catch (Exception e) {
			LOG.error("fetchSuccesstalks failed: {} ", e);
			throw new GenericException("There was a problem in fetching Successtalks.");
		}
		return successTalkResponseSchema;
	}

	SuccessTalk mapLearningEntityToSuccesstalk(NewLearningContentEntity learningEntity) {
		SuccessTalk successtalk = new SuccessTalk();
		SuccessTalkSession successtalkSession = new SuccessTalkSession();
		List<SuccessTalkSession> sessionList = new ArrayList<>();
		successtalk.setDocId(learningEntity.getId());
		successtalk.setTitle(learningEntity.getTitle());
		successtalk.setDescription(learningEntity.getDescription());
		successtalk.setStatus(learningEntity.getStatus());
		//Adding Session Details
		successtalkSession.setPresenterName(learningEntity.getPresenterName());
		successtalkSession.setRegion(learningEntity.getRegion());
		successtalkSession.setRegistrationUrl(learningEntity.getRegistrationUrl());
		successtalkSession.setSessionId(learningEntity.getId());
		successtalkSession.setSessionStartDate(learningEntity.getSessionStartDate().getTime());
		successtalkSession.setScheduled(false);
		sessionList.add(successtalkSession);
		successtalk.setSessions(sessionList);
		successtalk.setDuration(learningEntity.getDuration());
		successtalk.setRecordingUrl(learningEntity.getRecordingUrl());
		successtalk.setSuccessTalkId(learningEntity.getId());
		return successtalk;
	}
	
	public List<PIW> fetchPIWs(String ccoid, String puid, String region, String sortField, String sortType, String filter,
			String search) {
		List<NewLearningContentEntity> result = new ArrayList<>();
		List<PIW> piwItems = new ArrayList<>();

		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			result = learningContentDAO.listPIWs(region, sortField, sortType, query_map, search);
			//populate bookmark and registration info
			Set<String> userBookmarks = null;
			if(null != ccoid){
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
			for(NewLearningContentEntity entity : result){
				PIW learningItem =  new PIW(entity);
				if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.contains(learningItem.getPiwId())){
					learningItem.setBookmark(true);
				}
				LearningStatusEntity userRegistration = userRegistrations.stream()
						.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
								.equalsIgnoreCase(learningItem.getPiwId()))
						.findFirst().orElse(null);
				if (userRegistration != null && userRegistration.getRegStatus() != null) {
					learningItem.setStatus(userRegistration.getRegStatus());
				}
				piwItems.add(learningItem);
			}
			

		}catch (Exception e) {
			LOG.error("listByRegion failed: {} ", e);
			throw new GenericException("There was a problem in fetching PIWs by Region.");
		}
		
		return piwItems;
	}
	
	@Override
	public CountResponseSchema getIndexCounts() {
		LOG.info("Entering the getIndexCounts");
		long requestStartTime = System.currentTimeMillis();		
		List<CountSchema> indexCounts = new ArrayList<>();
		CountResponseSchema countResponse = new CountResponseSchema();
		try {

			requestStartTime = System.currentTimeMillis();	
			CountSchema webinarCount = getWebinarCount();
			LOG.info("Received webinar count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(webinarCount);

			CountSchema successAcamedyCount = new CountSchema();
			successAcamedyCount.setLabel("Learning");
			requestStartTime = System.currentTimeMillis();	
			successAcamedyCount.setCount(successAcademyDAO.count());
			LOG.info("Received Success Academy count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(successAcamedyCount);

			requestStartTime = System.currentTimeMillis();	
			CountSchema documentationCount = getDocumentationCount();
			LOG.info("Received documentation count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(documentationCount);

			countResponse.setLearningStatus(indexCounts);

		} catch (Exception e) {
			LOG.error("Could not fetch index counts", e);
			throw new GenericException("Could not fetch index counts", e);

		}

		return countResponse;
	}

	private CountSchema getWebinarCount() {

		CountSchema webinarCount = new CountSchema();
		webinarCount.setLabel("Webinars");
		webinarCount.setCount(new Long(learningContentDAO.getPIWCount()+learningContentDAO.getSuccessTalkCount()));
		return webinarCount;

	}

	private CountSchema getDocumentationCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel("Documentation");
		documentationCount.setCount(new Long(learningContentDAO.getDocumentationCount()));
		return documentationCount;

	}

	private Map<String, String>  filterStringtoMap(String filter){
		Map<String, String> query_map = new LinkedHashMap<String, String>();
		if (!StringUtils.isBlank(filter)) {
			filter = filter.replaceAll("%3B", ";");
			filter = filter.replaceAll("%3A", ":");
			String[] columnFilter = filter.split(";");
			for (int colFilterIndex = 0; colFilterIndex < columnFilter.length; colFilterIndex++) {
				String[] valueFilter = columnFilter[colFilterIndex].split(":");
				String fieldName = valueFilter[0];
				String fieldValue = valueFilter[1];
				query_map.put(fieldName, fieldValue);
			}
		}
		return query_map;
	}

	@Override
	public HashMap<String, HashMap<String,String>> getViewMoreFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts) {
		Map<String, String> query_map = filterStringtoMap(filter);
		return learningContentDAO.getViewMoreFiltersWithCount(query_map, filterCounts);
	}

	@Override
	public LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema,
			String xMasheryHandshake) {
		UserDetailsWithCompanyList userDetails = partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake);
		List<Company> companies = userDetails.getCompanyList();
		Optional<Company> matchingObject = companies.stream()
				.filter(c -> (c.getPuid().equals(puid) && c.isDemoAccount())).findFirst();
		Company company = matchingObject.isPresent() ? matchingObject.get() : null;
		if (company != null)
			throw new NotAllowedException("Not Allowed for DemoAccount");

		try {
			LearningStatusEntity learning_status_existing = learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(learningStatusSchema.getLearningItemId(), userId, puid);
			// record already exists in the table
			if (learning_status_existing != null) {
				if(learningStatusSchema.getRegStatus()!=null){
					learning_status_existing.setRegStatus(learningStatusSchema.getRegStatus().toString());
					learning_status_existing.setRegUpdatedTimestamp(java.time.LocalDateTime.now());
				}
				if(learningStatusSchema.isViewed()){
					learning_status_existing.setViewedTimestamp(java.time.LocalDateTime.now());
				}
				learningStatusRepo.save(learning_status_existing);
				return learning_status_existing;
			}
			// new record
			else {
				LearningStatusEntity learning_status_new = new LearningStatusEntity();
				learning_status_new.setUserId(userId);
				learning_status_new.setPuid(puid);
				learning_status_new.setLearningItemId(learningStatusSchema.getLearningItemId());
				if(learningStatusSchema.getRegStatus()!=null){
					learning_status_new.setRegStatus(learningStatusSchema.getRegStatus().toString());
					learning_status_new.setRegUpdatedTimestamp(java.time.LocalDateTime.now());
				}
				if(learningStatusSchema.isViewed()){
					learning_status_new.setViewedTimestamp(java.time.LocalDateTime.now());
				}
				return learningStatusRepo.save(learning_status_new);
			}

		} catch (Exception e) {
			throw new GenericException("There was a problem in registering user to the PIW");
		}
	}

	@Override
	public List<LearningContentItem> fetchRecentlyViewedContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> learningContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		learningContentList=learningContentDAO.fetchRecentlyViewedContent(puid, ccoid, query_map);
		//populate bookmark info  and registration info
		Set<String> userBookmarks = null;
		if(null != ccoid){
			userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
		}
		List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
		for(NewLearningContentEntity entity : learningContentList){
			LearningContentItem learningItem = new LearningContentItem(entity);
			if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
					&& userBookmarks.contains(learningItem.getId())){
				learningItem.setBookmark(true);
			}
			LearningStatusEntity userRegistration = userRegistrations.stream()
					.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
							.equalsIgnoreCase(learningItem.getId()))
					.findFirst().orElse(null);
			if (userRegistration != null && userRegistration.getRegStatus() != null) {
				learningItem.setStatus(userRegistration.getRegStatus());
			}
			result.add(learningItem);
		}
		return result;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid,String userId, String filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		Map<String, String> query_map = filterStringtoMap(filter);
		return learningContentDAO.getRecentlyViewedFiltersWithCount(puid, userId, query_map, filterCounts);
	}

	@Override
	public List<LearningContentItem> fetchBookMarkedContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> learningFilteredList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		learningFilteredList=learningContentDAO.fetchFilteredContent(puid, ccoid, query_map);
		//populate bookmark info
		Set<String> userBookmarks = null;
		userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
		List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
		for(NewLearningContentEntity entity : learningFilteredList){
			LearningContentItem learningItem = new LearningContentItem(entity);
			if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
					&& userBookmarks.contains(entity.getId())){
				learningItem.setBookmark(true);
			}
			LearningStatusEntity userRegistration = userRegistrations.stream()
					.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
							.equalsIgnoreCase(learningItem.getId()))
					.findFirst().orElse(null);
			if (userRegistration != null && userRegistration.getRegStatus() != null) {
				learningItem.setStatus(userRegistration.getRegStatus());
			}
			result.add(learningItem);
		}
		return result;
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(String puid, String ccoid,
			String filter, HashMap<String, HashMap<String, String>> filterCounts) {
		Map<String, String> query_map = filterStringtoMap(filter);
		List<LearningContentItem> bookmarkedList = new ArrayList<>();
		bookmarkedList = fetchBookMarkedContent(puid, ccoid, filter);
		return learningContentDAO.getBookmarkedFiltersWithCount(query_map, filterCounts, bookmarkedList);
	}

}
