package com.cisco.cx.training.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	
	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake,String searchToken, String applyFilters, 
			String sortBy, String sortOrder) 
	{
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
				
		List<LearningItemEntity> dbCards = null;
		if( searchToken!=null && !searchToken.trim().isEmpty() &&
				applyFilters!=null && !applyFilters.isEmpty()	)
		{
			Set<String> filteredCards = filterCards(applyFilters);
			dbCards = productDocumentationDAO.getAllLearningCardsByFilterSearch(filteredCards,"%"+searchToken+"%",Sort.by(order, sort));			
		}
		else if(searchToken!=null && !searchToken.trim().isEmpty())
			dbCards = productDocumentationDAO.getAllLearningCardsBySearch("%"+searchToken+"%",Sort.by(order, sort));
		else if(applyFilters!=null && !applyFilters.isEmpty())
			dbCards = productDocumentationDAO.getAllLearningCardsByFilter(filterCards(applyFilters),Sort.by(order, sort)); 
		else 
			dbCards=productDocumentationDAO.getAllLearningCards(Sort.by(order, sort));
		
		LOG.info("dbCards={}",dbCards);
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));
		
		
		return responseModel;
	}
	
	private Set<String> filterCards(String applyFilters)
	{	
		String[] contentTypes = applyFilters.split(",");
		LOG.info("types={}{}...{}",contentTypes.length,contentTypes);
		
		Set<String> mappedContentTypes = new HashSet<String>();
		Arrays.asList(contentTypes).forEach(ct -> {mappedContentTypes.add(ct);});		
		Set<String> cardIds = productDocumentationDAO.getLearningsByContentType(mappedContentTypes);
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
					
			cards.add(card);
		});
		return cards;
	}
	
	public HashMap<String, Object> getAllLearningFiltersBySearch(String searchToken)
	{
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put("Content Type", contentTypeFilter);
		Set<String> cardIds = null;
		if(searchToken!=null && !searchToken.trim().isEmpty())
			cardIds = productDocumentationDAO.getAllLearningCardIdsBySearch("%"+searchToken+"%");

		if(cardIds!=null && cardIds.size()>0)
		{
			List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCountByCards(cardIds);
			contentTypeFilter.putAll(listToMap(dbList));
		}

		return filters;

	}
	

	
	/**
	 * filter=CT-LW,PDF,WP,PPT,XYZ,VOD,LM
	 * @param applyFilters
	 * @return
	 */
	public HashMap<String, Object> getAllLearningFiltersByApply(String applyFilters){
		
		Set<String> cardIds = filterCards(applyFilters);
		LOG.info("mapped = {} ",cardIds);	
		HashMap<String, Object> filters = new HashMap<>();		
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put("Content Type", contentTypeFilter);
		List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCountByCards(cardIds);
		contentTypeFilter.putAll(listToMap(dbList));
		
		return filters;		
	}
	
	public HashMap<String, Object> getAllLearningFilters(String searchToken,String applyFilters){
		
		if( searchToken!=null && !searchToken.trim().isEmpty() &&
				applyFilters!=null && !applyFilters.isEmpty()	)
		{
			Set<String> filteredCards = filterCards(applyFilters);
			Set<String> filteredSearchedCards = productDocumentationDAO.getAllLearningCardIdsByFilterSearch(filteredCards,"%"+searchToken+"%");
			List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCountByCards(filteredSearchedCards);
			HashMap<String, Object> filters = new HashMap<>();		
			HashMap<String, String> contentTypeFilter = new HashMap<>();
			filters.put("Content Type", contentTypeFilter);
			contentTypeFilter.putAll(listToMap(dbList));
			return filters;
		}		
		else if(searchToken!=null && !searchToken.trim().isEmpty()) return getAllLearningFiltersBySearch(searchToken);
		else if(applyFilters!=null && !applyFilters.trim().isEmpty()) return getAllLearningFiltersByApply(applyFilters);
		
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, String> contentTypeFilter = new HashMap<>();		
				
		List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCount();
		contentTypeFilter.putAll(listToMap(dbList));
		
		filters.put("Content Type", contentTypeFilter);
		
		
		return filters;
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
	
	/** sort **/
	
	private static final String DEFAULT_SORT_FIELD = "sort_by_date";
	private static final Direction DEFAULT_SORT_ORDER = Sort.Direction.DESC;
	
}



