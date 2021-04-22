package com.cisco.cx.training.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.UserDetails;


@Service
public class ProductDocumentationService{
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private LearningBookmarkDAO learningDAO;
	
	@Autowired
	private PartnerProfileService partnerProfileService;

	@Autowired
	private ProductDocumentationDAO productDocumentationDAO;
	
	private Set<String> filterCards(HashMap<String, Object> applyFilters)
	{	
		LOG.info("applyFilters = {}",applyFilters);
		Set<String> cardIds =  new HashSet<String>();
		if(applyFilters==null || applyFilters.isEmpty()) return cardIds;
		
		HashMap<String, Set<String>> filteredCards = new HashMap<String, Set<String>>();
		
		/** OR **/
		applyFilters.keySet().forEach(k -> {
			Object v = applyFilters.get(k);
			List<String> list;
			if(v instanceof List) {
				list= (List<String>)v;				
				switch(k) {
				case TECHNOLOGY_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByTC(new HashSet<String>(list)));break;
				case DOCUMENTATION_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByAT(new HashSet<String>(list)));break;
				case LIVE_EVENTS_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByRegion(new HashSet<String>(list)));break;
				case CONTENT_TYPE_FILTER : filteredCards.put(k, productDocumentationDAO.getLearningsByContentType(new HashSet<String>(list)));break;
				case LANGUAGE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByLanguage(new HashSet<String>(list)));break;
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
						((Map)iv).keySet().forEach(ivk -> {
							Object ivv = ((Map)iv).get(ivk);
							List<String> ivlist;
							if(ivv instanceof List) 
							{
								ivlist= (List<String>)ivv;
								LOG.info("PS={} uc={} st={}",ivlist,ivk,ik);
								Set<String> pitStops = new HashSet<String>(ivlist);
								String usecase = ivk.toString();
								String successtrack = ik.toString();
								cardIdsStUcPs.addAll(productDocumentationDAO.getCardIdsByPsUcSt(successtrack,usecase,pitStops));
							}						
						});
					}
				});
				filteredCards.put(k,cardIdsStUcPs);
			}
		});
		
		LOG.info("filteredCards = {} ",filteredCards);	

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
	
	//"createdTimeStamp": "2021-04-05 17:10:50.0",card.setCreatedTimeStamp(learning.getUpdated_timestamp().toString());//yyyy-mm-dd hh:mm:ss.fffffffff
	private List<GenericLearningModel>  mapLearningEntityToCards(List<LearningItemEntity> dbList, Set<String> userBookmarks)
	{
		List<GenericLearningModel>  cards = new ArrayList<GenericLearningModel>();
		if(dbList==null || dbList.size()==0) return cards;
		dbList.forEach(learning -> {
			
			GenericLearningModel card =  new GenericLearningModel();	
			
			card.setCreatedTimeStamp(learning.getSortByDate());  //same as created date
			card.setDescription(learning.getDescription());
			card.setDuration(learning.getDuration());
			
			if(null != userBookmarks && !CollectionUtils.isEmpty(userBookmarks)	
					&& userBookmarks.contains(learning.getLearning_item_id())) 
			card.setIsBookMarked(true);
		
			card.setLink(learning.getRegistrationUrl());//learning.getLink()
			card.setStatus(learning.getStatus());
			card.setPresenterName(learning.getPresenterName());
			card.setRowId(learning.getLearning_item_id());
			card.setTitle(learning.getTitle());
			card.setType(learning.getLearning_type());
			card.setRating(learning.getPiw_score());
					
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
			if(!((Map)stMap.get(st)).keySet().contains(uc)) ((Map)stMap.get(st)).put(uc, new HashMap<String,String>());
			if(!((Map)((Map)stMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stMap.get(st)).get(uc)).put(ps, dbValue);
			
			if(stFilter!=null)
			{
				if(!stAllKeysMap.keySet().contains(st)) stAllKeysMap.put(st, new HashMap<String,Map<String,String>>()) ;
				if(!((Map)stAllKeysMap.get(st)).keySet().contains(uc)) ((Map)stAllKeysMap.get(st)).put(uc, new HashMap<String,String>());
				if(!((Map)((Map)stAllKeysMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stAllKeysMap.get(st)).get(uc)).put(ps, "0");
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
	private static final String DOCUMENTATION_FILTER = "Documentation";
	private static final String SUCCESS_TRACKS_FILTER = "Success Tracks";
	private static final String TECHNOLOGY_FILTER = "Technology";
	private static final String FOR_YOU_FILTER = "For You";
	private static final String[] FILTER_CATEGORIES = new String[]{ 
			TECHNOLOGY_FILTER, SUCCESS_TRACKS_FILTER, DOCUMENTATION_FILTER, 
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER};
	
	
	private void initializeFilters(final HashMap<String, Object> filters, final HashMap<String, Object> countFilters)
	{	
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put(CONTENT_TYPE_FILTER, contentTypeFilter);		
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCount();
		Map<String,String> allContentsCT = listToMap(dbListCT);countFilters.put(CONTENT_TYPE_FILTER, allContentsCT);
		allContentsCT.keySet().forEach(k -> contentTypeFilter.put(k, "0"));
		
		HashMap<String, String> technologyFilter = new HashMap<>();
		filters.put(TECHNOLOGY_FILTER, technologyFilter);		
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCount();
		Map<String,String> allContentsTC = listToMap(dbListTC);countFilters.put(TECHNOLOGY_FILTER, allContentsTC);
		allContentsTC.keySet().forEach(k -> technologyFilter.put(k, "0"));
		
		HashMap<String, String> languageFilter = new HashMap<>();
		filters.put(LANGUAGE_FILTER, languageFilter);		
		List<Map<String,Object>> dbListLG= productDocumentationDAO.getAllLanguageWithCount();
		Map<String,String> allContentsLG = listToMap(dbListLG);countFilters.put(LANGUAGE_FILTER, allContentsLG);
		allContentsLG.keySet().forEach(k -> languageFilter.put(k, "0"));
		
		HashMap<String, String> documentationFilter = new HashMap<>();
		filters.put(DOCUMENTATION_FILTER, documentationFilter);		
		List<Map<String,Object>> dbListDC = productDocumentationDAO.getAllDocumentationWithCount();
		Map<String,String> allContentsDC = listToMap(dbListDC);countFilters.put(DOCUMENTATION_FILTER, allContentsDC);
		allContentsDC.keySet().forEach(k -> documentationFilter.put(k, "0"));
		
		HashMap<String, String> regionFilter = new HashMap<>();
		filters.put(LIVE_EVENTS_FILTER, regionFilter);		
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCount();
		Map<String,String> allContentsLE = listToMap(dbListLE);countFilters.put(LIVE_EVENTS_FILTER, allContentsLE);
		allContentsLE.keySet().forEach(k -> regionFilter.put(k, "0"));
		
		//TODO ST
		HashMap<String, Object> stFilter = new HashMap<>();
		filters.put(SUCCESS_TRACKS_FILTER, stFilter);
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcPsWithCount();
		Map<String,Object> allContentsST = listToSTMap(dbListST,stFilter);countFilters.put(SUCCESS_TRACKS_FILTER, allContentsST);
	}

	
	private void setFilterCounts(Set<String> cardIds, final HashMap<String, Object> filters)
	{
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(cardIds);		
		((Map<String,String>)filters.get(CONTENT_TYPE_FILTER)).putAll(listToMap(dbListCT));
		
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(cardIds);		
		((Map<String,String>)filters.get(TECHNOLOGY_FILTER)).putAll(listToMap(dbListTC));
		
		List<Map<String,Object>> dbListLG = productDocumentationDAO.getAllLanguageWithCountByCards(cardIds);		
		((Map<String,String>)filters.get(LANGUAGE_FILTER)).putAll(listToMap(dbListLG));
		
		List<Map<String,Object>> dbListDC = productDocumentationDAO.getAllDocumentationWithCountByCards(cardIds);		
		((Map<String,String>)filters.get(DOCUMENTATION_FILTER)).putAll(listToMap(dbListDC));
		
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(cardIds);		
		((Map<String,String>)filters.get(LIVE_EVENTS_FILTER)).putAll(listToMap(dbListLE));	
		
		//TODO ST
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcPsWithCountByCards(cardIds);		
		((Map<String,Object>)filters.get(SUCCESS_TRACKS_FILTER)).putAll(listToSTMap(dbListST,null));	
	}

	/**
	 * {"Success Tracks":{"Security":{"ABC":["Umbrella"],"Security1":["Anti-Virus","Firewall"]},"Campus Network":{"XYZ":["Use"],"Campus Software Image Management":["Use","Implement"]}}}
	 * {"Language":["English","Japanese"],"Content Type":["PDF","Video"],"Success Tracks":{"Security":{"ABC":["Umbrella"],"Security1":["Anti-Virus","Firewall"]},"Campus Network":{"XYZ":["Use"],"CSIM":["Onboard","Implement"]}}}
	 * @param searchToken
	 * @param applyFilters
	 * @return
	 */
	public HashMap<String, Object> getAllLearningFilters(String searchToken, HashMap<String, Object> applyFilters) 
	{
		HashMap<String, Object> filters = new HashMap<>();	
		HashMap<String, Object> countFilters = new HashMap<>();
		
		initializeFilters(filters,countFilters);
		
		Set<String> cardIds =  new HashSet<String>();
				
		if( searchToken!=null && !searchToken.trim().isEmpty() && applyFilters!=null && !applyFilters.isEmpty()	)
		{
			Set<String> filteredCards = filterCards(applyFilters);
			cardIds = productDocumentationDAO.getAllLearningCardIdsByFilterSearch(filteredCards,"%"+searchToken+"%");		
		}		
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{			
				cardIds = productDocumentationDAO.getAllLearningCardIdsBySearch("%"+searchToken+"%");
		}
		else if(applyFilters!=null && !applyFilters.isEmpty()) {
			cardIds = filterCards(applyFilters);
		}
		else {
			return countFilters;
		}

		setFilterCounts(cardIds,filters);
		
		return filters;
	}

	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake, String searchToken,
			HashMap<String, Object> applyFilters, String sortBy, String sortOrder) {

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
			Set<String> filteredCards = filterCards(applyFilters);
			if(filteredCards!=null && !filteredCards.isEmpty())
				dbCards = productDocumentationDAO.getAllLearningCardsByFilterSearch(filteredCards,"%"+searchToken+"%",Sort.by(order, sort));			
		}
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{
			dbCards = productDocumentationDAO.getAllLearningCardsBySearch("%"+searchToken+"%",Sort.by(order, sort));
		}			
		else if(applyFilters!=null && !applyFilters.isEmpty())
		{
			Set<String> filteredCards = filterCards(applyFilters);
			if(filteredCards!=null && !filteredCards.isEmpty())
				dbCards = productDocumentationDAO.getAllLearningCardsByFilter(filteredCards,Sort.by(order, sort)); 
		}			
		else 
		{
			dbCards=productDocumentationDAO.getAllLearningCards(Sort.by(order, sort));
		}			
		
		LOG.info("dbCards={}",dbCards);
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));
		
		
		return responseModel;
	
	}
	
}



