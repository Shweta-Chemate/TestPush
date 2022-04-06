package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.PartnerPortalLookupDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.UserLearningPreferencesDAO;
import com.cisco.cx.training.app.entities.PartnerPortalLookUpEntity;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserLearningPreference;
import com.cisco.cx.training.util.SuccessAcademyMapper;

@SuppressWarnings({"squid:S1200"})
@Service
public class TrainingAndEnablementServiceImpl implements TrainingAndEnablementService {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private CommunityDAO communityDAO;

	@Autowired
	private SuccessAcademyDAO successAcademyDAO;
	
	@Autowired
	private PartnerPortalLookupDAO partnerPortalLookupDAO;

	@SuppressWarnings("unused")
	@Autowired
	private SmartsheetDAO smartsheetDAO;

	@Autowired
	private PartnerProfileService partnerProfileService;
	
	@Autowired
	private LearningBookmarkDAO learningDAO;
	
	@Autowired
	private ProductDocumentationService productDocumentationService;

	@Autowired
	UserLearningPreferencesDAO userLearningPreferencesDAO;

	
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
	public CountSchema getCommunityCount() {

		CountSchema communityCount = new CountSchema();
		communityCount.setLabel("Cisco Community");
		// Community Count is currently hardcoded to 1
		communityCount.setCount(1L);
		return communityCount;
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
		for(Entry<String, List<String>> entry : mapData.entrySet()){		
			String key = entry.getKey();
			SuccessAcademyFilter filter = new SuccessAcademyFilter();					
			filter.setName(key);
			filter.setFilters(entry.getValue());					
			filter.setTabLocationOnUI(lookupValues.get(key.toLowerCase().replaceAll(" ", ""))); //NOSONAR
			filters.add(filter);
		}
		LOG.info("Sending final response in {} ", (System.currentTimeMillis() - requestStartTime));
		return filters;
	}

	@Override
	public BookmarkResponseSchema bookmarkLearningForUser(
			BookmarkRequestSchema bookmarkRequestSchema,
			String xMasheryHandshake, String puid) {
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
			learningDAO.createOrUpdate(bookmarkResponseSchema, puid);
			LOG.info("Updated bookmark in {} ", (System.currentTimeMillis() - requestStartTime));
			return bookmarkResponseSchema;
		}
	}
	
	
	private Map<String,String> getLookUpMapFromEntity(List<PartnerPortalLookUpEntity> entityList){
		HashMap<String, String> lookUpValues = new HashMap<String, String>();
		for(PartnerPortalLookUpEntity entity : entityList){
			String key = entity.getPartnerPortalKey().replaceAll(CXPP_UI_TAB_PREFIX, ""); //NOSONAR
			lookUpValues.put(key.toLowerCase(), entity.getPartnerPortalKeyValue());
		}		
		return lookUpValues;
	}

	@Override
	public LearningRecordsAndFiltersModel getAllLearningInfoPost(String xMasheryHandshake, String searchToken,
			HashMap<String, Object> filters, String sortBy, String sortOrder, String contentTab) {
		
		return productDocumentationService.getAllLearningInfo(xMasheryHandshake,searchToken,filters,sortBy, sortOrder,contentTab);
	}

	@Override
	public Map<String, Object> getAllLearningFiltersPost(String searchToken, HashMap<String, Object> filters, String contentTab) {
		return productDocumentationService.getAllLearningFilters(searchToken,filters,contentTab);
	}

	@Override
	public Map<String, List<UserLearningPreference>> postUserLearningPreferences(String xMasheryHandshake,
			Map<String, List<UserLearningPreference>> userPreferences) {
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		return userLearningPreferencesDAO.createOrUpdateULP(ccoId, userPreferences);
	}

	@Override
	public Map<String, List<UserLearningPreference>> getUserLearningPreferences(String xMasheryHandshake) {
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();	
		return userLearningPreferencesDAO.fetchUserLearningPreferences(ccoId);
	}

	@Override
	public LearningRecordsAndFiltersModel getMyPreferredLearnings(String xMasheryHandshake, String search,
			HashMap<String, Object> filters, String sortBy, String sortOrder, String puid,Integer limit) {		
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		//get specialization info
		List<String> specializations = new ArrayList<>();
		specializations.add("pls");
		specializations.add("offer");
		HashMap<String, Object> preferences = userLearningPreferencesDAO.getULPPreferencesDDB(ccoId);
		preferences.put(Constants.SPECIALIZATION_FILTER, specializations);
		return productDocumentationService.fetchMyPreferredLearnings(ccoId,search,filters,sortBy, sortOrder,puid,preferences,limit);
		
	}
}

