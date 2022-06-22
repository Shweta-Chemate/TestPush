package com.cisco.cx.training.app.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntityPK;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.app.repo.PeerViewedRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.SuccessTipsAttachment;
import com.cisco.cx.training.util.ProductDocumentationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"squid:S134","squid:CommentedOutCodeLine","squid:S1200","java:S3776","java:S2221","java:S104","java:S4288", "java:S138", "common-java:DuplicatedBlocks"})
@Service
public class TPService extends ProductDocumentationService{
	private static final Logger LOG = LoggerFactory.getLogger(TPService.class);

	@Autowired
	private LearningBookmarkDAO learningDAO;

	@Autowired
	private ProductDocumentationDAO productDocumentationDAO;

	/** sort **/

	private static final String DEFAULT_SORT_FIELD = "sort_by_date";
	private static final Direction DEFAULT_SORT_ORDER = Sort.Direction.DESC;

	/** filters **/
	private static final String CONTENT_TYPE_FILTER = "Content Type";
	private static final String LANGUAGE_FILTER = "Language";
	private static final String LIVE_EVENTS_FILTER = "Live Events";
	private static final String SUCCESS_TRACKS_FILTER = "Success Tracks";  
	private static final String LIFECYCLE_FILTER="Lifecycle";
	private static final String TECHNOLOGY_FILTER = "Technology";	
	private static final String ROLE_FILTER = "Role";
	

	private void initializeTPFiltersByCards(final HashMap<String, Object> filters, final HashMap<String, Object> countFilters, 
			String contentTab, String hcaasStatus, Set<String> cardIds)
	{	
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put(CONTENT_TYPE_FILTER, contentTypeFilter);		
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(contentTab,cardIds, hcaasStatus);
		Map<String,String> allContentsCT = ProductDocumentationUtil.listToMap(dbListCT);countFilters.put(CONTENT_TYPE_FILTER, allContentsCT);
		allContentsCT.keySet().forEach(k -> contentTypeFilter.put(k, "0"));

		HashMap<String, String> technologyFilter = new HashMap<>();
		filters.put(TECHNOLOGY_FILTER, technologyFilter);		
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(contentTab, cardIds, hcaasStatus);
		Map<String,String> allContentsTC = ProductDocumentationUtil.listToMap(dbListTC);countFilters.put(TECHNOLOGY_FILTER, allContentsTC);
		allContentsTC.keySet().forEach(k -> technologyFilter.put(k, "0"));

		HashMap<String, String> languageFilter = new HashMap<>();
		filters.put(LANGUAGE_FILTER, languageFilter);		
		List<Map<String,Object>> dbListLG= productDocumentationDAO.getAllLanguageWithCountByCards(contentTab, cardIds, hcaasStatus);
		Map<String,String> allContentsLG = ProductDocumentationUtil.listToMap(dbListLG);countFilters.put(LANGUAGE_FILTER, allContentsLG);
		allContentsLG.keySet().forEach(k -> languageFilter.put(k, "0"));

		HashMap<String, String> regionFilter = new HashMap<>();
		filters.put(LIVE_EVENTS_FILTER, regionFilter);		
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(contentTab,cardIds, hcaasStatus);
		Map<String,String> allContentsLE = ProductDocumentationUtil.listToMap(dbListLE);countFilters.put(LIVE_EVENTS_FILTER, allContentsLE);
		allContentsLE.keySet().forEach(k -> regionFilter.put(k, "0"));

		//no documentation filter

		HashMap<String, Object> stFilter = new HashMap<>();
		filters.put(SUCCESS_TRACKS_FILTER, stFilter);
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCountByCards(contentTab, cardIds, hcaasStatus);//productDocumentationDAO.getAllStUcPsWithCount(contentTab);
		Map<String,Object> allContentsST = ProductDocumentationUtil.listToSTMap(dbListST,stFilter);countFilters.put(SUCCESS_TRACKS_FILTER, allContentsST);

		HashMap<String, Object> lcFilter = new HashMap<>();
		filters.put(LIFECYCLE_FILTER, lcFilter);
		List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPitstopsWithCountByCards(contentTab,cardIds, hcaasStatus);
		Map<String,String> allContentsLC = ProductDocumentationUtil.listToMap(dbListLC);countFilters.put(LIFECYCLE_FILTER, allContentsLC);
		allContentsLC.keySet().forEach(k -> lcFilter.put(k, "0"));

		//no for you filter 	

		HashMap<String, String> roleFilter = new HashMap<>();
		filters.put(ROLE_FILTER, roleFilter);		
		List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCountByCards(contentTab, cardIds, hcaasStatus);
		Map<String,String> allContentsRole = ProductDocumentationUtil.listToMap(dbListRole);countFilters.put(ROLE_FILTER, allContentsRole);
		allContentsRole.keySet().forEach(k -> roleFilter.put(k, "0"));

		//no for cisco+ filter 
	}


	private static final String TOPPICKS = "Toppicks";
	
