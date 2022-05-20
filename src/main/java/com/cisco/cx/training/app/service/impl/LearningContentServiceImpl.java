package com.cisco.cx.training.app.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.app.service.SplitClientService;
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
import com.cisco.cx.training.util.LearningContentUtil;

@SuppressWarnings({"squid:S2221","squid:S5361","squid:S134","squid:S1200","squid:S00104","java:S3776","java:S4288"})
@Service
public class LearningContentServiceImpl implements LearningContentService {

	private static final Logger LOG = LoggerFactory.getLogger(LearningContentServiceImpl.class);

	static final HashMap<String, String> filterNameMappings= LearningContentUtil.getMappings();

	static final List<String> defaultFilterOrder= LearningContentUtil.getDefaultFilterOrder();

	static final List<String> cxInsightsFilterOrder= LearningContentUtil.getCXInsightsFilterOrder();

	static final List<String> lfcFilterOrder= LearningContentUtil.getLFCFilterOrder();
	
	private static final String DEMO_NA_MSG = "Not Allowed for DemoAccount";
	private static final String TECHNOLOGY_LABEL = "Technology";
	private static final String ST_LABEL = "Success Tracks";
	private static final String ROLE_LABEL = "Role";
	private static final String DOC_LABEL = "Documentation";
	private static final String SUCCESS_TIPS_LABEL = "Success Tips";
	private static final String WEBINARS_LABEL = "Webinars";
	private static final String CISCO_PLUS_LABEL = "Cisco+";

	@Autowired
	private NewLearningContentDAO learningContentDAO;
	
	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;
	
	@Autowired
	private PartnerProfileService partnerProfileService;

	@Autowired
	private LearningStatusRepo learningStatusRepo;
	
	@Autowired
	ProductDocumentationService productDocumentationService;
	
	@Autowired
	private SplitClientService splitService;

