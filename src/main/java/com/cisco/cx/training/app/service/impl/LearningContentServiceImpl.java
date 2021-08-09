package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.cisco.cx.training.app.dao.UserLearningPreferencesDAO;
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

	public static final HashMap<String, String> filterNameMappings=getMappings();

	public static final List<String> defaultFilterOrder= getDefaultFilterOrder();

	public static final List<String> cxInsightsFilterOrder= getCXInsightsFilterOrder();

	public static final List<String> lfcFilterOrder= getLFCFilterOrder();

	private static HashMap<String, String> getMappings() {
		HashMap<String, String> filterGroupMappings=new HashMap<String, String>();
		filterGroupMappings.put(Constants.LANGUAGE, Constants.LANGUAGE_PRM);
		filterGroupMappings.put(Constants.LIVE_EVENTS, Constants.REGION);
		filterGroupMappings.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_PRM);
		filterGroupMappings.put(Constants.ROLE, Constants.ROLE);
		filterGroupMappings.put(Constants.MODEL, Constants.MODEL);
		filterGroupMappings.put(Constants.TECHNOLOGY, Constants.TECHNOLOGY);
		filterGroupMappings.put(Constants.SUCCESS_TRACK, Constants.SUCCESS_TRACK);
		filterGroupMappings.put(Constants.LIFECYCLE, Constants.LIFECYCLE);
		filterGroupMappings.put(Constants.FOR_YOU_FILTER, Constants.FOR_YOU_FILTER);
		return filterGroupMappings;
	}

	private static List<String> getDefaultFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add(Constants.ROLE);
		order.add(Constants.SUCCESS_TRACK);
		order.add(Constants.LIFECYCLE);
		order.add(Constants.TECHNOLOGY);
		order.add(Constants.LIVE_EVENTS);
		order.add(Constants.CONTENT_TYPE);
		order.add(Constants.LANGUAGE);
		return order;
	}

	private static List<String> getCXInsightsFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add(Constants.LIFECYCLE);
		order.add(Constants.ROLE);
		order.add(Constants.TECHNOLOGY);
		order.add(Constants.SUCCESS_TRACK);
		order.add(Constants.LIVE_EVENTS);
		order.add(Constants.FOR_YOU_FILTER);
		order.add(Constants.CONTENT_TYPE);
		order.add(Constants.LANGUAGE);
		return order;
	}

	private static List<String> getLFCFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add("Accelerate"); order.add("Need");
		order.add("Evaluate"); order.add("Select");
		order.add("Align"); order.add("Purchase");
		order.add("Onboard"); order.add("Implement");
		order.add("Use");  order.add("Engage");
		order.add("Adopt"); order.add("Optmize");
		order.add("Renew"); order.add("Recommend");
		order.add("Advocate");
		return order;
	}

	@Autowired
	private NewLearningContentDAO learningContentDAO;
	
	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;
	
	@Autowired
	private PartnerProfileService partnerProfileService;

	@Autowired
	private LearningStatusRepo learningStatusRepo;
	
	@Autowired
	private UserLearningPreferencesDAO ulpDAO;

	@Override
	public SuccessTalkResponseSchema fetchSuccesstalks(String ccoid, String sortField, String sortType,
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
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
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
	
	public List<PIW> fetchPIWs(String ccoid, String region, String sortField, String sortType, String filter,
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
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
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

			requestStartTime = System.currentTimeMillis();	
			CountSchema documentationCount = getDocumentationCount();
			LOG.info("Received documentation count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(documentationCount);
			
			requestStartTime = System.currentTimeMillis();	
			CountSchema successTrackCount = getSuccessTrackCount();
			LOG.info("Received Success Tracks count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(successTrackCount);
			
			requestStartTime = System.currentTimeMillis();	
			CountSchema technologyCount = getTechnologyCount();
			LOG.info("Received technology count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(technologyCount);
			
			requestStartTime = System.currentTimeMillis();	
			CountSchema roleCount = getRolesCount();
			LOG.info("Received roles count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(roleCount);

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
	
	private CountSchema getSuccessTrackCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel("Success Tracks");
		documentationCount.setCount(new Long(learningContentDAO.getSuccessTracksCount()+learningContentDAO.getLifecycleCount()));
		return documentationCount;

	}
	
	private CountSchema getTechnologyCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel("Technology");
		documentationCount.setCount(new Long(learningContentDAO.getTechnologyCount()));
		return documentationCount;

	}
	
	private CountSchema getRolesCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel("Role");
		documentationCount.setCount(new Long(learningContentDAO.getRolesCount()));
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
	public List<LearningContentItem> fetchNewLearningContent(String ccoid, HashMap<String, Object> filtersSelected) {
		List<NewLearningContentEntity> learningContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		//get filters selected for success track filter and other filters separately and populate the selection into different objects
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			learningContentList = learningContentDAO.fetchNewLearningContent(queryMap, stMap);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			for (NewLearningContentEntity entity : learningContentList) {
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
			LOG.error("There was a problem in fetching new learning content", e);
			throw new GenericException("There was a problem in fetching new learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getViewMoreNewFiltersWithCount(HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> viewMoreNewCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			viewMoreNewCounts = learningContentDAO.getViewMoreNewFiltersWithCount(filtersSelected);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching new filter counts", e);
			throw new GenericException("There was a problem in fetching new filter counts");
		}
		result=orderFilters(viewMoreNewCounts, LearningContentServiceImpl.getDefaultFilterOrder());
		return result;
	}

	@Override
	public LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema,
			String xMasheryHandshake) {
		Registration regStatus=learningStatusSchema.getRegStatus();
		UserDetailsWithCompanyList userDetails=null;
		if(regStatus!=null) {
			userDetails = partnerProfileService.fetchUserDetailsWithCompanyList(xMasheryHandshake);
			List<Company> companies = userDetails.getCompanyList();
			Optional<Company> matchingObject = companies.stream()
					.filter(c -> (c.getPuid().equals(puid) && c.isDemoAccount())).findFirst();
			Company company = matchingObject.isPresent() ? matchingObject.get() : null;
			if (company != null)
				throw new NotAllowedException("Not Allowed for DemoAccount");
		}
		try {
			ulpDAO.addLearningsViewedForRole(userDetails,learningStatusSchema);
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
	public List<LearningContentItem> fetchRecentlyViewedContent(String ccoid, HashMap<String, Object> filtersSelected) {
		List<NewLearningContentEntity> learningContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		//get filters selected for success track filter and other filters separately and populate the selection into different objects
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			learningContentList=learningContentDAO.fetchRecentlyViewedContent(ccoid, queryMap, stMap);
			//populate bookmark info  and registration info
			Set<String> userBookmarks = null;
			if(null != ccoid){
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
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
	public Map<String, Object> getRecentlyViewedFiltersWithCount(String userId, HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> recentlyViewedCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			recentlyViewedCounts = learningContentDAO.getRecentlyViewedFiltersWithCount(userId, filtersSelected);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching recently viewed filter counts", e);
			throw new GenericException("There was a problem in fetching recently viewed filter counts");
		}
		result=orderFilters(recentlyViewedCounts, LearningContentServiceImpl.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchBookMarkedContent(String ccoid, HashMap<String, Object> filtersSelected) {
		List<NewLearningContentEntity> learningFilteredList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		//get filters selected for success track filter and other filters separately and populate the selection into different objects
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			learningFilteredList=learningContentDAO.fetchFilteredContent(queryMap, stMap);
			//get bookmarked content
			Map<String,Object> userBookmarks = null;
			userBookmarks = learningBookmarkDAO.getBookmarksWithTime(ccoid);
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
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
	public Map<String, Object> getBookmarkedFiltersWithCount(String ccoid,HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> bookmarkedCounts = new HashMap<>();
		Map<String, Object>  result;
		try
		{
			List<LearningContentItem> bookmarkedList = new ArrayList<>();
			bookmarkedList = fetchBookMarkedContent(ccoid, new HashMap<String, Object>());
			bookmarkedCounts = learningContentDAO.getBookmarkedFiltersWithCount(filtersSelected, bookmarkedList);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching bookmarked filter counts", e);
			throw new GenericException("There was a problem in fetching bookmarked filter counts");
		}
		result=orderFilters(bookmarkedCounts, LearningContentServiceImpl.getDefaultFilterOrder());
		return result;
	}
	
	@Override
	public List<LearningContentItem> fetchUpcomingContent(String ccoid, HashMap<String, Object> filtersSelected) {
		List<NewLearningContentEntity> upcomingContentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			upcomingContentList = learningContentDAO.fetchUpcomingContent(queryMap, stMap);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
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
	public Map<String, Object> getUpcomingFiltersWithCount(HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> upcomingContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			upcomingContentCounts = learningContentDAO.getUpcomingFiltersWithCount(filtersSelected);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching upcoming filter counts", e);
			throw new GenericException("There was a problem in fetching upcoming filter counts");
		}
		result=orderFilters(upcomingContentCounts, LearningContentServiceImpl.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchPopularAcrossPartnersContent(String ccoid, HashMap<String, Object> filtersSelected) {
		List<NewLearningContentEntity> contentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			contentList = learningContentDAO.fetchPopularAcrossPartnersContent(queryMap, stMap);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			for (NewLearningContentEntity entity : contentList) {
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
			LOG.error("There was a problem in fetching popular across partners learning content", e);
			throw new GenericException("There was a problem in fetching popular across partners learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getPopularAcrossPartnersFiltersWithCount(HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> popularContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			popularContentCounts = learningContentDAO.getPopularAcrossPartnersFiltersWithCount(filtersSelected);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching popular across partners filter counts", e);
			throw new GenericException("There was a problem in fetching popular across partners filter counts");
		}
		result=orderFilters(popularContentCounts, LearningContentServiceImpl.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchCXInsightsContent(String ccoid, HashMap<String, Object> filtersSelected, String searchToken,
			String sortField, String sortType) {
		List<NewLearningContentEntity> contentList = new ArrayList<>();
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap=new HashMap<>();
		Object stMap=null;
		if(filtersSelected!=null) {
			filtersSelected.keySet().forEach(filterGroup->{
				if(!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>)filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap=filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try
		{
			contentList = learningContentDAO.fetchCXInsightsContent(ccoid, queryMap, stMap, searchToken, sortField, sortType);
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
	public Map<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, HashMap<String, Object> filtersSelected) {
		HashMap<String, Object> cxInsightsContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			cxInsightsContentCounts = learningContentDAO.getCXInsightsFiltersWithCount(userId, searchToken, filtersSelected);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching cx insights filters", e);
			throw new GenericException("There was a problem in fetching cx insights filters");
		}
		result=orderFilters(cxInsightsContentCounts, LearningContentServiceImpl.getCXInsightsFilterOrder());
		return result;
	}
	
	@Override
	public LearningMap getLearningMap(String id, String title) {
		if(id == null && title ==null)
		{
			LOG.error("Both ID and Title cannot be null");
			throw new GenericException("There was a problem in fetching learning map: both ID and Title cannot be null.");
		}
		LearningMap learningMap = new LearningMap();
		try
		{
			learningMap = learningContentDAO.getLearningMap(id,title);
		}catch (Exception e) {
			LOG.error("There was a problem fetching learning map", e);
			throw new GenericException("There was a problem in fetching learning map");
		}
		return learningMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> orderFilters(HashMap<String, Object> filters, List<String> order) {
		LinkedHashMap<String, Object> result=new LinkedHashMap<>();
		for(String filterGroup : order) {
			if(filters.containsKey(filterGroup)) {
				if(filterGroup.equals(Constants.LIFECYCLE))
				{
					LinkedHashMap<String, Object> lfcFilterNew=new LinkedHashMap<>();
					Map<String, String> lfcOld = (Map<String, String>) filters.get(filterGroup);
					List<String> lfcFilterOrder = LearningContentServiceImpl.getLFCFilterOrder();
					for(String filter : lfcFilterOrder) {
						if(lfcOld.containsKey(filter))
							lfcFilterNew.put(filter, lfcOld.get(filter));
					}
					filters.put(filterGroup, lfcFilterNew);
				}
				result.put(filterGroup, filters.get(filterGroup));
			}
		}
		return result;
	}

}