	/** {"Language":["English","Japanese"], "Toppicks":["CI-100","SWAP-200"]} **/
	public Map<String, Object> getTopPicksFiltersViewMore(Map<String, Object> applyFilters, boolean hcaasStatusFlag) {	

		/** common start **/
		String hcaasStatus = String.valueOf(hcaasStatusFlag);LOG.info("tpf hcaas{}",hcaasStatus);
		String contentTab =  TOPPICKS;
		HashMap<String, Object> filters = new HashMap<>();	
		HashMap<String, Object> countFilters = new HashMap<>();			
		Set<String> cardIds = new HashSet();		
		if(!CollectionUtils.isEmpty(applyFilters))
		{
			Object cardIdsObj =  applyFilters.remove(contentTab);			
			if(cardIdsObj instanceof List)
			{
				List<String> cardIdsList = (List<String>)cardIdsObj;
				if(!CollectionUtils.isEmpty(cardIdsList))
				{
					cardIds.addAll(cardIdsList);
				}		
			}		
		}		
		initializeTPFiltersByCards(filters,countFilters,contentTab, hcaasStatus, cardIds); //extra but must here
		Map<String, Set<String>> filteredCardsMap = new HashMap<>();	
		if(!cardIds.isEmpty() && !CollectionUtils.isEmpty(applyFilters))		
		{
			filteredCardsMap = filterTpCards(applyFilters,contentTab,hcaasStatus,cardIds);
			cardIds = ProductDocumentationUtil.andFilters(filteredCardsMap);			
		}
		/** common end **/	
		boolean search=false;
		Set<String> searchCardIds =  new HashSet<>();
		if(!CollectionUtils.isEmpty(applyFilters) && applyFilters.size()==1)
		{
			applyFilters.keySet().forEach(k -> filters.put(k, countFilters.get(k)));
		}	
		setFilterCounts(cardIds,filters,filteredCardsMap,search,contentTab, searchCardIds, hcaasStatus);	
		cleanFilters(filters);

		return orderFilters(filters, contentTab);
	}

	/** {"Language":["English","Japanese"], "Toppicks":["CI-100","SWAP-200"]} **/
	public LearningRecordsAndFiltersModel getTopPicksCardsViewMore(String userId, Map<String, Object> applyFilters, boolean hcaasStatusFlag) {	

		/** common start **/
		String hcaasStatus = String.valueOf(hcaasStatusFlag);LOG.info("tpc hcaas{}",hcaasStatus);
		String contentTab =  TOPPICKS;
		Set<String> filteredTPCards = new HashSet();
		if(!CollectionUtils.isEmpty(applyFilters))
		{
			Object cardIdsObj =  applyFilters.remove(contentTab);			
			if(cardIdsObj instanceof List)
			{
				List<String> cardIdsList = (List<String>)cardIdsObj;
				if(!CollectionUtils.isEmpty(cardIdsList))
				{
					filteredTPCards.addAll(cardIdsList);
				}		
			}		
		}				
		if(!filteredTPCards.isEmpty() && !CollectionUtils.isEmpty(applyFilters))
		{
			Map<String, Set<String>> filteredCardsMap = filterTpCards(applyFilters,contentTab,hcaasStatus,filteredTPCards);
			filteredTPCards = ProductDocumentationUtil.andFilters(filteredCardsMap);	//filtered toppicks 
		}
		/** common end **/

		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);
		if(!filteredTPCards.isEmpty())
		{			
			List<LearningItemEntity> dbCards = new ArrayList<>();
			long requestStartTime = System.currentTimeMillis();	
			Set<String> userBookmarks = learningDAO.getBookmarks(userId);
			LOG.info("TP-UB in {} ", (System.currentTimeMillis() - requestStartTime));
			requestStartTime = System.currentTimeMillis();	
			dbCards.addAll(productDocumentationDAO.getAllLearningCardsByFilter(contentTab,filteredTPCards,
					Sort.by(DEFAULT_SORT_ORDER, DEFAULT_SORT_FIELD), hcaasStatus));
			LOG.info("TP-FC in {} ", (System.currentTimeMillis() - requestStartTime));
			learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));		
		}		
		return responseModel;
	}

	//no for you, documentation, specialization
	private Map<String, Set<String>> filterTpCards(Map<String, Object> applyFilters, String contentTab, String hcaasStatus,Set<String> tpCardIds)
	{	
		LOG.info("TP applyFilters = {}",applyFilters);	
		Map<String, Set<String>> filteredCards = new HashMap<>();
		if(CollectionUtils.isEmpty(applyFilters)) {return filteredCards;}

		/** OR **/
		applyFilters.keySet().forEach(k -> {
			Object v = applyFilters.get(k);
			List<String> list;
			if(v instanceof List) {
				list= (List<String>)v;				
				switch(k) {
				case TECHNOLOGY_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByTC(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				case LIVE_EVENTS_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByRegion(contentTab,new HashSet<>(list), hcaasStatus,tpCardIds));break;
				case CONTENT_TYPE_FILTER : filteredCards.put(k, productDocumentationDAO.getTpLearningsByContentType(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				case LANGUAGE_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByLanguage(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				case ROLE_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByRole(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				case LIFECYCLE_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByPs(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				//case Constants.CISCO_PLUS_FILTER : filteredCards.put(k, productDocumentationDAO.getTpCardIdsByCiscoPlus(contentTab,new HashSet<>(list),hcaasStatus, tpCardIds));break;
				default : LOG.info("TP other {}={}",k,list);
				};
			}
			else if ( v instanceof Map) {	
				Set<String> cardIdsStUcPs = new HashSet<>();
				((Map) v).keySet().forEach(ik->{
					Object iv = ((Map)v).get(ik);
					List<String> ilist;
					if(iv instanceof Map) {
						Set<String> usecaseS= ((Map) iv).keySet(); String successtrack = ik.toString();
						cardIdsStUcPs.addAll(productDocumentationDAO.getTpCardIdsByPsUcSt(contentTab,successtrack,usecaseS,hcaasStatus, tpCardIds));
					}
				});
				filteredCards.put(k,cardIdsStUcPs);
			}
		});

		LOG.info("TP filteredCards = {} {} ",filteredCards.size(), filteredCards);	
		return filteredCards;

	}




}
