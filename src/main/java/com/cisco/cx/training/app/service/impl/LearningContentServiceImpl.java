package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
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
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.LearningStatusSchema.Registration;
import com.cisco.cx.training.models.PIW;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;

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
				learningItem.setBookmark(false);
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
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp());
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
				learningItem.setBookmark(false);
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
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp());
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
			successAcamedyCount.setLabel("CX Collection");
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
			int count=0;
			for (int colFilterIndex = 0; colFilterIndex < columnFilter.length; colFilterIndex++) {
				String[] valueFilter = columnFilter[colFilterIndex].split(":");
				String fieldName = valueFilter[0];
				String fieldValue = valueFilter[1];
				// for differentating between OR and AND condition for successacademy filters
				if(fieldName.equals("assetFacet"))
					fieldName+=count++;
				query_map.put(fieldName, fieldValue);
			}
		}
		return query_map;
	}

	@Override
	public Map<String, Map<String,String>> getViewMoreNewFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts) {
		HashMap<String, HashMap<String,String>> viewMoreCounts = new HashMap<>();
		Map<String, Map<String,String>> result;
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			viewMoreCounts = learningContentDAO.getViewMoreNewFiltersWithCount(query_map, filterCounts);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching new filter counts", e);
			throw new GenericException("There was a problem in fetching new filter counts");
		}
		result=orderFilters(viewMoreCounts);
		return result;
	}

	@Override
	public LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema,
			String xMasheryHandshake) {
		Registration regStatus=learningStatusSchema.getRegStatus();
		if(regStatus!=null) {
			UserDetailsWithCompanyList userDetails = partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake);
			List<Company> companies = userDetails.getCompanyList();
			Optional<Company> matchingObject = companies.stream()
					.filter(c -> (c.getPuid().equals(puid) && c.isDemoAccount())).findFirst();
			Company company = matchingObject.isPresent() ? matchingObject.get() : null;
			if (company != null)
				throw new NotAllowedException("Not Allowed for DemoAccount");
		}
		try {
			LearningStatusEntity learning_status_existing = learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(learningStatusSchema.getLearningItemId(), userId, puid);
			// record already exists in the table
			if (learning_status_existing != null) {
				if(regStatus!=null){
					learning_status_existing.setRegStatus(regStatus.toString());
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
				if(regStatus!=null){
					learning_status_new.setRegStatus(regStatus.toString());
					learning_status_new.setRegUpdatedTimestamp(java.time.LocalDateTime.now());
				}
				if(learningStatusSchema.isViewed()){
					learning_status_new.setViewedTimestamp(java.time.LocalDateTime.now());
				}
				return learningStatusRepo.save(learning_status_new);
			}

		} catch (Exception e) {
			LOG.error("There was a problem in registering user to the PIW", e);
			throw new GenericException("There was a problem in registering user to the PIW");
		}
	}

	@Override
	public List<LearningContentItem> fetchRecentlyViewedContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> learningContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		try
		{
			learningContentList=learningContentDAO.fetchRecentlyViewedContent(ccoid, query_map);
			//populate bookmark info  and registration info
			Set<String> userBookmarks = null;
			if(null != ccoid){
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
			for(NewLearningContentEntity entity : learningContentList){
				LearningContentItem learningItem = new LearningContentItem(entity);
				learningItem.setBookmark(false);
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
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp());
				}
				result.add(learningItem);
			}			
		}catch (Exception e) {
			LOG.error("There was a problem in fetching recently viewed learning content", e);
			throw new GenericException("There was a problem in fetching recently viewed learning content");
		}
		return result;
	}

	@Override
	public Map<String, Map<String,String>> getRecentlyViewedFiltersWithCount(String puid,String userId, String filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		HashMap<String, HashMap<String,String>> recentlyViewedCounts = new HashMap<>();
		Map<String, Map<String,String>> result;
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			recentlyViewedCounts = learningContentDAO.getRecentlyViewedFiltersWithCount(puid, userId, query_map, filterCounts);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching recently viewed filter counts", e);
			throw new GenericException("There was a problem in fetching recently viewed filter counts");
		}
		result=orderFilters(recentlyViewedCounts);
		return result;
	}

	@Override
	public List<LearningContentItem> fetchBookMarkedContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> learningFilteredList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		try
		{
			learningFilteredList=learningContentDAO.fetchFilteredContent(puid, ccoid, query_map);
			//get bookmarked content
			Map<String,Object> userBookmarks = null;
			userBookmarks = learningBookmarkDAO.getBookmarksWithTime(ccoid);
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
			for(NewLearningContentEntity entity : learningFilteredList){
				LearningContentItem learningItem = new LearningContentItem(entity);
				if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.keySet().contains(entity.getId())){
					learningItem.setBookmark(true);
					learningItem.setBookmarkTimeStamp((long) userBookmarks.get(entity.getId()));
					result.add(learningItem);
					LearningStatusEntity userRegistration = userRegistrations.stream()
							.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
									.equalsIgnoreCase(learningItem.getId()))
							.findFirst().orElse(null);
					if (userRegistration != null && userRegistration.getRegStatus() != null) {
						learningItem.setStatus(userRegistration.getRegStatus());
						learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp());
					}
				}
			}			
		}catch (Exception e) {
			LOG.error("There was a problem in fetching bookmarked learning content", e);
			throw new GenericException("There was a problem in fetching bookmarked learning content");
		}
		return result.stream()
				  .sorted(Comparator.comparing(LearningContentItem::getBookmarkTimeStamp).reversed())
				  .collect(Collectors.toList());
	}
	
	@Override
	public Map<String, Map<String,String>> getBookmarkedFiltersWithCount(String puid, String ccoid,
			String filter, HashMap<String, HashMap<String, String>> filterCounts) {
		HashMap<String, HashMap<String,String>> bookmarkedCounts = new HashMap<>();
		Map<String, Map<String,String>> result;
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			List<LearningContentItem> bookmarkedList = new ArrayList<>();
			String empty=new String();
			bookmarkedList = fetchBookMarkedContent(puid, ccoid, empty);
			bookmarkedCounts = learningContentDAO.getBookmarkedFiltersWithCount(query_map, filterCounts, bookmarkedList);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching bookmarked filter counts", e);
			throw new GenericException("There was a problem in fetching bookmarked filter counts");
		}
		result=orderFilters(bookmarkedCounts);
		return result;
	}
	
	@Override
	public List<LearningContentItem> fetchUpcomingContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> upcomingContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		try
		{
			upcomingContentList = learningContentDAO.fetchUpcomingContent(query_map);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserIdAndPuid(ccoid, puid);
			for (NewLearningContentEntity entity : upcomingContentList) {
				LearningContentItem learningItem = new LearningContentItem(entity);
				learningItem.setBookmark(false);
				if (null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.contains(learningItem.getId())) {
					learningItem.setBookmark(true);
				}
				LearningStatusEntity userRegistration = userRegistrations.stream()
						.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
								.equalsIgnoreCase(learningItem.getId()))
						.findFirst().orElse(null);
				if (userRegistration != null && userRegistration.getRegStatus() != null) {
					learningItem.setStatus(userRegistration.getRegStatus());
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp());
				}
				result.add(learningItem);
			}			
		}catch (Exception e) {
			LOG.error("There was a problem in fetching upcoming learning content", e);
			throw new GenericException("There was a problem in fetching upcoming learning content");
		}
		return result;
	}
	
	@Override
	public Map<String, Map<String,String>> getUpcomingFiltersWithCount(String filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		HashMap<String, HashMap<String,String>> upcomingContentCounts = new HashMap<>();
		Map<String, Map<String,String>> result;
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			upcomingContentCounts = learningContentDAO.getUpcomingFiltersWithCount(query_map, filterCounts);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching upcoming filter counts", e);
			throw new GenericException("There was a problem in fetching upcoming filter counts");
		}
		result=orderFilters(upcomingContentCounts);
		return result;
	}

	@Override
	public List<LearningContentItem> fetchSuccessAcademyContent(String puid, String ccoid, String filter) {
		List<NewLearningContentEntity> contentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		if(query_map.containsValue(Constants.CAMPUS_NETWORK)) {
			query_map.replace(query_map.keySet().stream()
            .filter(key -> Constants.CAMPUS_NETWORK.equals(query_map.get(key))).findFirst().get(), Constants.CAMPUS_NETWORK, Constants.CAMPUS);
		}
		try
		{
			contentList = learningContentDAO.fetchSuccessAcademyContent(query_map);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			for (NewLearningContentEntity entity : contentList) {
				LearningContentItem learningItem = new LearningContentItem(entity);
				learningItem.setBookmark(false);
				if (null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.contains(learningItem.getId())) {
					learningItem.setBookmark(true);
				}
				result.add(learningItem);
			}
		}catch (Exception e) {
			LOG.error("There was a problem in fetching successacademy learning content", e);
			throw new GenericException("There was a problem in fetching successacademy learning content");
		}
		return result;
	}
	
	@Override
	public Map<String, Map<String,String>> getSuccessAcademyFiltersWithCount(String filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		HashMap<String, HashMap<String,String>> successAcademyContentCounts = new HashMap<>();
		Map<String, Map<String,String>> result;
		try
		{
			Map<String, String> query_map = filterStringtoMap(filter);
			if(query_map.containsValue(Constants.CAMPUS_NETWORK)) {
				query_map.replace(query_map.keySet().stream()
	            .filter(key -> Constants.CAMPUS_NETWORK.equals(query_map.get(key))).findFirst().get(), Constants.CAMPUS_NETWORK, Constants.CAMPUS);
			}
			successAcademyContentCounts = learningContentDAO.getSuccessAcademyFiltersWithCount(query_map, filterCounts);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching successacademy filters", e);
			throw new GenericException("There was a problem in fetching successacademy filters");
		}
		result=orderFilters(successAcademyContentCounts);
		return result;
	}

	@Override
	public List<LearningContentItem> fetchCXInsightsContent(String ccoid, String filter, String searchToken,
			String sortField, String sortType) {
		List<NewLearningContentEntity> contentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, String> query_map = filterStringtoMap(filter);
		try
		{
			contentList = learningContentDAO.fetchCXInsightsContent(query_map, searchToken, sortField, sortType);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			for (NewLearningContentEntity entity : contentList) {
				LearningContentItem learningItem = new LearningContentItem(entity);
				learningItem.setBookmark(false);
				if (null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.contains(learningItem.getId())) {
					learningItem.setBookmark(true);
				}
				result.add(learningItem);
			}
		}catch (Exception e) {
			LOG.error("There was a problem in fetching CX Insights learning content", e);
			throw new GenericException("There was a problem in fetching CX Insights learning content");
		}
		return result;
	}
	
	@Override
	public LearningMap getLearningMap(String id) {
		LearningMap learningMap = new LearningMap();
		try
		{
			learningMap = learningContentDAO.getLearningMap(id);
		}catch (Exception e) {
			LOG.error("There was a problem fetching learning map", e);
			throw new GenericException("There was a problem in fetching learning map");
		}
		return learningMap;
	}

	private Map<String, Map<String, String>> orderFilters(HashMap<String, HashMap<String, String>> viewMoreCounts) {
		TreeMap<String, Map<String, String>> sorted = new TreeMap<>();
		LinkedHashMap<String, Map<String, String>> result=new LinkedHashMap<>();
		sorted.putAll(viewMoreCounts);
		for(String filterGroup:sorted.keySet()) {
			TreeMap<String, String> filters=new TreeMap<>();
			filters.putAll(sorted.get(filterGroup));
			sorted.put(filterGroup, filters);
		}
		result.putAll(sorted);
		if(result.containsKey(Constants.LIVE_EVENTS)) {
			result.remove(Constants.LIVE_EVENTS); result.put(Constants.LIVE_EVENTS, sorted.get(Constants.LIVE_EVENTS));
		}
		if(result.containsKey(Constants.CONTENT_TYPE)) {
			result.remove(Constants.CONTENT_TYPE); result.put(Constants.CONTENT_TYPE, sorted.get(Constants.CONTENT_TYPE));
		}
		if(result.containsKey(Constants.LANGUAGE)) {
			result.remove(Constants.LANGUAGE); result.put(Constants.LANGUAGE, sorted.get(Constants.LANGUAGE));
		}
		return result;
	}

}
