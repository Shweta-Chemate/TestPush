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
public class ProductDocumentationService{
	private static final Logger LOG = LoggerFactory.getLogger(ProductDocumentationService.class);

	@Autowired
	private LearningBookmarkDAO learningDAO;

	@Autowired
	private ProductDocumentationDAO productDocumentationDAO;

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Autowired
	private PeerViewedRepo peerViewedRepo;
	
	@Autowired
	private HttpServletRequest httpServletRequest;
	
	@Autowired
	private TPService tpService;

	@Value("${top.picks.learnings.display.limit}")
	public Integer topicksLimit;

	private Random r = new Random();  //NOSONAR
	
	@Autowired
	private SplitClientService splitService;

	private Map<String, Set<String>> filterCards(Map<String, Object> applyFilters, String contentTab, String hcaasStatus)
	{	
		LOG.info("applyFilters = {}",applyFilters);	
		Map<String, Set<String>> filteredCards = new HashMap<>();
		if(applyFilters==null || applyFilters.isEmpty()) {return filteredCards;}

		/** OR **/
		applyFilters.keySet().forEach(k -> {
			Object v = applyFilters.get(k);
			List<String> list;
			if(v instanceof List) {
				list= (List<String>)v;				
				switch(k) {
				case TECHNOLOGY_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByTC(contentTab,new HashSet<>(list), hcaasStatus));break;
				//case DOCUMENTATION_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByAT(contentTab,new HashSet<String>(list)));break; //NOSONAR
				case LIVE_EVENTS_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByRegion(contentTab,new HashSet<>(list), hcaasStatus));break;
				case CONTENT_TYPE_FILTER : filteredCards.put(k, productDocumentationDAO.getLearningsByContentType(contentTab,new HashSet<>(list), hcaasStatus));break;
				case LANGUAGE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByLanguage(contentTab,new HashSet<>(list), hcaasStatus));break;
				case FOR_YOU_FILTER : filteredCards.put(k, getCardIdsByYou(contentTab,new HashSet<>(list), hcaasStatus));break;
				case ROLE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByRole(contentTab, new HashSet<>(list), hcaasStatus));break;
				case LIFECYCLE_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByPsUcSt(contentTab,new HashSet<>(list), hcaasStatus));break;
				case Constants.SPECIALIZATION_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsBySpecialization(new HashSet<>(list)));break;
				case Constants.CISCO_PLUS_FILTER : filteredCards.put(k, productDocumentationDAO.getCardIdsByCiscoPlus(contentTab, new HashSet<>(list), hcaasStatus));break;
				default : LOG.info("other {}={}",k,list);
				};
			}
			else if ( v instanceof Map) {	
				Set<String> cardIdsStUcPs = new HashSet<>();
				((Map) v).keySet().forEach(ik->{
					Object iv = ((Map)v).get(ik);
					List<String> ilist;
					if(iv instanceof Map) {
						Set<String> usecaseS= ((Map) iv).keySet(); String successtrack = ik.toString();
						cardIdsStUcPs.addAll(productDocumentationDAO.getCardIdsByPsUcSt(contentTab,successtrack,usecaseS, hcaasStatus));
					}
				});
				filteredCards.put(k,cardIdsStUcPs);
			}
		});

		LOG.info("filteredCards = {} {} ",filteredCards.size(), filteredCards);	
		return filteredCards;

	}

	private Map<String,String> getLearningMapCounts()
	{
		List<Map<String, Object>> dbList = productDocumentationDAO.getLearningMapCounts();
		Map<String, String> lmCounts = ProductDocumentationUtil.listToMap(dbList);//LOG.info("lmCounts={}",lmCounts);
		return lmCounts;
	}

	//"createdTimeStamp": "2021-04-05 17:10:50.0",card.setCreatedTimeStamp(learning.getUpdated_timestamp().toString());//yyyy-mm-dd hh:mm:ss.fffffffff
	protected List<GenericLearningModel>  mapLearningEntityToCards(List<LearningItemEntity> dbList, Set<String> userBookmarks)
	{

		Map<String, String> lmCounts = getLearningMapCounts();
		List<GenericLearningModel>  cards = new ArrayList<>();
		if(dbList==null || dbList.size()==0) {return cards;}
		dbList.forEach(learning -> {

			GenericLearningModel card =  new GenericLearningModel();	
			if(learning.getSortByDate()==null){card.setCreatedTimeStamp(null);}
			else {card.setCreatedTimeStamp(Timestamp.valueOf(learning.getSortByDate()).toInstant().toString());}  //same as created date
			card.setDescription(learning.getDescription());
			card.setDuration(learning.getDuration());

			if(!CollectionUtils.isEmpty(userBookmarks) && userBookmarks.contains(learning.getLearning_item_id()))
			{
				card.setIsBookMarked(true);	
			}		

			//card.setLink(learning.getRegistrationUrl());//learning.getLink()
			card.setStatus(learning.getStatus());
			card.setPresenterName(learning.getPresenterName());
			card.setRowId(learning.getLearning_item_id());card.setId(learning.getLearning_item_id());
			card.setTitle(learning.getTitle());
			card.setType(learning.getLearning_type());
			card.setRating(learning.getPiw_score());
			card.setSpecialization(learning.getSpecialization());

			card.setRegistrationUrl(learning.getRegistrationUrl());
			card.setRecordingUrl(learning.getRecordingUrl());

			card.setLink(learning.getAsset_links());
			card.setContentType(learning.getAsset_types());
			card.setUrlDescrption(learning.getAsset_description());
			
			if(learning.getLearning_type().equals(Constants.SUCCESSTIPS)
					&& StringUtils.isNotBlank(learning.getAsset_types())) {
				List<SuccessTipsAttachment> videoAttachments = new ArrayList<>();
				List<SuccessTipsAttachment> fileAttachments = new ArrayList<>();
				String[] asset_types = learning.getAsset_types().split(",");
				String[] asset_links = learning.getAsset_links().split(",");
				String[] asset_description = learning.getAsset_description().split(":");
				String[] asset_titles = learning.getAsset_titles().split(":");
				for(int i=0 ;i<asset_types.length;i++) {
					SuccessTipsAttachment successTipAttac = new SuccessTipsAttachment();
					successTipAttac.setAttachmentType(asset_types[i]);
					if(i < asset_links.length) {
						successTipAttac.setUrl(asset_links[i]);
					}else {
						successTipAttac.setUrl("");
					}
					if(asset_description.length == 0) {
						successTipAttac.setUrlDescription("");
					}else {
						if((i+1) > asset_description.length) {
							successTipAttac.setUrlDescription("");
						}else {
							successTipAttac.setUrlDescription(asset_description[i]);
						}
					}
					if(i < asset_titles.length) {
						successTipAttac.setUrlTitle(asset_titles[i]);
					}else {
						successTipAttac.setUrlTitle("");
					}
					if(asset_types[i].equalsIgnoreCase(Constants.SUCCESS_TIPS_VIDEO)) {
						videoAttachments.add(successTipAttac);
					}else {
						fileAttachments.add(successTipAttac);
					}
				}
				if(!videoAttachments.isEmpty()) {
					card.setSuccessTipsVideos(videoAttachments);
				}
				if(!fileAttachments.isEmpty()) {
					card.setSuccessTipsFiles(fileAttachments);
				}
			}
			
			card.setAvgRatingPercentage(learning.getAvgRatingPercentage());
			card.setTotalCompletions(learning.getTotalCompletions());
			card.setVotesPercentage(learning.getVotesPercentage());

			card.setLearning_map(learning.getLearning_map());
			if(LEARNING_MAP_TYPE.equals(learning.getLearning_type())
					&& lmCounts.containsKey(learning.getLearning_item_id()))
			{
				card.setModulecount(lmCounts.get(learning.getLearning_item_id()));
			}
			if(Constants.SUCCESSTIPS.equalsIgnoreCase(learning.getLearning_type())) {
				if(splitService.getSplitValue(Constants.SUCCESS_TIPS_SPLIT_KEY)) {
					cards.add(card);
				}
			}else {
				cards.add(card);
			}
		});
		return cards;
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
	private static final String CISCOPLUS_FILTER = "Cisco+";
	private static final String[] FILTER_CATEGORIES = new String[]{
			CISCOPLUS_FILTER, SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER, //DOCUMENTATION_FILTER,
			ROLE_FILTER, 
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER};

	private static final String[] FILTER_CATEGORIES_ROLE = new String[]{ 
			ROLE_FILTER, CISCOPLUS_FILTER, SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER,
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER, CISCOPLUS_FILTER};

	private static final String[] FILTER_CATEGORIES_TOPPICKS = new String[]{ ROLE_FILTER, //CISCOPLUS_FILTER, 
			SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER,
			LIVE_EVENTS_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER};
	
	private static final String[] FOR_YOU_KEYS = new String[]{"New","Top Picks","Based on Your Customers",
			"Bookmarked","Popular with Partners"};

	/** nulls **/
	private static final String NULL_TEXT = "null";

	/** lmap **/
	private static final String LEARNING_MAP_TYPE = "learningmap";

	private void initializeFilters(final HashMap<String, Object> filters, final HashMap<String, Object> countFilters, String contentTab, String hcaasStatus)
	{	
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put(CONTENT_TYPE_FILTER, contentTypeFilter);		
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsCT = ProductDocumentationUtil.listToMap(dbListCT);countFilters.put(CONTENT_TYPE_FILTER, allContentsCT);
		allContentsCT.keySet().forEach(k -> contentTypeFilter.put(k, "0"));

		HashMap<String, String> technologyFilter = new HashMap<>();
		filters.put(TECHNOLOGY_FILTER, technologyFilter);		
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsTC = ProductDocumentationUtil.listToMap(dbListTC);countFilters.put(TECHNOLOGY_FILTER, allContentsTC);
		allContentsTC.keySet().forEach(k -> technologyFilter.put(k, "0"));

		HashMap<String, String> languageFilter = new HashMap<>();
		filters.put(LANGUAGE_FILTER, languageFilter);		
		List<Map<String,Object>> dbListLG= productDocumentationDAO.getAllLanguageWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsLG = ProductDocumentationUtil.listToMap(dbListLG);countFilters.put(LANGUAGE_FILTER, allContentsLG);
		allContentsLG.keySet().forEach(k -> languageFilter.put(k, "0"));

		HashMap<String, String> regionFilter = new HashMap<>();
		filters.put(LIVE_EVENTS_FILTER, regionFilter);		
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsLE = ProductDocumentationUtil.listToMap(dbListLE);countFilters.put(LIVE_EVENTS_FILTER, allContentsLE);
		allContentsLE.keySet().forEach(k -> regionFilter.put(k, "0"));
		
		//removed documentation filter

		HashMap<String, Object> stFilter = new HashMap<>();
		filters.put(SUCCESS_TRACKS_FILTER, stFilter);
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCount(contentTab, hcaasStatus);//productDocumentationDAO.getAllStUcPsWithCount(contentTab);
		Map<String,Object> allContentsST = ProductDocumentationUtil.listToSTMap(dbListST,stFilter);countFilters.put(SUCCESS_TRACKS_FILTER, allContentsST);

		HashMap<String, Object> lcFilter = new HashMap<>();
		filters.put(LIFECYCLE_FILTER, lcFilter);
		List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPsWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsLC = ProductDocumentationUtil.listToMap(dbListLC);countFilters.put(LIFECYCLE_FILTER, allContentsLC);
		allContentsLC.keySet().forEach(k -> lcFilter.put(k, "0"));

		HashMap<String, Object> youFilter = new HashMap<>();
		filters.put(FOR_YOU_FILTER, youFilter);		
		Map<String,String> allContentsYou = getForYouCounts(contentTab,null, hcaasStatus);countFilters.put(FOR_YOU_FILTER, allContentsYou);
		allContentsYou.keySet().forEach(k -> youFilter.put(k, "0"));

		HashMap<String, String> roleFilter = new HashMap<>();
		filters.put(ROLE_FILTER, roleFilter);		
		List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsRole = ProductDocumentationUtil.listToMap(dbListRole);countFilters.put(ROLE_FILTER, allContentsRole);
		allContentsRole.keySet().forEach(k -> roleFilter.put(k, "0"));

		HashMap<String, String> ciscoPlusFilter = new HashMap<>();
		filters.put(CISCOPLUS_FILTER, ciscoPlusFilter);
		List<Map<String,Object>> dbListCiscoPlus = productDocumentationDAO.getAllCiscoPlusWithCount(contentTab, hcaasStatus);
		Map<String,String> allContentsCiscoPlus = ProductDocumentationUtil.listToMap(dbListCiscoPlus);
		countFilters.put(CISCOPLUS_FILTER, allContentsCiscoPlus);
		allContentsCiscoPlus.keySet().forEach(k -> ciscoPlusFilter.put(k, "0"));
	}

	private Set<String> getCardIdsByYou(String contentTab, HashSet<String> youList, String hcaasStatus)
	{
		final Set<String> cardIds = new HashSet<>();
		youList.forEach(youKey ->
		{
			if(Arrays.asList(FOR_YOU_KEYS).contains(youKey))
			{
				if(youKey.equals(FOR_YOU_KEYS[0])) //New
				{
					final Set<String> cardIdsNew = new HashSet<>();
					List<NewLearningContentEntity> result = learningContentRepo.findNew(hcaasStatus);
					result.forEach(card -> cardIdsNew.add(card.getId()));
					if(!cardIdsNew.isEmpty()) {
						cardIds.addAll(productDocumentationDAO.getAllNewCardIdsByCards(contentTab, cardIdsNew, hcaasStatus));}
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

	private Map<String,String>  getForYouCounts(String contentTab, Set<String>cardIds, String hcaasStatus)
	{
		Map<String,String> youMap = new HashMap<>();

		//1. New
		List<NewLearningContentEntity> result = learningContentRepo.findNew(hcaasStatus);//1 month as of now
		if(result == null) {youMap.put(FOR_YOU_KEYS[0], "0");} 
		else 
		{
			Set<String> cardIdsNew = new HashSet<>();
			Set<String> dbSet = new HashSet<>();
			result.forEach(card -> cardIdsNew.add(card.getId()));
			dbSet.addAll(productDocumentationDAO.getAllNewCardIdsByCards(contentTab, cardIdsNew, hcaasStatus));

			if(cardIds!=null) // && !cardIds.isEmpty() -- set can be empty here
			{							
				dbSet.retainAll(cardIds);
			}

			youMap.put(FOR_YOU_KEYS[0], String.valueOf(dbSet.size()));

		}

		//2. Bookmarked

		return youMap;
	}

	protected void setFilterCounts(Set<String> cardIdsInp, final HashMap<String, Object> filters, 
			Map<String, Set<String>> filteredCardsMap, boolean search, String contentTab, Set<String> searchCardIds, String hcaasStatus)
	{
		LOG.info("filteredCardsMap={}",filteredCardsMap);
		if(filteredCardsMap ==null || filteredCardsMap.isEmpty() || (filteredCardsMap.size()==1 && search) )  //only search
		{
			setFilterCounts(cardIdsInp, filters,contentTab, filteredCardsMap,search, searchCardIds, hcaasStatus);
		}			
		else
		{
			Set<String> cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,CONTENT_TYPE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(CONTENT_TYPE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListCT));

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,TECHNOLOGY_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(TECHNOLOGY_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListTC));

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,LANGUAGE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLG = productDocumentationDAO.getAllLanguageWithCountByCards(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(LANGUAGE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLG));

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,LIVE_EVENTS_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(LIVE_EVENTS_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLE));	

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,SUCCESS_TRACKS_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCountByCards(contentTab,cardIds, hcaasStatus);//productDocumentationDAO.getAllStUcPsWithCountByCards(contentTab,cardIds);
			Map<String,Object> filterAndCountsFromDb = ProductDocumentationUtil.listToSTMap(dbListST,null);
			mergeSTFilterCounts(filters,filterAndCountsFromDb);

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,LIFECYCLE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPitstopsWithCountByCards(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(LIFECYCLE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLC));

			if(!TOPPICKS.equals(contentTab)) {
			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,FOR_YOU_FILTER,searchCardIds,search);
			Map<String,String> dbMapYou = getForYouCounts(contentTab,cardIds, hcaasStatus);
			((Map<String,String>)filters.get(FOR_YOU_FILTER)).putAll(dbMapYou);	}			

			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,ROLE_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCountByCards(contentTab, cardIds, hcaasStatus);
			((Map<String,String>)filters.get(ROLE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListRole));

			if(!TOPPICKS.equals(contentTab)) {
			cardIds = ProductDocumentationUtil.andFiltersWithExcludeKey(filteredCardsMap,CISCOPLUS_FILTER,searchCardIds,search);
			List<Map<String,Object>> dbListCiscoPlus = productDocumentationDAO.getAllCiscoPlusWithCountByCards(contentTab, cardIds, hcaasStatus);
			((Map<String,String>)filters.get(CISCOPLUS_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListCiscoPlus));}
		}		
	}

	private void setFilterCounts(Set<String> cardIdsInp, final HashMap<String, Object> filters, String contentTab, 
			Map<String, Set<String>> filteredCardsMap, boolean search, Set<String> searchCardIdsInp, String hcaasStatus)
	{
		Set<String> cardIds = new HashSet<>();
		cardIds.addAll(cardIdsInp);
		Set<String> searchCardIds = new HashSet<>();
		searchCardIds.addAll(searchCardIdsInp);

		if(search && filteredCardsMap.containsKey(CONTENT_TYPE_FILTER)) {cardIds = searchCardIds;}  
		List<Map<String,Object>> dbListCT = productDocumentationDAO.getAllContentTypeWithCountByCards(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(CONTENT_TYPE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListCT));

		if(search && filteredCardsMap.containsKey(TECHNOLOGY_FILTER)) {cardIds = searchCardIds;} else {cardIds = cardIdsInp;} 
		List<Map<String,Object>> dbListTC = productDocumentationDAO.getAllTechnologyWithCountByCards(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(TECHNOLOGY_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListTC));

		if(search && filteredCardsMap.containsKey(LANGUAGE_FILTER)) {cardIds = searchCardIds; } else {cardIds = cardIdsInp;}
		List<Map<String,Object>> dbListLG = productDocumentationDAO.getAllLanguageWithCountByCards(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(LANGUAGE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLG));

		if(search && filteredCardsMap.containsKey(LIVE_EVENTS_FILTER)) { cardIds = searchCardIds; }else {cardIds = cardIdsInp;}
		List<Map<String,Object>> dbListLE = productDocumentationDAO.getAllLiveEventsWithCountByCards(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(LIVE_EVENTS_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLE));	

		if(search && filteredCardsMap.containsKey(SUCCESS_TRACKS_FILTER)) {cardIds = searchCardIds; }else {cardIds = cardIdsInp;}
		List<Map<String,Object>> dbListST = productDocumentationDAO.getAllStUcWithCountByCards(contentTab,cardIds, hcaasStatus);//productDocumentationDAO.getAllStUcPsWithCountByCards(contentTab,cardIds);
		Map<String,Object> filterAndCountsFromDb = ProductDocumentationUtil.listToSTMap(dbListST,null);
		mergeSTFilterCounts(filters,filterAndCountsFromDb);

		if(search && filteredCardsMap.containsKey(LIFECYCLE_FILTER)) {cardIds = searchCardIds;} else {cardIds = cardIdsInp;	}
		List<Map<String,Object>> dbListLC = productDocumentationDAO.getAllPitstopsWithCountByCards(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(LIFECYCLE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListLC));		

		if(!TOPPICKS.equals(contentTab)) {
		if(search && filteredCardsMap.containsKey(FOR_YOU_FILTER)) {cardIds = searchCardIds;} else {cardIds = cardIdsInp;}
		Map<String,String> dbMapYou = getForYouCounts(contentTab,cardIds, hcaasStatus);
		((Map<String,String>)filters.get(FOR_YOU_FILTER)).putAll(dbMapYou); }

		if(search && filteredCardsMap.containsKey(ROLE_FILTER)) {cardIds = searchCardIds;} else {cardIds = cardIdsInp;}
		List<Map<String,Object>> dbListRole = productDocumentationDAO.getAllRoleWithCountByCards(contentTab, cardIds, hcaasStatus);
		((Map<String,String>)filters.get(ROLE_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListRole));

		if(!TOPPICKS.equals(contentTab)) {
		if(search && filteredCardsMap.containsKey(CISCOPLUS_FILTER)) {cardIds = searchCardIds;} else {cardIds = cardIdsInp;}
		List<Map<String,Object>> dbListCiscoPlus = productDocumentationDAO.getAllCiscoPlusWithCountByCards(contentTab, cardIds, hcaasStatus);
		((Map<String,String>)filters.get(CISCOPLUS_FILTER)).putAll(ProductDocumentationUtil.listToMap(dbListCiscoPlus)); }

	}

	private void mergeSTFilterCounts(Map<String,Object> filters , Map<String,Object> filterAndCountsFromDb) {
		Map<String,Object> stFilters = ((Map<String,Object>)filters.get(SUCCESS_TRACKS_FILTER));
		for(Entry<String, Object> entry : filterAndCountsFromDb.entrySet()) {
			String stkey = entry.getKey();
			if(stFilters.containsKey(stkey)) {
				Map<String,Object> stFilter = (Map<String,Object>)stFilters.get(stkey);
				Map<String,Object> stFilterFromDB = (Map<String,Object>)entry.getValue();
				for(Entry<String, Object> useCaseEntry : stFilterFromDB.entrySet()) {
					String useCaseKey = useCaseEntry.getKey();
					if(stFilter.containsKey(useCaseKey)) {  //NOSONAR
						stFilter.put(useCaseKey, useCaseEntry.getValue()); //addition
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
	public Map<String, Object> getAllLearningFilters(String searchToken, Map<String, Object> applyFilters, String contentTabInp, boolean hcaasStatusFlag)
	{		
		String hcaasStatus = String.valueOf(hcaasStatusFlag);
		String contentTab =  contentTabInp!=null && contentTabInp.toLowerCase().equals(ROLE_DB_TABLE.toLowerCase())?
				ROLE_DB_TABLE:TECHNOLOGY_DB_TABLE;

		HashMap<String, Object> filters = new HashMap<>();	
		HashMap<String, Object> countFilters = new HashMap<>();

		initializeFilters(filters,countFilters,contentTab, hcaasStatus);

		Set<String> cardIds =  new HashSet<String>(); //NOSONAR
		Map<String, Set<String>> filteredCardsMap = new HashMap<>();
		boolean search=false;
		Set<String> searchCardIds =  new HashSet<>();

		if( searchToken!=null && !searchToken.trim().isEmpty() && applyFilters!=null && !applyFilters.isEmpty()	)
		{
			search = true;
			filteredCardsMap = filterCards(applyFilters,contentTab, hcaasStatus);
			Set<String> filteredCards = ProductDocumentationUtil.andFilters(filteredCardsMap);
			cardIds = productDocumentationDAO.getAllLearningCardIdsByFilterSearch(contentTab,filteredCards,"%"+searchToken+"%", hcaasStatus);
			searchCardIds = productDocumentationDAO.getAllLearningCardIdsBySearch(contentTab,"%"+searchToken+"%", hcaasStatus);
		}		
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{			
			search = true;
			cardIds = productDocumentationDAO.getAllLearningCardIdsBySearch(contentTab,"%"+searchToken+"%", hcaasStatus);
		}
		else if(applyFilters!=null && !applyFilters.isEmpty())
		{
			filteredCardsMap = filterCards(applyFilters,contentTab, hcaasStatus);
			cardIds = ProductDocumentationUtil.andFilters(filteredCardsMap);			
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
		setFilterCounts(cardIds,filters,filteredCardsMap,search,contentTab, searchCardIds, hcaasStatus);
		cleanFilters(filters);

		return orderFilters(filters, contentTab);
	}

	protected Map<String, Object> orderFilters(final HashMap<String, Object> filters, String contentTab)
	{
		Map<String, Object> orderedFilters = new LinkedHashMap<>();
		String [] orders = FILTER_CATEGORIES;
		if(contentTab.equals(ROLE_DB_TABLE)) {orders = FILTER_CATEGORIES_ROLE;}
		if(contentTab.equals(TOPPICKS)) {orders = FILTER_CATEGORIES_TOPPICKS;}
		
		for(int i=0;i<orders.length;i++)
		{
			String key = orders[i];
			if(filters.containsKey(key)) {orderedFilters.put(key, filters.get(key));}
		}
		return orderedFilters;
	}

	protected void cleanFilters(final HashMap<String, Object> filters)
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
			if(subFilters==null) {removeThese.add(k);}//remove filter itself
			else {
			Set<String> nulls = new HashSet<>();
			Set<String>  all = subFilters.keySet();
			all.forEach(ak -> {
				if(ak==null || ak.trim().isEmpty() || ak.trim().equalsIgnoreCase(NULL_TEXT)) {
					nulls.add(ak);}
			});
			nulls.forEach(n-> subFilters.remove(n));
			if(subFilters.size()==0) {removeThese.add(k);}//remove filter itself
			}
		});

		removeThese.forEach(filter->filters.remove(filter));
		return removeThese;
	}

	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake, String searchToken,
			HashMap<String, Object> applyFilters, String sortBy, String sortOrder, String contentTabInp, boolean hcaasStatusFlag) {
		String hcaasStatus = String.valueOf(hcaasStatusFlag);

		String contentTab =  contentTabInp!=null && contentTabInp.toLowerCase().equals(ROLE_DB_TABLE.toLowerCase())?
				ROLE_DB_TABLE:TECHNOLOGY_DB_TABLE;

		String sort = DEFAULT_SORT_FIELD ; 
		Direction order = DEFAULT_SORT_ORDER ; 		

		if(sortOrder!=null && sortOrder.equalsIgnoreCase("asc")) { order = Sort.Direction.ASC;}		
		LOG.info("sort={} {}",sort, order);

		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		Set<String> userBookmarks = learningDAO.getBookmarks(userId);

		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);

		List<LearningItemEntity> dbCards = new ArrayList<>();
		if( searchToken!=null && !searchToken.trim().isEmpty() &&
				applyFilters!=null && !applyFilters.isEmpty()	)
		{
			Set<String> filteredCards = ProductDocumentationUtil.andFilters(filterCards(applyFilters,contentTab, hcaasStatus));
			if(filteredCards!=null && !filteredCards.isEmpty()) {
				dbCards = productDocumentationDAO.getAllLearningCardsByFilterSearch(contentTab,filteredCards,"%"+searchToken+"%",Sort.by(order, sort), hcaasStatus);}
		}
		else if(searchToken!=null && !searchToken.trim().isEmpty())
		{
			dbCards = productDocumentationDAO.getAllLearningCardsBySearch(contentTab,"%"+searchToken+"%",Sort.by(order, sort), hcaasStatus);
		}			
		else if(applyFilters!=null && !applyFilters.isEmpty())
		{
			Set<String> filteredCards = ProductDocumentationUtil.andFilters(filterCards(applyFilters,contentTab, hcaasStatus));
			if(filteredCards!=null && !filteredCards.isEmpty()) {
				dbCards = productDocumentationDAO.getAllLearningCardsByFilter(contentTab,filteredCards,Sort.by(order, sort), hcaasStatus); }
		}			
		else 
		{
			dbCards=productDocumentationDAO.getAllLearningCards(contentTab,Sort.by(order, sort), hcaasStatus);
		}			

		LOG.info("dbCards={}",dbCards);
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));

		sortSpecial(learningCards,sortBy,order);

		return responseModel;

	}

	void sortSpecial(List<GenericLearningModel> learningCards , String sortBy, Direction order)
	{
		if(sortBy!=null && sortBy.trim().equalsIgnoreCase("title"))
		{
			learningCards.sort(new SortTitle());
			if(order.isDescending()) {
				Collections.reverse(learningCards);}
		}
	}

	class SortTitle implements Comparator<GenericLearningModel>
	{
		@Override
		public int compare(GenericLearningModel o1, GenericLearningModel o2) {	
			String o1Title = o1.getTitle(), o2Title = o2.getTitle();

			if(o1Title!=null) {
				o1Title = o1.getTitle().trim().replaceAll(REG_CHARS, "").toLowerCase();}
			else {o1Title="";}

			if(o2Title!=null) {
				o2Title = o2.getTitle().trim().replaceAll(REG_CHARS, "").toLowerCase();}

			return o1Title.compareTo(o2Title);			
		}		
	}

	//  !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~   and lower must.
	private static final String REG_CHARS= "[\\Q!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\\E]";

	private static final String TECHNOLOGY_DB_TABLE = "Technology";
	private static final String ROLE_DB_TABLE = "Skill";
	private static final String TOPPICKS = "Toppicks";
	
	/** Preferences **/
	private static final String TIME_INTERVAL_FILTER = "Time Interval";
	private static final Map<String,String>PREFERENCE_FILTER_MAPPING = new HashMap<>(); 
	static {
		PREFERENCE_FILTER_MAPPING.put("role", ROLE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("technology", TECHNOLOGY_FILTER);
		PREFERENCE_FILTER_MAPPING.put("language", LANGUAGE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("region", LIVE_EVENTS_FILTER);
		PREFERENCE_FILTER_MAPPING.put("timeinterval", TIME_INTERVAL_FILTER);
		PREFERENCE_FILTER_MAPPING.put("specialization", Constants.SPECIALIZATION_FILTER);

	}
	private static final Integer TOP_PICKS_LIMIT = 25;
	private static final String TI_START_TIME = "startTime";
	private static final String TI_END_TIME = "endTime";
	private static final String TI_TIME_ZONE = "timeZone";

	private static final int TWENTY_FOUR = 24;
	private static final int TWELVE = 12;
	private static final int TWO = 2;	
	private static final int SIXTY= 60;
	private static final String PM = "PM";
	private static final String UTC_MINUS = "UTC-";

	private String getUserRole()
	{
		long requestStartTime = System.currentTimeMillis();
		String userRoleId = (String) httpServletRequest.getServletContext().getAttribute(Constants.ROLE_ID);
		String userRole = productDocumentationDAO.getUserRole(userRoleId);
		LOG.info("PD-Role found {} {} in {}.",userRoleId, userRole, (System.currentTimeMillis() - requestStartTime));
		
		return userRole;
	}

	/** TOP Picks = my role + my preferences  
	 * @param limit **/
	public LearningRecordsAndFiltersModel fetchMyPreferredLearnings(String userId, //NOSONAR
			HashMap<String, Object> filters, String puid,			//NOSONAR
			HashMap<String, Object> preferences, Integer limit, boolean hcaasStatusFlag) {
		String hcaasStatus = String.valueOf(hcaasStatusFlag);
		String userRole = getUserRole();
		HashMap<String, Object> prefFilters = new HashMap<>();	
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
			if(!((List)prefFilters.get(ROLE_FILTER)).contains(userRole)) {((List)prefFilters.get(ROLE_FILTER)).add(userRole);}
		}
		else
		{
			List<Object> prefList = new ArrayList<>();prefList.add(userRole);
			prefFilters.put(ROLE_FILTER, prefList);
		}	
		LearningRecordsAndFiltersModel allCards= getCards(userId,prefFilters,userRole, hcaasStatus);

		int limitEnd = (topicksLimit == null || topicksLimit < 0)?TOP_PICKS_LIMIT:topicksLimit; 
		prioratizeCards(allCards,DEFAULT_SORT_ORDER);
		int specializedEndIndex = getSpecializedEndIndex(allCards);
		LOG.info("specialization index :: {}", specializedEndIndex);
		randomizeCards(allCards,limitEnd,specializedEndIndex);
		limitCards(allCards, limitEnd);

		return allCards;		
	}

	int getSpecializedEndIndex(LearningRecordsAndFiltersModel allCards) {
		int index = allCards.getLearningData().stream().filter(model -> {
			return model.getSpecialization()!=null;
		}).collect(Collectors.toList()).size();
		return index;
	}

	private Set<String> getPeerViewedCards(String userRole)
	{
		long requestStartTime = System.currentTimeMillis();	
		Set<String> peerViewed = new HashSet<>();
		try
		{
			List<PeerViewedEntity> peerCards = peerViewedRepo.findByRoleName(userRole);
			peerCards.forEach(pv -> peerViewed.add(pv.getCardId()));	
			LOG.info("PD-peer viewed {} {} in {} ",peerViewed.size(), peerViewed, (System.currentTimeMillis() - requestStartTime));
		}
		catch(Exception e)
		{
			LOG.error("Error occurred get peer view",e);
		}

		return peerViewed;
	}

	public void addLearningsViewedForRole(String userId,String cardId, String puid) {	
		LOG.info("Viewed addition {} {} {} ",userId , cardId, puid);
		try
		{
			String userRole = getUserRole();
			PeerViewedEntityPK pvPK = new PeerViewedEntityPK();
			pvPK.setCardId(cardId);
			pvPK.setRoleName(userRole);
			Optional<PeerViewedEntity> peerViewExist = peerViewedRepo.findById(pvPK);
			// record already exists in the table
			if (peerViewExist.isPresent()) {
				PeerViewedEntity dbEntry = peerViewExist.get();
				if(dbEntry!=null){
					dbEntry.setUpdatedTime(Timestamp.valueOf(ProductDocumentationUtil.getNowDateUTCStr()));
					peerViewedRepo.save(dbEntry);
				}
			}
			else
			{
				PeerViewedEntity newEntry = new PeerViewedEntity();
				newEntry.setCardId(cardId);
				newEntry.setRole_name(userRole);
				newEntry.setUpdatedTime(Timestamp.valueOf(ProductDocumentationUtil.getNowDateUTCStr()));
				peerViewedRepo.save(newEntry);
			}

		}
		catch(Exception e)
		{
			LOG.error("Error occurred save peer view",e);
		}	
	}

	private List<String> getRangeLW(List<LearningItemEntity> onlyFutureLWIds, Map<String, String> ddbTI)
	{
		long requestStartTime = System.currentTimeMillis();	
		List<String> rangeCardsIds = new ArrayList<>();
		String startTime = ddbTI.get(TI_START_TIME).trim();
		String endTime = ddbTI.get(TI_END_TIME).trim();
		String timeZone = ddbTI.get(TI_TIME_ZONE).trim();
		//LOG.info("TI:{},{},{}",startTime,endTime, timeZone);

		int hrs1 = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
		int min1 = Integer.parseInt(startTime.substring(startTime.indexOf(":")+1, startTime.indexOf(" ")));
		if(startTime.contains(PM)) {hrs1=hrs1+TWELVE;}
		else if (hrs1 == TWELVE) {hrs1=0;}

		int hrs2 = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
		int min2 = Integer.parseInt(endTime.substring(endTime.indexOf(":")+1, endTime.indexOf(" ")));
		if(endTime.contains(PM)) {hrs2=hrs2+TWELVE;}
		else if (hrs2 == TWELVE) {hrs2=0;}
		//LOG.info("{} {} {} {}",hrs1,min1,hrs2,min2);

		Integer hrMin[] = ProductDocumentationUtil.getHrsMins(timeZone);
		int hrs3 = hrMin[0];
		int min3 = hrMin[1];
		if(timeZone.contains(UTC_MINUS)) {min3 = min3 * -1;}		

		for(LearningItemEntity futureCard : onlyFutureLWIds)
		{			
			Date date4 = Timestamp.valueOf(futureCard.getSortByDate());	
			int finalHrs = date4.getHours() + hrs3; 
			if(finalHrs<0) {finalHrs = finalHrs*-1 -1;} 
			if(finalHrs>=TWENTY_FOUR) {finalHrs-=TWENTY_FOUR;}
			int finalMin = date4.getMinutes() + min3; 
			if(finalMin<0) {finalMin += SIXTY; finalHrs-=1; } 
			if(finalMin>=SIXTY) { finalMin-=SIXTY;finalHrs+=1;}
			LOG.info("finalHrs {} {} {} {} {} {} {} {} {} {}",futureCard.getLearning_item_id(),hrs1,min1,hrs2,min2, hrs3, min3, date4 , finalHrs, finalMin);

			boolean hrsCondition = (finalHrs>hrs1 && finalHrs<hrs2);
			boolean hrMinCondition1 = (finalHrs==hrs1 && finalMin>=min1 );
			boolean hrMinCondition2 = (finalHrs==hrs2 && finalMin <= min2 ) ;
			if( hrsCondition ||	hrMinCondition1 ||	hrMinCondition2	)
			{ rangeCardsIds.add(futureCard.getLearning_item_id());} 
		}		
		LOG.info("PD-range processed in {} ", (System.currentTimeMillis() - requestStartTime));
		return rangeCardsIds; //rangeCards
	}

	/** ["{\"endTime\":\"4:00 PM\",\"startTime\":\"9:00 AM\",\"timeZone\":\"PDT(UTC-7)\"}"] 
	 * @param limit **/
	@SuppressWarnings("unchecked")
	private Set<String> getWebinarTimeinterval(List<String> timeInterval, String hcaasStatus)
	{
		Set<String> onlyFutureLWInRange  = new HashSet<>();
		try
		{
			Map<String,String> ddbTI = new HashMap<>();
			if(timeInterval !=null && timeInterval.size()==1)
			{
				String tiStr = timeInterval.get(0);
				Map<String,String> ddbTimeinterval= new ObjectMapper().readValue(tiStr, Map.class);
				ddbTI.putAll(ddbTimeinterval);				

				boolean srtCondition = ddbTI.get(TI_START_TIME)!=null && !ddbTI.get(TI_START_TIME).trim().isEmpty();
				boolean endCondition = ddbTI.get(TI_END_TIME)!=null && !ddbTI.get(TI_END_TIME).trim().isEmpty() ;
				boolean tznCondition = ddbTI.get(TI_TIME_ZONE)!=null && !ddbTI.get(TI_TIME_ZONE).trim().isEmpty();	
				if(	srtCondition &&	endCondition && tznCondition )
				{
					List<LearningItemEntity>  onlyFutureLWs = new ArrayList<>();
					Set<String> onlyFutureLWIds= new HashSet<>();
					String contentTab = "Preference";
					long requestStartTime = System.currentTimeMillis();	
					onlyFutureLWs.addAll(productDocumentationDAO.getUpcomingWebinars(contentTab, hcaasStatus));
					LOG.info("PD-UWeb fetch in {} ", (System.currentTimeMillis() - requestStartTime));
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
				GenericLearningModel::getSpecialization,Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(GenericLearningModel::getCreatedTimeStamp,Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(
						GenericLearningModel::getAvgRatingPercentage, Comparator.nullsFirst(Comparator.naturalOrder()))
				);
		if(order.isDescending()) {Collections.reverse(learningCards);}
	}

	private void randomizeCards(LearningRecordsAndFiltersModel learningCards, Integer limitEnd, int specializationIndex)
	{		
		int orgSize = learningCards.getLearningData().size();
		if(orgSize > limitEnd) //25
		{
			long requestStartTime = System.currentTimeMillis();	
			int randomNums = orgSize>= limitEnd*TWO ? limitEnd/TWO : orgSize-limitEnd-1;  //12 or less			
			int boundry = orgSize>= limitEnd*TWO ? limitEnd*TWO : orgSize; //50 or less
			Set<Integer> randomIndexes = new HashSet<>();			
			//while(randomIndexes.size()<randomNums) ---may take more time
			for(int i=0;i<=randomNums;i++)
			{
				int randomNum = r.nextInt(boundry-specializationIndex) + specializationIndex;
				randomIndexes.add(randomNum);
			}
			LOG.info("Randomly removed {} {}", randomIndexes.size(),randomIndexes);

			List<String> allCardIds = new ArrayList<>();
			List<String> newListCardIds = new ArrayList<>();
			List<GenericLearningModel> orgList = learningCards.getLearningData();
			List<GenericLearningModel> newList = new ArrayList<>();
			for(int i=0;i<boundry;i++ )
			{
				allCardIds.add(orgList.get(i).getRowId());
				if(!randomIndexes.contains(i)) { newList.add(orgList.get(i)); newListCardIds.add(orgList.get(i).getRowId()); }
			}
			learningCards.setLearningData(newList);
			LOG.info(" random org {} {}, new {}{}", allCardIds.size(),allCardIds , newListCardIds.size(), newListCardIds );
			LOG.info("PD-random done in {} ", (System.currentTimeMillis() - requestStartTime));
		}
	}

	private void limitCards(LearningRecordsAndFiltersModel learningCards, Integer limitEnd)
	{		
		if(learningCards!=null && learningCards.getLearningData()!=null && learningCards.getLearningData().size()>0)
		{		
			if(limitEnd > learningCards.getLearningData().size()) {limitEnd = learningCards.getLearningData().size();}
			List<GenericLearningModel> preferredCards = learningCards.getLearningData().subList(0, limitEnd);
			learningCards.setLearningData(preferredCards);
		}
	}

	/** all prefs are OR , no anding **/ 
	private LearningRecordsAndFiltersModel getCards(String userId, HashMap<String, Object> applyFilters, String userRole, String hcaasStatus)
	{
		String contentTab = "Preference";
		String sort = DEFAULT_SORT_FIELD ; 
		Direction order = DEFAULT_SORT_ORDER ;				
		Set<String> userBookmarks = learningDAO.getBookmarks(userId);
		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);				
		List<LearningItemEntity> dbCards = new ArrayList<>();
		Map<String, Set<String>> prefCards = new HashMap<>();
		if(!applyFilters.isEmpty())
		{
			prefCards.putAll(filterCards(applyFilters,contentTab, hcaasStatus));
		}
		prefCards.put("peerCards",getPeerViewedCards(userRole));
		prefCards.put("tiCards",getWebinarTimeinterval((List<String>) applyFilters.get(TIME_INTERVAL_FILTER), hcaasStatus));
		Set<String> filteredCards = ProductDocumentationUtil.orPreferences(prefCards);
		if(filteredCards!=null && !filteredCards.isEmpty()) {  //NOSONAR
			long requestStartTime = System.currentTimeMillis();	
			dbCards.addAll(productDocumentationDAO.getAllLearningCardsByFilter(contentTab,filteredCards,Sort.by(order, sort), hcaasStatus));
			LOG.info("PD-gC in {} ", (System.currentTimeMillis() - requestStartTime));
			}		
		LOG.info("all OR dbCards= {}",dbCards.size());
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));		
		return responseModel;	
	}	

	/** {"Language":["English","Japanese"], "Toppicks":["CI-100","SWAP-200"]} **/
	public Map<String, Object> getTopPicksFiltersPost(Map<String, Object> applyFilters, boolean hcaasStatusFlag) {
		return tpService.getTopPicksFiltersViewMore(applyFilters, hcaasStatusFlag);
	}

	/** {"Language":["English","Japanese"], "Toppicks":["CI-100","SWAP-200"]} **/
	public LearningRecordsAndFiltersModel getTopPicksCardsPost(String userId, Map<String, Object> applyFilters, boolean hcaasStatusFlag) {	
		return tpService.getTopPicksCardsViewMore(userId, applyFilters, hcaasStatusFlag);
	}

}
