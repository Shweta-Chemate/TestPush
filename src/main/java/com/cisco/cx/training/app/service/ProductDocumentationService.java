package com.cisco.cx.training.app.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

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
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductDocumentationService{
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private LearningBookmarkDAO learningDAO;
	
	@Autowired
	private PartnerProfileService partnerProfileService;

	@Autowired
	private ProductDocumentationDAO productDocumentationDAO;
	
	@Autowired
	private NewLearningContentRepo learningContentRepo;
	
	@Autowired
	private PeerViewedRepo peerViewedRepo;
	
	@Value("${top.picks.learnings.display.limit}")
	public Integer topicksLimit;
	
	private Map<String, Set<String>> filterCards(HashMap<String, Object> applyFilters, String contentTab)
	{	
		LOG.info("applyFilters = {}",applyFilters);	
		Map<String, Set<String>> filteredCards = new HashMap<String, Set<String>>();
		if(applyFilters==null || applyFilters.isEmpty()) return filteredCards;
		
		/** OR **/
		applyFilters.keySet().forEach(k -> {
			Object v = applyFilters.get(k);
			List<String> list;
			if(v instanceof List) {
				list= (List<String>)v;				
				switch(k) {
				case TECHNOLOGY_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByTC(contentTab,new HashSet<String>(list)));break;
				//case DOCUMENTATION_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByAT(contentTab,new HashSet<String>(list)));break;
				case LIVE_EVENTS_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByRegion(contentTab,new HashSet<String>(list)));break;
				case CONTENT_TYPE_FILTER : filteredCards.put(k, productDocumentationDAO.getLearningsByContentType(contentTab,new HashSet<String>(list)));break;
				case LANGUAGE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByLanguage(contentTab,new HashSet<String>(list)));break;
				case FOR_YOU_FILTER : filteredCards.put(k, getCardIdsByYou(contentTab,new HashSet<String>(list)));break;
				case ROLE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByRole(contentTab, new HashSet<String>(list)));break;
				case LIFECYCLE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByPsUcSt(contentTab,new HashSet<String>(list)));break;
				default : LOG.info("other {}={}",k,list);
				};
			}
			else if ( v instanceof Map) {	
				Set<String> cardIdsStUcPs = new HashSet<String>();
				//LOG.info("ST="+((Map) v).keySet());
				((Map) v).keySet().forEach(ik->{
					Object iv = ((Map)v).get(ik);
					List<String> ilist;
					if(iv instanceof Map) {
						//LOG.info("UC="+((Map) iv).keySet());
						Set<String> usecaseS= ((Map) iv).keySet(); String successtrack = ik.toString();
						cardIdsStUcPs.addAll(productDocumentationDAO.getCardIdsByPsUcSt(contentTab,successtrack,usecaseS));
					/*	((Map)iv).keySet().forEach(ivk -> {
							Object ivv = ((Map)iv).get(ivk);
							List<String> ivlist;
							if(ivv instanceof List) 
							{
								ivlist= (List<String>)ivv;
								LOG.info("PS={} uc={} st={}",ivlist,ivk,ik);
								Set<String> pitStops = new HashSet<String>(ivlist);
								String usecase = ivk.toString();
								String successtrack = ik.toString();
								cardIdsStUcPs.addAll(productDocumentationDAO.getCardIdsByPsUcSt(contentTab,successtrack,usecase,pitStops));
							}						
						}); */
					}
				});
				filteredCards.put(k,cardIdsStUcPs);
			}
		});
		
		LOG.info("filteredCards = {} {} ",filteredCards.size(), filteredCards);	
		return filteredCards;
	
	}
	
	private Set<String> andFilters(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<String>();
		
		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			for(int i=0; i<keys.length; i++)
			{
				if(i==0) cardIds.addAll(filteredCards.get(keys[i]));
				else cardIds.retainAll(filteredCards.get(keys[i]));
			}
		}
		LOG.info("mapped = {} ",cardIds);	
		
		return cardIds;
	}
	
	private Map<String,String> getLearningMapCounts()
	{
		List<Map<String, Object>> dbList = productDocumentationDAO.getLearningMapCounts();
		Map<String, String> lmCounts = listToMap(dbList);//LOG.info("lmCounts={}",lmCounts);
		return lmCounts;
	}
	
	//"createdTimeStamp": "2021-04-05 17:10:50.0",card.setCreatedTimeStamp(learning.getUpdated_timestamp().toString());//yyyy-mm-dd hh:mm:ss.fffffffff
	private List<GenericLearningModel>  mapLearningEntityToCards(List<LearningItemEntity> dbList, Set<String> userBookmarks)
	{
		
		Map<String, String> lmCounts = getLearningMapCounts();
		List<GenericLearningModel>  cards = new ArrayList<GenericLearningModel>();
		if(dbList==null || dbList.size()==0) return cards;
		dbList.forEach(learning -> {
			
			GenericLearningModel card =  new GenericLearningModel();	
			if(learning.getSortByDate()==null)card.setCreatedTimeStamp(null);
			else card.setCreatedTimeStamp(Timestamp.valueOf(learning.getSortByDate()));  //same as created date
			card.setDescription(learning.getDescription());
			card.setDuration(learning.getDuration());
			
			if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)
					&& userBookmarks.contains(learning.getLearning_item_id()))
			card.setIsBookMarked(true);
		
			//card.setLink(learning.getRegistrationUrl());//learning.getLink()
			card.setStatus(learning.getStatus());
			card.setPresenterName(learning.getPresenterName());
			card.setRowId(learning.getLearning_item_id());card.setId(learning.getLearning_item_id());
			card.setTitle(learning.getTitle());
			card.setType(learning.getLearning_type());
			card.setRating(learning.getPiw_score());
			
			card.setRegistrationUrl(learning.getRegistrationUrl());
			card.setRecordingUrl(learning.getRecordingUrl());
			
			card.setLink(learning.getAsset_links());
			card.setContentType(learning.getAsset_types());
			
			card.setAvgRatingPercentage(learning.getAvgRatingPercentage());
			card.setTotalCompletions(learning.getTotalCompletions());
			card.setVotesPercentage(learning.getVotesPercentage());

			card.setLearning_map(learning.getLearning_map());
			if(LEARNING_MAP_TYPE.equals(learning.getLearning_type())
					&& lmCounts.containsKey(learning.getLearning_item_id()))
			{
				card.setModulecount(lmCounts.get(learning.getLearning_item_id()));
			}
											
			cards.add(card);
		});
		return cards;
	}
	
	private Map<String,String> listToMap(List<Map<String,Object>> dbList)
	{
		Map<String,String> countMap = new HashMap<String,String>();
		for(Map<String,Object> dbMap : dbList)
		{
			String dbKey = String.valueOf(dbMap.get("dbkey"));
			String dbValue = String.valueOf(dbMap.get("dbvalue"));			
			countMap.put(dbKey,dbValue);		
		}
		return countMap;
	}
	
	private Map<String,Object> listToSTMap(List<Map<String,Object>> dbList, final Map<String,Object> stFilter)
	{
		Map<String,Object> stAllKeysMap = new HashMap<String,Object>();
		Map<String,Object> stCountMap = new HashMap<String,Object>();
		
		Map<String,Object> stMap = new HashMap<String,Object>();//new HashMap<String,Map<String,Map<String,String>>>();
		
		Set<String> distinctST = new HashSet<String>();
		Map<String,List<String>> distinctUCForST = new HashMap<String,List<String>>();
		Map<String,List<String>> distinctPSForUC = new HashMap<String,List<String>>();
		
		
		for(Map<String,Object> dbMap : dbList)
		{
			String st = String.valueOf(dbMap.get("successtrack"));
			String uc = String.valueOf(dbMap.get("usecase"));
			String ps = String.valueOf(dbMap.get("pitstop"));
			
			String dbValue = String.valueOf(dbMap.get("dbvalue"));	
			
			distinctST.add(st);
			if(!distinctUCForST.keySet().contains(st)) distinctUCForST.put(st, new ArrayList<String>());
			distinctUCForST.get(st).add(uc);
			if(!distinctPSForUC.keySet().contains(uc)) distinctPSForUC.put(uc, new ArrayList<String>());
			distinctPSForUC.get(uc).add(ps);
			
			if(!stMap.keySet().contains(st)) stMap.put(st, new HashMap<String,Map<String,String>>()) ;
			if(!((Map)stMap.get(st)).keySet().contains(uc)) ((Map)stMap.get(st)).put(uc, dbValue);//.put(uc, new HashMap<String,String>());
			//if(!((Map)((Map)stMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stMap.get(st)).get(uc)).put(ps, dbValue);
			
			if(stFilter!=null)
			{
				if(!stAllKeysMap.keySet().contains(st)) stAllKeysMap.put(st, new HashMap<String,Map<String,String>>()) ;
				if(!((Map)stAllKeysMap.get(st)).keySet().contains(uc)) ((Map)stAllKeysMap.get(st)).put(uc, "0");//.put(uc, new HashMap<String,String>());
				//if(!((Map)((Map)stAllKeysMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stAllKeysMap.get(st)).get(uc)).put(ps, "0");
			}					
		}		
		stCountMap.putAll(stMap);if(stFilter!=null)stFilter.putAll(stAllKeysMap);		
		
		LOG.info("stCountMap {} , stFilter={}",stCountMap, stFilter);
		
		return stCountMap;
	}
	
	/** sort **/
	
	private static final String DEFAULT_SORT_FIELD = "sort_by_date";
	private static final Direction DEFAULT_SORT_ORDER = Sort.Direction.DESC;

	/** filters **/
	private static final String CONTENT_TYPE_FILTER = "Content Type";
	private static final String LANGUAGE_FILTER = "Language";
	private static final String LIVE_EVENTS_FILTER = "Live Events";
	//private static final String DOCUMENTATION_FILTER = "Documentation";
	private static final String SUCCESS_TRACKS_FILTER = "Success Tracks";  
	private static final String LIFECYCLE_FILTER="Lifecycle";
	private static final String TECHNOLOGY_FILTER = "Technology";
	private static final String FOR_YOU_FILTER = "For You";
	private static final String ROLE_FILTER = "Role";
	private static final String[] FILTER_CATEGORIES = new String[]{ 
			SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER, //DOCUMENTATION_FILTER,
			ROLE_FILTER, 
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER };
	
	private static final String[] FILTER_CATEGORIES_ROLE = new String[]{ 
			ROLE_FILTER, SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER, 
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER };
	
	private static final String[] FOR_YOU_KEYS = new String[]{"New","Top Picks","Based on Your Customers",
			"Bookmarked","Popular with Partners"};
	
	/** nulls **/
	private static final String NULL_TEXT = "null";
	
	/** lmap **/
	private static final String LEARNING_MAP_TYPE = "learningmap";
	
	private void initializeFilters(final HashMap<String, Object> filters, final HashMap<String, Object> countFilters, String contentTab)
	{	
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put(CONTENT_TYPE_FILTER, contentTypeFilter);		
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCount(contentTab);
		Map<String,String> allContentsCT = listToMap(dbListCT);countFilters.put(CONTENT_TYPE_FILTER, allContentsCT);
		allContentsCT.keySet().forEach(k -> contentTypeFilter.put(k, "0"));
		
		HashMap<String, String> technologyFilter = new HashMap<>();
		filters.put(TECHNOLOGY_FILTER, technologyFilter);		
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCount(contentTab);
		Map<String,String> allContentsTC = listToMap(dbListTC);countFilters.put(TECHNOLOGY_FILTER, allContentsTC);
		allContentsTC.keySet().forEach(k -> technologyFilter.put(k, "0"));
		
		HashMap<String, String> languageFilter = new HashMap<>();
		filters.put(LANGUAGE_FILTER, languageFilter);		
		List<Map<String,Object>> dbListLG= productDocumentationDAO.getAllLanguageWithCount(contentTab);
		Map<String,String> allContentsLG = listToMap(dbListLG);countFilters.put(LANGUAGE_FILTER, allContentsLG);
		allContentsLG.keySet().forEach(k -> languageFilter.put(k, "0"));
		
		/*if(contentTab.equals(TECHNOLOGY_DB_TABLE))
		{
			HashMap<String, String> documentationFilter = new HashMap<>();
			filters.put(DOCUMENTATION_FILTER, documentationFilter);		
			List<Map<String,Object>> dbListDC = productDocumentationDAO.getAllDocumentationWithCount(contentTab);
			Map<String,String> allContentsDC = listToMap(dbListDC);countFilters.put(DOCUMENTATION_FILTER, allContentsDC);
			allContentsDC.keySet().forEach(k -> documentationFilter.put(k, "0"));
		}*/
		
		HashMap<String, String> regionFilter = new HashMap<>();
		filters.put(LIVE_EVENTS_FILTER, regionFilter);		
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCount(contentTab);
		Map<String,String> allContentsLE = listToMap(dbListLE);countFilters.put(LIVE_EVENTS_FILTER, allContentsLE);
		allContentsLE.keySet().forEach(k -> regionFilter.put(k, "0"));
		
		//if(contentTab.equals(TECHNOLOGY_DB_TABLE))
		{
		HashMap<String, Object> stFilter = new HashMap<>();
		filters.put(SUCCESS_TRACKS_FILTER, stFilter);
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCount(contentTab);//productDocumentationDAO.getAllStUcPsWithCount(contentTab);
		Map<String,Object> allContentsST = listToSTMap(dbListST,stFilter);countFilters.put(SUCCESS_TRACKS_FILTER, allContentsST);
		
		HashMap<String, Object> lcFilter = new HashMap<>();
		filters.put(LIFECYCLE_FILTER, lcFilter);
		List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPsWithCount(contentTab);
		Map<String,String> allContentsLC = listToMap(dbListLC);countFilters.put(LIFECYCLE_FILTER, allContentsLC);
		allContentsLC.keySet().forEach(k -> lcFilter.put(k, "0"));
		}
		
		HashMap<String, Object> youFilter = new HashMap<>();
		filters.put(FOR_YOU_FILTER, youFilter);		
		Map<String,String> allContentsYou = getForYouCounts(contentTab,null);countFilters.put(FOR_YOU_FILTER, allContentsYou);
		allContentsYou.keySet().forEach(k -> youFilter.put(k, "0"));		
		
		//if(contentTab.equals(ROLE_DB_TABLE))
		{
		HashMap<String, String> roleFilter = new HashMap<>();
		filters.put(ROLE_FILTER, roleFilter);		
		List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCount(contentTab);
		Map<String,String> allContentsRole = listToMap(dbListRole);countFilters.put(ROLE_FILTER, allContentsRole);
		allContentsRole.keySet().forEach(k -> roleFilter.put(k, "0"));
		}
	}
	
	private Set<String> getCardIdsByYou(String contentTab, HashSet<String> youList)
	{
		final Set<String> cardIds = new HashSet<String>();
		youList.forEach(youKey ->
		{
			if(Arrays.asList(FOR_YOU_KEYS).contains(youKey))
			{
				if(youKey.equals(FOR_YOU_KEYS[0])) //New
				{
					final Set<String> cardIdsNew = new HashSet<String>();
					List<NewLearningContentEntity> result = learningContentRepo.findNew();
					result.forEach(card -> cardIdsNew.add(card.getId()));
					if(!cardIdsNew.isEmpty())
					cardIds.addAll(productDocumentationDAO.getAllNewCardIdsByCards(contentTab, cardIdsNew));
				}
				else if(youKey.equals(FOR_YOU_KEYS[3])) //Bookmarked
				{
					//TODO  cardIds.add(card.getId());
				}				
			}
			else
			{
				LOG.info("other youKey = {}",youKey);
			}
		});
		LOG.info("Yous = {}",cardIds);
		return cardIds;
	}

	private Map<String,String>  getForYouCounts(String contentTab, Set<String>cardIds)
	{
		Map<String,String> youMap = new HashMap<String,String>();
		
		//1. New
		List<NewLearningContentEntity> result = learningContentRepo.findNew();//1 month as of now
		if(result == null) youMap.put(FOR_YOU_KEYS[0], "0"); 
		else 
		{
			Set<String> cardIdsNew = new HashSet<String>();
			Set<String> dbSet = new HashSet<String>();
			result.forEach(card -> cardIdsNew.add(card.getId()));
			dbSet.addAll(productDocumentationDAO.getAllNewCardIdsByCards(contentTab, cardIdsNew));
			
			if(cardIds!=null) // && !cardIds.isEmpty() -- set can be empty here
			{							
				dbSet.retainAll(cardIds);
			}
			
			youMap.put(FOR_YOU_KEYS[0], String.valueOf(dbSet.size()));
						
		}
		
		//2. Bookmarked
		
		return youMap;
	}
	
	private void setFilterCounts(Set<String> cardIdsInp, final HashMap<String, Object> filters, 
			Map<String, Set<String>> filteredCardsMap, boolean search, String contentTab, Set<String> searchCardIds)
	{
		LOG.info("filteredCardsMap={}",filteredCardsMap);
		if(filteredCardsMap ==null || filteredCardsMap.isEmpty() || (filteredCardsMap.size()==1 && search) )  //only search
		{
			setFilterCounts(cardIdsInp, filters,contentTab, filteredCardsMap,search, searchCardIds);
		}			
		else
		{
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,CONTENT_TYPE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(CONTENT_TYPE_FILTER)).putAll(listToMap(dbListCT));
			
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,TECHNOLOGY_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(TECHNOLOGY_FILTER)).putAll(listToMap(dbListTC));
			
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,LANGUAGE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLG = productDocumentationDAO.getAllLanguageWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(LANGUAGE_FILTER)).putAll(listToMap(dbListLG));
			
			/*if(contentTab.equals(TECHNOLOGY_DB_TABLE))
			{
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,DOCUMENTATION_FILTER,cardIdsInp,search);
			List<Map<String,Object>> dbListDC = productDocumentationDAO.getAllDocumentationWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(DOCUMENTATION_FILTER)).putAll(listToMap(dbListDC));
			}*/
			
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,LIVE_EVENTS_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(LIVE_EVENTS_FILTER)).putAll(listToMap(dbListLE));	
			
			//if(contentTab.equals(TECHNOLOGY_DB_TABLE))
			{
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,SUCCESS_TRACKS_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCountByCards(contentTab,cardIds);//productDocumentationDAO.getAllStUcPsWithCountByCards(contentTab,cardIds);
			Map<String,Object> filterAndCountsFromDb = listToSTMap(dbListST,null);
			mergeSTFilterCounts(filters,filterAndCountsFromDb);
			
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,LIFECYCLE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPitstopsWithCountByCards(contentTab,cardIds);		
			((Map<String,String>)filters.get(LIFECYCLE_FILTER)).putAll(listToMap(dbListLC));
			
			}
			
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,FOR_YOU_FILTER,searchCardIds,search);
			Map<String,String> dbMapYou = getForYouCounts(contentTab,cardIds);		
			((Map<String,String>)filters.get(FOR_YOU_FILTER)).putAll(dbMapYou);				
			
			//if(contentTab.equals(ROLE_DB_TABLE))
			{
			cardIds = andFiltersWithExcludeKey(filteredCardsMap,ROLE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCountByCards(contentTab, cardIds);		
			((Map<String,String>)filters.get(ROLE_FILTER)).putAll(listToMap(dbListRole));
			}
		}		
	}
	
	private Set<String> andFiltersWithExcludeKey(Map<String, Set<String>> filteredCards, String excludeKey, 
			Set<String> cardIdsInp, boolean search)
	{
		Set<String> cardIds =  new HashSet<String>();
		
		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			int first=-1;
			for(int i=0; i<keys.length; i++)
			{
				if(keys[i].equalsIgnoreCase(excludeKey)) continue;
				if(first==-1) first=i;
				if(i==first) cardIds.addAll(filteredCards.get(keys[i]));
				else cardIds.retainAll(filteredCards.get(keys[i]));
			}
			LOG.info("mapped with exclude key {} = {} cardIdsInp={}", excludeKey, cardIds, cardIdsInp);	
			if(search) cardIds.retainAll(cardIdsInp);
		}
		
		LOG.info("count for exclude key {} = {}", excludeKey, cardIds);	
		return cardIds;
	}
	
	private void setFilterCounts(Set<String> cardIdsInp, final HashMap<String, Object> filters, String contentTab, 
			Map<String, Set<String>> filteredCardsMap, boolean search, Set<String> searchCardIds)
	{
		Set<String> cardIds = cardIdsInp;
		
		if(search && filteredCardsMap.containsKey(CONTENT_TYPE_FILTER)) cardIds = searchCardIds;  
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(CONTENT_TYPE_FILTER)).putAll(listToMap(dbListCT));
				
		if(search && filteredCardsMap.containsKey(TECHNOLOGY_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp; 
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(TECHNOLOGY_FILTER)).putAll(listToMap(dbListTC));
		
		if(search && filteredCardsMap.containsKey(LANGUAGE_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		List<Map<String,Object>> dbListLG = productDocumentationDAO.getAllLanguageWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(LANGUAGE_FILTER)).putAll(listToMap(dbListLG));
		
		/*if(contentTab.equals(TECHNOLOGY_DB_TABLE))
		{
		if(search && filteredCardsMap.containsKey(DOCUMENTATION_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		List<Map<String,Object>> dbListDC = productDocumentationDAO.getAllDocumentationWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(DOCUMENTATION_FILTER)).putAll(listToMap(dbListDC));
		}*/
		
		if(search && filteredCardsMap.containsKey(LIVE_EVENTS_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(LIVE_EVENTS_FILTER)).putAll(listToMap(dbListLE));	
		
		//if(contentTab.equals(TECHNOLOGY_DB_TABLE))
		{
		if(search && filteredCardsMap.containsKey(SUCCESS_TRACKS_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCountByCards(contentTab,cardIds);//productDocumentationDAO.getAllStUcPsWithCountByCards(contentTab,cardIds);
		Map<String,Object> filterAndCountsFromDb = listToSTMap(dbListST,null);
		mergeSTFilterCounts(filters,filterAndCountsFromDb);
		
		if(search && filteredCardsMap.containsKey(LIFECYCLE_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;		
		List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPitstopsWithCountByCards(contentTab,cardIds);		
		((Map<String,String>)filters.get(LIFECYCLE_FILTER)).putAll(listToMap(dbListLC));		
		}
		
		if(search && filteredCardsMap.containsKey(FOR_YOU_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		Map<String,String> dbMapYou = getForYouCounts(contentTab,cardIds);		
		((Map<String,String>)filters.get(FOR_YOU_FILTER)).putAll(dbMapYou);
				
		//if(contentTab.equals(ROLE_DB_TABLE))
		{
		if(search && filteredCardsMap.containsKey(ROLE_FILTER)) cardIds = searchCardIds; else cardIds = cardIdsInp;
		List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCountByCards(contentTab, cardIds);		
		((Map<String,String>)filters.get(ROLE_FILTER)).putAll(listToMap(dbListRole));
		}
		
	}
	
	private void mergeSTFilterCounts(Map<String,Object> filters , Map<String,Object> filterAndCountsFromDb) {
		Map<String,Object> stFilters = ((Map<String,Object>)filters.get(SUCCESS_TRACKS_FILTER));
		for(String stkey : filterAndCountsFromDb.keySet()) {
			if(stFilters.containsKey(stkey)) {
				Map<String,Object> stFilter = (Map<String,Object>)stFilters.get(stkey);
				Map<String,Object> stFilterFromDB = (Map<String,Object>)filterAndCountsFromDb.get(stkey);
				for(String useCaseKey : stFilterFromDB.keySet()) {
					if(stFilter.containsKey(useCaseKey)) {
						stFilter.put(useCaseKey, stFilterFromDB.get(useCaseKey)); //addition
						/*
						 * Map<String,Object> useCaseFilter =
						 * (Map<String,Object>)stFilter.get(useCaseKey); Map<String,Object>
						 * useCaseFilterFromDB = (Map<String,Object>)stFilterFromDB.get(useCaseKey);
						 * for(String pitStopKey : useCaseFilterFromDB.keySet()) {
						 * if(useCaseFilter.containsKey(pitStopKey)) { useCaseFilter.put(pitStopKey,
						 * useCaseFilterFromDB.get(pitStopKey)); } }
						 */
					}
				}
			}
		}
	}

	/**
	 * {"Success Tracks":{"Security":{"ABC":["Umbrella"],"Security1":["Anti-Virus","Firewall"]},"Campus Network":{"XYZ":["Use"],"Campus Software Image Management":["Use","Implement"]}}}
	 * {"Language":["English","Japanese"],"Content Type":["PDF","Video"],"Success Tracks":{"Security":{"ABC":["Umbrella"],"Security1":["Anti-Virus","Firewall"]},"Campus Network":{"XYZ":["Use"],"CSIM":["Onboard","Implement"]}}}
	 * @param searchToken
	 * @param applyFilters
	 * @param contentTab 
	 * @return
	 */
	public Map<String, Object> getAllLearningFilters(String searchToken, HashMap<String, Object> applyFilters, String contentTabInp) 
	{		
		String contentTab =  contentTabInp!=null && contentTabInp.toLowerCase().equals(ROLE_DB_TABLE.toLowerCase())?
				ROLE_DB_TABLE:TECHNOLOGY_DB_TABLE;
		
		HashMap<String, Object> filters = new HashMap<>();	
		HashMap<String, Object> countFilters = new HashMap<>();
		
		initializeFilters(filters,countFilters,contentTab);
				
		Set<String> cardIds =  new HashSet<String>();
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		boolean search=false;
		Set<String> searchCardIds =  new HashSet<String>();
		
		if( searchToken!=null && !searchToken.trim().isEmpty() && applyFilters!=null && !applyFilters.isEmpty()	)
		{
			search = true;
			filteredCardsMap = filterCards(applyFilters,contentTab);
			Set<String> filteredCards = andFilters(filteredCardsMap);
			cardIds = productDocumentationDAO.getAllLearningCardIdsByFilterSearch(contentTab,filteredCards,"%"+searchToken+"%");
			
			//if(applyFilters.size()==1)
			{
				searchCardIds = productDocumentationDAO.getAllLearningCardIdsBySearch(contentTab,"%"+searchToken+"%");	
			}
		}		
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{			
			search = true;
			cardIds = productDocumentationDAO.getAllLearningCardIdsBySearch(contentTab,"%"+searchToken+"%");				
		}
		else if(applyFilters!=null && !applyFilters.isEmpty())
		{
			filteredCardsMap = filterCards(applyFilters,contentTab);
			cardIds = andFilters(filteredCardsMap);			
		}
		else 
		{
			cleanFilters(countFilters);
			return orderFilters(countFilters, contentTab);
		}

		if(applyFilters!=null && !applyFilters.isEmpty() && applyFilters.size()==1 && !search)
		{
			applyFilters.keySet().forEach(k -> filters.put(k, countFilters.get(k)));
		}
		setFilterCounts(cardIds,filters,filteredCardsMap,search,contentTab, searchCardIds);		
		cleanFilters(filters);
		
		return orderFilters(filters, contentTab);
	}
	
	private Map<String, Object> orderFilters(final HashMap<String, Object> filters, String contentTab)
	{
		Map<String, Object> orderedFilters = new LinkedHashMap<String,Object>();
		String [] orders = FILTER_CATEGORIES;
		if(contentTab.equals(ROLE_DB_TABLE)) orders = FILTER_CATEGORIES_ROLE;
			
		for(int i=0;i<orders.length;i++)
		{
			String key = orders[i];
			if(filters.containsKey(key)) orderedFilters.put(key, filters.get(key));
		}
		return orderedFilters;
	}
	
	private void cleanFilters(final HashMap<String, Object> filters)
	{	
		LOG.info("All {}",filters);
		if(filters.keySet().contains(SUCCESS_TRACKS_FILTER))//do 2 more times
		{
			HashMap<String, Object> stFilters = (HashMap<String, Object>)filters.get(SUCCESS_TRACKS_FILTER);//ST
			/*
			 * stFilters.forEach((k,v) -> { HashMap<String, Object> ucFilters =
			 * (HashMap<String, Object>)v;//UC removeNulls(ucFilters); //this will remove
			 * null pts and parent uc if has only one null pt });
			 */			
			removeNulls(stFilters);  //this will remove null ucs and parent st if has only one null uc
		}
		Set<String> removeThese = removeNulls(filters);  //all top level
		LOG.info("Removed {} final {}",removeThese, filters);
	}
	
	/* e.g. Tech null 498 */
	private Set<String> removeNulls(final HashMap<String, Object> filters)
	{
		Set<String> removeThese = new HashSet<String>();
		filters.forEach((k,v)-> {
			Map<String, Object> subFilters  = (Map<String, Object>)v;

			Set<String> nulls = new HashSet<String>();
			Set<String>  all = subFilters.keySet();
			all.forEach(ak -> {
				if(ak==null || ak.trim().isEmpty() || ak.trim().equalsIgnoreCase(NULL_TEXT))
					nulls.add(ak);
			});
			nulls.forEach(n-> subFilters.remove(n));
			if(subFilters.size()==0) removeThese.add(k);//remove filter itself

		});
				
		removeThese.forEach(filter->filters.remove(filter));
		return removeThese;
	}

	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake, String searchToken,
			HashMap<String, Object> applyFilters, String sortBy, String sortOrder, String contentTabInp) {
		
		String contentTab =  contentTabInp!=null && contentTabInp.toLowerCase().equals(ROLE_DB_TABLE.toLowerCase())?
				ROLE_DB_TABLE:TECHNOLOGY_DB_TABLE;

		String sort = DEFAULT_SORT_FIELD ; 
		Direction order = DEFAULT_SORT_ORDER ; 		
		if(sortBy!=null  && !sortBy.equalsIgnoreCase("date")) sort = sortBy;
		if(sortOrder!=null && sortOrder.equalsIgnoreCase("asc")) order = Sort.Direction.ASC;		
		LOG.info("sort={} {}",sort, order);
		
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		Set<String> userBookmarks = null;
		if(null != userDetails){userBookmarks = learningDAO.getBookmarks(userDetails.getCecId());}
		
		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);
				
		List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();
		if( searchToken!=null && !searchToken.trim().isEmpty() &&
				applyFilters!=null && !applyFilters.isEmpty()	)
		{
			Set<String> filteredCards = andFilters(filterCards(applyFilters,contentTab));
			if(filteredCards!=null && !filteredCards.isEmpty())
				dbCards = productDocumentationDAO.getAllLearningCardsByFilterSearch(contentTab,filteredCards,"%"+searchToken+"%",Sort.by(order, sort));			
		}
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{
			dbCards = productDocumentationDAO.getAllLearningCardsBySearch(contentTab,"%"+searchToken+"%",Sort.by(order, sort));
		}			
		else if(applyFilters!=null && !applyFilters.isEmpty())
		{
			Set<String> filteredCards = andFilters(filterCards(applyFilters,contentTab));
			if(filteredCards!=null && !filteredCards.isEmpty())
				dbCards = productDocumentationDAO.getAllLearningCardsByFilter(contentTab,filteredCards,Sort.by(order, sort)); 
		}			
		else 
		{
			dbCards=productDocumentationDAO.getAllLearningCards(contentTab,Sort.by(order, sort));
		}			
		
		LOG.info("dbCards={}",dbCards);
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));
		
		sortSpecial(learningCards,sort,order);
		
		return responseModel;
	
	}
	
	void sortSpecial(List<GenericLearningModel> learningCards , String sortBy, Direction order)
	{
		if(sortBy.equalsIgnoreCase("title"))
		{
			learningCards.sort(new SortTitle());
			if(order.isDescending())
				Collections.reverse(learningCards);
		}
	}
	
	class SortTitle implements Comparator<GenericLearningModel>
	{
		@Override
		public int compare(GenericLearningModel o1, GenericLearningModel o2) {	
			String o1Title = o1.getTitle(), o2Title = o2.getTitle();
			
			if(o1Title!=null)
				o1Title = o1.getTitle().trim().replaceAll(regChars, "").toLowerCase();
			
			if(o2Title!=null)
				o2Title = o2.getTitle().trim().replaceAll(regChars, "").toLowerCase();
			
			return o1Title.compareTo(o2Title);			
		}		
	}
	
	//  !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~   and lower must.
	private static final String regChars= "[\\Q!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\\E]";
	
	private static final String TECHNOLOGY_DB_TABLE = "Technology";
	private static final String ROLE_DB_TABLE = "Skill";
	
	/** Preferences **/
	private static final String TIME_INTERVAL_FILTER = "Time Interval";
	private static final Map<String,String>PREFERENCE_FILTER_MAPPING = new HashMap<String,String>(); 
	static {
		PREFERENCE_FILTER_MAPPING.put("role", ROLE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("technology", TECHNOLOGY_FILTER);
		PREFERENCE_FILTER_MAPPING.put("language", LANGUAGE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("region", LIVE_EVENTS_FILTER);
		PREFERENCE_FILTER_MAPPING.put("timeinterval", TIME_INTERVAL_FILTER);
	}
	private static Integer TOP_PICKS_LIMIT = 25;
	private static final String TI_START_TIME = "startTime";
	private static final String TI_END_TIME = "endTime";
	private static final String TI_TIME_ZONE = "timeZone";
	
	private String getUserRole(String userId, String puId)
	{
		String userRole = productDocumentationDAO.getUserRole(userId,puId);
		LOG.info("Role found {}.",userRole);
		return userRole;
	}
	
	/** TOP Picks = my role + my preferences  
	 * @param limit **/
	public LearningRecordsAndFiltersModel fetchMyPreferredLearnings(String userId, String search,
			HashMap<String, Object> filters, String sortBy, String sortOrder, String puid,
			HashMap<String, Object> preferences, Integer limit) {
		String userRole = getUserRole(userId,puid);		
		HashMap<String, Object> prefFilters = new HashMap<String,Object>();	
		if(preferences!=null && preferences.size()>0)
		{
			PREFERENCE_FILTER_MAPPING.keySet().forEach(prefKey ->{
				if(preferences.keySet().contains(prefKey))
				{	
					prefFilters.put(PREFERENCE_FILTER_MAPPING.get(prefKey), preferences.get(prefKey));
				}				
			});			
		}
		if(prefFilters.containsKey(ROLE_FILTER))
		{
			if(!((List)prefFilters.get(ROLE_FILTER)).contains(userRole))((List)prefFilters.get(ROLE_FILTER)).add(userRole);
		}
		else
		{
			List<Object> prefList = new ArrayList<Object>();prefList.add(userRole);
			prefFilters.put(ROLE_FILTER, prefList);
		}	
		LearningRecordsAndFiltersModel allCards= getCards(userId,prefFilters,userRole);
		
		int limitEnd = (topicksLimit == null || topicksLimit < 0)?TOP_PICKS_LIMIT:topicksLimit; 
		prioratizeCards(allCards,DEFAULT_SORT_ORDER);
		randomizeCards(allCards,limitEnd);
		limitCards(allCards, limitEnd);
		
		return allCards;		
	}	


	private Set<String> getPeerViewedCards(String userRole)
	{
		Set<String> peerViewed = new HashSet<String>();
		try
		{
			List<PeerViewedEntity> peerCards = peerViewedRepo.findByRoleName(userRole);
			peerCards.forEach(pv -> peerViewed.add(pv.getCardId()));	
			LOG.info("peer viewed {} {}",peerViewed.size(), peerViewed);
		}
		catch(Exception e)
		{
			LOG.error("Error occurred get peer view",e);
		}

		return peerViewed;
	}
	
	public void addLearningsViewedForRole(String userId,String cardId, String puid) {		
		try
		{
			String userRole = getUserRole(userId,puid);
			PeerViewedEntityPK pvPK = new PeerViewedEntityPK();
			pvPK.setCardId(cardId);
			pvPK.setRoleName(userRole);
			Optional<PeerViewedEntity> peerViewExist = peerViewedRepo.findById(pvPK);
			// record already exists in the table
			if (peerViewExist != null && peerViewExist.isPresent()) {
				PeerViewedEntity dbEntry = peerViewExist.get();
				if(dbEntry!=null){
					dbEntry.setUpdatedTime(Timestamp.valueOf(getNowDateUTCStr()));
				}				
				peerViewedRepo.save(dbEntry);
			}
			else
			{
				PeerViewedEntity newEntry = new PeerViewedEntity();
				newEntry.setCardId(cardId);
				newEntry.setRole_name(userRole);
				newEntry.setUpdatedTime(Timestamp.valueOf(getNowDateUTCStr()));
				peerViewedRepo.save(newEntry);
			}

		}
		catch(Exception e)
		{
			LOG.error("Error occurred save peer view",e);
		}	
	}

	private String getNowDateUTCStr()
	{
		try
		{
			Date nowDate = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat();
			sdf1.applyPattern("yyyy-MM-dd HH:mm:ss");
			sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
			String nowStr = sdf1.format(nowDate);
			LOG.info("sdf1 str:{} ",nowStr);
			//Date nowDateUTC = sdf1.parse(nowStr);return nowDateUTC;			
			return nowStr;
		}
		catch(Exception e)
		{
			LOG.info("Err in nowUTC str",e);
		}
		return null;
	}
	
	private Integer[] getHrsMins(String timeZone)
	{
		int utcStartInd = timeZone.indexOf("UTC");
		Integer hrMin []= new Integer[] {0,0};
		if(utcStartInd>-1)
		{
			int utcTimeHrsStart = utcStartInd+3;
			int utcTimeHrsEnd = timeZone.indexOf(":",utcTimeHrsStart);
			int utcTimeMinuteStart=-1,utcTimeMinuteEnd=-1;
			if(utcTimeHrsEnd == -1) utcTimeHrsEnd = timeZone.length();
			else { utcTimeMinuteStart = utcTimeHrsEnd+1; utcTimeMinuteEnd = timeZone.length();}			
			int hrs3 = Integer.parseInt(timeZone.substring(utcTimeHrsStart, utcTimeHrsEnd));			
			int min3 = utcTimeMinuteStart==-1?0:Integer.parseInt(timeZone.substring(utcTimeMinuteStart, utcTimeMinuteEnd));		
			hrMin[0]=hrs3; hrMin[1]=min3;			
		}
		return hrMin;
	}
	
	private List<String> getRangeLW(List<LearningItemEntity> onlyFutureLWIds, Map<String, String> ddbTI)
	{
		List<String> rangeCardsIds = new ArrayList<String>();
		String startTime = ddbTI.get(TI_START_TIME).trim();
		String endTime = ddbTI.get(TI_END_TIME).trim();
		String timeZone = ddbTI.get(TI_TIME_ZONE).trim();
		//LOG.info("TI:{},{},{}",startTime,endTime, timeZone);
	
		int hrs1 = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
		int min1 = Integer.parseInt(startTime.substring(startTime.indexOf(":")+1, startTime.indexOf(" ")));
		if(startTime.contains("PM")) hrs1=hrs1+12;
		else if (hrs1 == 12) hrs1=0;
		
		int hrs2 = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
		int min2 = Integer.parseInt(endTime.substring(endTime.indexOf(":")+1, endTime.indexOf(" ")));
		if(endTime.contains("PM")) hrs2=hrs2+12;
		else if (hrs2 == 12) hrs2=0;
		//LOG.info("{} {} {} {}",hrs1,min1,hrs2,min2);
		
		Integer hrMin[] = getHrsMins(timeZone);
		int hrs3 = hrMin[0];
		int min3 = hrMin[1];
		if(timeZone.contains("UTC-")) min3 = min3 * -1;		
		
		for(LearningItemEntity futureCard : onlyFutureLWIds)
		{			
			Date date4 = Timestamp.valueOf(futureCard.getSortByDate());	
			int finalHrs = date4.getHours() + hrs3; if(finalHrs<0) finalHrs = finalHrs*-1 -1; if(finalHrs>=24) finalHrs-=24;
			int finalMin = date4.getMinutes() + min3; if(finalMin<0) {finalMin += 60; finalHrs-=1; } if(finalMin>=60) { finalMin-=60;finalHrs+=1;}
			LOG.info("finalHrs {} {} {} {} {} {} {} {} {} {}",futureCard.getLearning_item_id(),hrs1,min1,hrs2,min2, hrs3, min3, date4 , finalHrs, finalMin); 
			if( (finalHrs>hrs1 && finalHrs<hrs2 ) ||
					(finalHrs==hrs1 && finalMin>=min1 ) ||
					(finalHrs==hrs2 && finalMin <= min2 )
				)
			 { rangeCardsIds.add(futureCard.getLearning_item_id());} 
		}		
		return rangeCardsIds; //rangeCards
	}
	
	/** ["{\"endTime\":\"4:00 PM\",\"startTime\":\"9:00 AM\",\"timeZone\":\"PDT(UTC-7)\"}"] 
	 * @param limit **/
	@SuppressWarnings("unchecked")
	private Set<String> getWebinarTimeinterval(List<String> timeInterval)
	{
		Set<String> onlyFutureLWInRange  = new HashSet<String>();
		try
		{
			Map<String,String> ddbTI = new HashMap<String,String>();
			if(timeInterval !=null && timeInterval.size()==1)
			{
				String tiStr = timeInterval.get(0);
				try {					
					Map<String,String> ddbTimeinterval= new ObjectMapper().readValue(tiStr, Map.class);
					ddbTI.putAll(ddbTimeinterval);
				} catch (JsonProcessingException e) {
					LOG.error("Invalid time interval",e);
				}
				if(	ddbTI.get(TI_START_TIME)!=null && !ddbTI.get(TI_START_TIME).trim().isEmpty() &&
						ddbTI.get(TI_END_TIME)!=null && !ddbTI.get(TI_END_TIME).trim().isEmpty() &&
						ddbTI.get(TI_TIME_ZONE)!=null && !ddbTI.get(TI_TIME_ZONE).trim().isEmpty()						
						)
				{
					List<LearningItemEntity>  onlyFutureLWs = new ArrayList<LearningItemEntity>();
					Set<String> onlyFutureLWIds= new HashSet<String>();
					String contentTab = "Preference";
					onlyFutureLWs.addAll(productDocumentationDAO.getUpcomingWebinars(contentTab));
					onlyFutureLWs.forEach(card->onlyFutureLWIds.add(card.getLearning_item_id()));
					LOG.info("onlyFutureLWIds: {} " , onlyFutureLWIds );
					onlyFutureLWInRange.addAll(getRangeLW(onlyFutureLWs,ddbTI));					
					LOG.info("onlyFutureLWInRange {}",onlyFutureLWInRange);									
				}
			}
		}
		catch(Exception e)
		{
			LOG.error("Error occurred processing TI.",e);
		}

		return onlyFutureLWInRange;
	}
	
	private void prioratizeCards(LearningRecordsAndFiltersModel learningCards,Direction order)
	{
		LOG.info("Already prioratize by newer."); //sort date desc		
		sortDateRating(learningCards.getLearningData(),order);		
	}
	

	/** cards already sorted by date add rating **/
	void sortDateRating(List<GenericLearningModel> learningCards, Direction order)
	{
		Collections.sort(learningCards, Comparator.comparing(
				GenericLearningModel::getCreatedTimeStamp,Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(
				GenericLearningModel::getAvgRatingPercentage, Comparator.nullsFirst(Comparator.naturalOrder()))
				);
		if(order.isDescending()) Collections.reverse(learningCards);
	}
	
	private void randomizeCards(LearningRecordsAndFiltersModel learningCards, Integer limitEnd)
	{
		int orgSize = learningCards.getLearningData().size();
		if(orgSize > limitEnd) //25
		{
			int randomNums = orgSize>= limitEnd*2 ? limitEnd/2 : orgSize-limitEnd-1;  //12 or less			
			int boundry = orgSize>= limitEnd*2 ? limitEnd*2 : orgSize; //50 or less
			Set<Integer> randomIndexes = new HashSet<Integer>();
			Random r = new Random();
			//while(randomIndexes.size()<randomNums) ---may take more time
			for(int i=0;i<=randomNums;i++)
			{
				int randomNum = r.nextInt(boundry);
				randomIndexes.add(randomNum);
			}
			LOG.info("Randomly removed {} {}", randomIndexes.size(),randomIndexes);
			
			List<String> allCardIds = new ArrayList<String>();
			List<String> newListCardIds = new ArrayList<String>();
			List<GenericLearningModel> orgList = learningCards.getLearningData();
			List<GenericLearningModel> newList = new ArrayList<GenericLearningModel>();
			for(int i=0;i<boundry;i++ )
			{
				allCardIds.add(orgList.get(i).getRowId());
				if(!randomIndexes.contains(i)) { newList.add(orgList.get(i)); newListCardIds.add(orgList.get(i).getRowId()); }
			}
			learningCards.setLearningData(newList);
			LOG.info(" random org {} {}, new {}{}", allCardIds.size(),allCardIds , newListCardIds.size(), newListCardIds );
		}
	}
	
	private void limitCards(LearningRecordsAndFiltersModel learningCards, Integer limitEnd)
	{		
		if(learningCards!=null && learningCards.getLearningData()!=null && learningCards.getLearningData().size()>0)
		{		
			if(limitEnd > learningCards.getLearningData().size()) limitEnd = learningCards.getLearningData().size();
			List<GenericLearningModel> preferredCards = learningCards.getLearningData().subList(0, limitEnd);
			learningCards.setLearningData(preferredCards);
		}
	}
	
	private Set<String> orPreferences(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<String>();
		
		/** OR **/
		if(!filteredCards.isEmpty())
		{
			filteredCards.forEach((k,v)->{
				cardIds.addAll(v);
			});			
		}
		LOG.info("OR mapped = {} {}",cardIds.size(), cardIds);	
		
		return cardIds;
	}
	
	/** all prefs are OR , no anding **/ 
	private LearningRecordsAndFiltersModel getCards(String userId, HashMap<String, Object> applyFilters, String userRole)
	{
		String contentTab = "Preference";
		String sort = DEFAULT_SORT_FIELD ; 
		Direction order = DEFAULT_SORT_ORDER ;				
		Set<String> userBookmarks = learningDAO.getBookmarks(userId);
		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);				
		List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();
		Map<String, Set<String>> prefCards = new HashMap<String,Set<String>>();
		if(applyFilters!=null && !applyFilters.isEmpty())
		{
			prefCards.putAll(filterCards(applyFilters,contentTab));
		}
		prefCards.put("peerCards",getPeerViewedCards(userRole));
		prefCards.put("tiCards",getWebinarTimeinterval((List<String>) applyFilters.get(TIME_INTERVAL_FILTER)));
		Set<String> filteredCards = orPreferences(prefCards);
		if(filteredCards!=null && !filteredCards.isEmpty())
			dbCards.addAll(productDocumentationDAO.getAllLearningCardsByFilter(contentTab,filteredCards,Sort.by(order, sort)));			
		LOG.info("all OR dbCards= {} {}",dbCards.size());
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));		
		return responseModel;	
	}
	
}