	@Override
	public SuccessTalkResponseSchema fetchSuccesstalks(String ccoid, String sortField, String sortType,
			String filter, String search) {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		try
		{
			Map<String, String> query_map = LearningContentUtil.filterStringtoMap(filter);			
			List<NewLearningContentEntity> successTalkEntityList = learningContentDAO.fetchSuccesstalks(sortField, sortType, query_map, search);
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
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp()!=null?userRegistration.getRegUpdatedTimestamp().toInstant().toString():null);
				}
				successtalkList.add(learningItem);
			}
		}
		catch (Exception e) {
			LOG.error("fetchSuccesstalks failed: ", e);
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
		successtalkSession.setSessionStartDate(learningEntity.getSessionStartDate()!=null?learningEntity.getSessionStartDate().toInstant().toString():null);
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
			Map<String, String> query_map = LearningContentUtil.filterStringtoMap(filter);
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
					learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp()!=null?userRegistration.getRegUpdatedTimestamp().toInstant().toString():null);
				}
				piwItems.add(learningItem);
			}
			

		}catch (Exception e) {
			LOG.error("listByRegion failed: ", e);
			throw new GenericException("There was a problem in fetching PIWs by Region.");
		}
		
		return piwItems;
	}
	
	@Override
	public CountResponseSchema getIndexCounts(boolean hcaasStatus) {
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
			if(splitService.getSplitValue(Constants.SUCCESS_TIPS_SPLIT_KEY)) {
				requestStartTime = System.currentTimeMillis();	
				CountSchema successTipsCount = getSuccessTipsCount();
				LOG.info("Received success tips count in {} ", (System.currentTimeMillis() - requestStartTime));
				indexCounts.add(successTipsCount);
			}
			
			if(hcaasStatus) {
				requestStartTime = System.currentTimeMillis();	
				CountSchema ciscoPlusCount = getCiscoPlusCount();
				LOG.info("Received Cisco+ count in {} ", (System.currentTimeMillis() - requestStartTime));
				indexCounts.add(ciscoPlusCount);
			}

			countResponse.setLearningStatus(indexCounts);

		} catch (Exception e) {			
			throw new GenericException("Could not fetch index counts", e);
		}

		return countResponse;
	}

	private CountSchema getWebinarCount() {

		CountSchema webinarCount = new CountSchema();
		webinarCount.setLabel(WEBINARS_LABEL);
		webinarCount.setCount(Long.valueOf(Integer.toUnsignedLong(learningContentDAO.getPIWCount())+learningContentDAO.getSuccessTalkCount()));
		return webinarCount;

	}

	private CountSchema getDocumentationCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(DOC_LABEL);
		documentationCount.setCount(Long.valueOf(learningContentDAO.getDocumentationCount()));
		return documentationCount;

	}
	
	private CountSchema getSuccessTipsCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(SUCCESS_TIPS_LABEL);
		documentationCount.setCount(Long.valueOf(learningContentDAO.getSuccessTipsCount()));
		return documentationCount;

	}
	
	private CountSchema getSuccessTrackCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(ST_LABEL);
		documentationCount.setCount(Long.valueOf(Integer.toUnsignedLong(learningContentDAO.getSuccessTracksCount())+learningContentDAO.getLifecycleCount()));
		return documentationCount;

	}
	
	private CountSchema getTechnologyCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(TECHNOLOGY_LABEL);
		documentationCount.setCount(Long.valueOf(learningContentDAO.getTechnologyCount()));
		return documentationCount;

	}
	
	private CountSchema getRolesCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(ROLE_LABEL);
		documentationCount.setCount(Long.valueOf(learningContentDAO.getRolesCount()));
		return documentationCount;

	}

	private CountSchema getCiscoPlusCount() {

		CountSchema documentationCount = new CountSchema();
		documentationCount.setLabel(CISCO_PLUS_LABEL);
		documentationCount.setCount(Long.valueOf(learningContentDAO.getCiscoPlusCount()));
		return documentationCount;

	}

	@Override
	public List<LearningContentItem> fetchNewLearningContent(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		String hcaasStatus = String.valueOf(hcaasStatusBoolean);
		List<LearningContentItem> result ;
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
			List<NewLearningContentEntity> learningContentList = learningContentDAO.fetchNewLearningContent(queryMap, stMap, hcaasStatus);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			result = getItemsWithRegAndBookmarkedCombined(learningContentList, userRegistrations, userBookmarks);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching new learning content", e);
			throw new GenericException("There was a problem in fetching new learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getViewMoreNewFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		HashMap<String, Object> viewMoreNewCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			viewMoreNewCounts = learningContentDAO.getViewMoreNewFiltersWithCount(filtersSelected, hcaasStatus);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching new filter counts", e);
			throw new GenericException("There was a problem in fetching new filter counts");
		}
		result=orderFilters(viewMoreNewCounts, LearningContentUtil.getDefaultFilterOrder());
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
			if (company != null) {
				throw new NotAllowedException(DEMO_NA_MSG);}
		}
		try {
			productDocumentationService.addLearningsViewedForRole(userId,learningStatusSchema.getLearningItemId(),puid);
			LearningStatusEntity learning_status_existing = learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(learningStatusSchema.getLearningItemId(), userId, puid);
			// record already exists in the table
			if (learning_status_existing != null) {
				if(regStatus!=null){
					learning_status_existing.setRegStatus(regStatus.toString());
					learning_status_existing.setRegUpdatedTimestamp(Timestamp.from(Instant.now()));
				}
				if(learningStatusSchema.isViewed()){
					learning_status_existing.setViewedTimestamp(Timestamp.from(Instant.now()));
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
					learning_status_new.setRegUpdatedTimestamp(Timestamp.from(Instant.now()));
				}
				if(learningStatusSchema.isViewed()){
					learning_status_new.setViewedTimestamp(Timestamp.from(Instant.now()));
				}
				return learningStatusRepo.save(learning_status_new);
			}
		} catch (Exception e) {
			LOG.error("There was a problem in registering user to the PIW", e);
			throw new GenericException("There was a problem in registering user to the PIW");
		}
	}

	

	@Override
	public List<LearningContentItem> fetchRecentlyViewedContent(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		List<LearningContentItem> result;
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
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			List<NewLearningContentEntity> learningContentList=learningContentDAO.fetchRecentlyViewedContent(ccoid, queryMap, stMap, hcaasStatus);
			//populate bookmark info  and registration info
			Set<String> userBookmarks = null;
			if(null != ccoid){
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			result = getItemsWithRegAndBookmarkedCombined(learningContentList, userRegistrations, userBookmarks);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching recently viewed learning content", e);
			throw new GenericException("There was a problem in fetching recently viewed learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getRecentlyViewedFiltersWithCount(String userId, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		HashMap<String, Object> recentlyViewedCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			recentlyViewedCounts = learningContentDAO.getRecentlyViewedFiltersWithCount(userId, filtersSelected, hcaasStatus);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching recently viewed filter counts", e);
			throw new GenericException("There was a problem in fetching recently viewed filter counts");
		}
		result=orderFilters(recentlyViewedCounts, LearningContentUtil.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchBookMarkedContent(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatus) {
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
			List<NewLearningContentEntity> learningFilteredList=learningContentDAO.fetchFilteredContent(queryMap, stMap, hcaasStatus);
			//get bookmarked content
			Map<String,Object> userBookmarks = null;
			userBookmarks = learningBookmarkDAO.getBookmarksWithTime(ccoid);
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			for(NewLearningContentEntity entity : learningFilteredList){
				LearningContentItem learningItem = new LearningContentItem(entity);
				if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
						&& userBookmarks.keySet().contains(entity.getId())){
					learningItem.setBookmark(true);
					learningItem.setBookmarkTimeStamp(Instant.ofEpochMilli(Long.valueOf((String)userBookmarks.get(entity.getId()))).toString());
					result.add(learningItem);
					LearningStatusEntity userRegistration = userRegistrations.stream()
							.filter(userRegistrationInStream -> userRegistrationInStream.getLearningItemId()
									.equalsIgnoreCase(learningItem.getId()))
							.findFirst().orElse(null);
					if (userRegistration != null && userRegistration.getRegStatus() != null) {
						learningItem.setStatus(userRegistration.getRegStatus());
						learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp()!=null?userRegistration.getRegUpdatedTimestamp().toInstant().toString():null);
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
	public Map<String, Object> getBookmarkedFiltersWithCount(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		HashMap<String, Object> bookmarkedCounts = new HashMap<>();
		Map<String, Object>  result;
		try
		{
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			List<LearningContentItem> bookmarkedList = fetchBookMarkedContent(ccoid, new HashMap<>(), hcaasStatusBoolean);
			bookmarkedCounts = learningContentDAO.getBookmarkedFiltersWithCount(filtersSelected, bookmarkedList, hcaasStatus);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching bookmarked filter counts", e);
			throw new GenericException("There was a problem in fetching bookmarked filter counts");
		}
		result=orderFilters(bookmarkedCounts, LearningContentUtil.getDefaultFilterOrder());
		return result;
	}
	
	@Override
	public List<LearningContentItem> fetchUpcomingContent(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		List<LearningContentItem> result;
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
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			List<NewLearningContentEntity> upcomingContentList = learningContentDAO.fetchUpcomingContent(queryMap, stMap, hcaasStatus);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			result = getItemsWithRegAndBookmarkedCombined(upcomingContentList, userRegistrations, userBookmarks);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching upcoming learning content", e);
			throw new GenericException("There was a problem in fetching upcoming learning content");
		}
		return result;
	}
	
	@Override
	public Map<String, Object> getUpcomingFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		HashMap<String, Object> upcomingContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			upcomingContentCounts = learningContentDAO.getUpcomingFiltersWithCount(filtersSelected, hcaasStatus);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching upcoming filter counts", e);
			throw new GenericException("There was a problem in fetching upcoming filter counts");
		}
		result=orderFilters(upcomingContentCounts, LearningContentUtil.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchPopularContent(String ccoid, Map<String, Object> filtersSelected, String popularityType, String puid, boolean hcaasStatusBoolean) {
		List<NewLearningContentEntity> contentList = new ArrayList<>();
		List<LearningContentItem> result;
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
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			if(popularityType.equals(Constants.POPULAR_ACROSS_PARTNERS_PATH)) {
				contentList = learningContentDAO.fetchPopularAcrossPartnersContent(queryMap, stMap, userBookmarks, hcaasStatus);
			}
			if(popularityType.equals(Constants.POPULAR_AT_PARTNER_PATH)) {
				contentList = learningContentDAO.fetchPopularAtPartnerContent(queryMap, stMap, puid, userBookmarks, hcaasStatus);
			}
			// populate bookmark and registration info
			List<LearningStatusEntity> userRegistrations = learningStatusRepo.findByUserId(ccoid);
			result = getItemsWithRegAndBookmarkedCombined(contentList, userRegistrations, userBookmarks);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching popular across partners learning content", e);
			throw new GenericException("There was a problem in fetching popular across partners learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getPopularContentFiltersWithCount(Map<String, Object> filtersSelected, String puid, String popularityType, String userId, boolean hcaasStatusBoolean) {
		HashMap<String, Object> popularContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			Set<String> userBookmarks = null;
			if (null != userId) {
				userBookmarks = learningBookmarkDAO.getBookmarks(userId);
			}
			if(popularityType.equals(Constants.POPULAR_ACROSS_PARTNERS_PATH)) {
				popularContentCounts = learningContentDAO.getPopularAcrossPartnersFiltersWithCount(filtersSelected, userBookmarks, hcaasStatus);
			}
			if(popularityType.equals(Constants.POPULAR_AT_PARTNER_PATH)) {
				popularContentCounts = learningContentDAO.getPopularAtPartnerFiltersWithCount(filtersSelected, puid, userBookmarks, hcaasStatus);
			}
		}catch (Exception e) {
			LOG.error("There was a problem in fetching popular across partners filter counts", e);
			throw new GenericException("There was a problem in fetching popular across partners filter counts");
		}
		result=orderFilters(popularContentCounts, LearningContentUtil.getDefaultFilterOrder());
		return result;
	}

	@Override
	public List<LearningContentItem> fetchCXInsightsContent(String ccoid, Map<String, Object> filtersSelected, String searchToken,
			String sortField, String sortType, boolean hcaasStatus) {
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
			List<NewLearningContentEntity> contentList = learningContentDAO.fetchCXInsightsContent(ccoid, queryMap,
					stMap, searchToken, sortField, sortType, hcaasStatus);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			result = getItemsWithRegAndBookmarkedCombined(contentList, new ArrayList<>(), userBookmarks);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching CX Insights learning content", e);
			throw new GenericException("There was a problem in fetching CX Insights learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, Map<String, Object> filtersSelected, boolean hcaasStatus) {
		HashMap<String, Object> cxInsightsContentCounts = new HashMap<>();
		Map<String, Object> result;
		try
		{
			cxInsightsContentCounts = learningContentDAO.getCXInsightsFiltersWithCount(userId, searchToken, filtersSelected, hcaasStatus);
		}catch (Exception e) {
			LOG.error("There was a problem in fetching cx insights filters", e);
			throw new GenericException("There was a problem in fetching cx insights filters");
		}
		result=orderFilters(cxInsightsContentCounts, LearningContentUtil.getCXInsightsFilterOrder());
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
					List<String> lfcFilterOrderList = LearningContentUtil.getLFCFilterOrder();
					for(String filter : lfcFilterOrderList) {
						if(lfcOld.containsKey(filter)) {
							lfcFilterNew.put(filter, lfcOld.get(filter));}
					}
					filters.put(filterGroup, lfcFilterNew);
				}
				result.put(filterGroup, filters.get(filterGroup));
			}
		}
		return result;
	}
	
	@Override
	public List<LearningContentItem> fetchFeaturedContent(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		List<LearningContentItem> result = new ArrayList<>();
		Map<String, List<String>> queryMap = new HashMap<>();
		Object stMap = null;
		if (filtersSelected != null) {
			filtersSelected.keySet().forEach(filterGroup -> {
				if (!filterGroup.equals(Constants.ST_FILTER_KEY)) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) filtersSelected.get(filterGroup);
					queryMap.put(LearningContentServiceImpl.filterNameMappings.get(filterGroup), values);
				}
			});
			stMap = filtersSelected.get(Constants.ST_FILTER_KEY);
		}
		try {
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			List<NewLearningContentEntity> featuredContentList = learningContentDAO.fetchFeaturedContent(queryMap, stMap, hcaasStatus);
			// populate bookmark and registration info
			Set<String> userBookmarks = null;
			if (null != ccoid) {
				userBookmarks = learningBookmarkDAO.getBookmarks(ccoid);
			}
			result = getItemsWithRegAndBookmarkedCombined(featuredContentList, new ArrayList<>(), userBookmarks);
		} catch (Exception e) {
			LOG.error("There was a problem in fetching featured learning content", e);
			throw new GenericException("There was a problem in fetching featured learning content");
		}
		return result;
	}

	@Override
	public Map<String, Object> getFeaturedFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatusBoolean) {
		HashMap<String, Object> featuredContentCounts = new HashMap<>();
		Map<String, Object> result;
		try {
			String hcaasStatus = String.valueOf(hcaasStatusBoolean);
			featuredContentCounts = learningContentDAO.getFeaturedFiltersWithCount(filtersSelected, hcaasStatus);
		} catch (Exception e) {
			LOG.error("There was a problem in fetching featured filter counts", e);
			throw new GenericException("There was a problem in fetching featured filter counts");
		}
		result = orderFilters(featuredContentCounts, LearningContentUtil.getDefaultFilterOrder());
		return result;
	}

	public List<LearningContentItem> getItemsWithRegAndBookmarkedCombined(List<NewLearningContentEntity> learningContentList, List<LearningStatusEntity> userRegistrations,
			Set<String> userBookmarks) {
		List<LearningContentItem> result = new ArrayList<>();
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
				learningItem.setRegTimestamp(userRegistration.getRegUpdatedTimestamp()!=null?userRegistration.getRegUpdatedTimestamp().toInstant().toString():null);
			}
			result.add(learningItem);
		}
		return result;
	}

}
