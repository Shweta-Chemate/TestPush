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
	
	
	public LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake,String searchToken, String applyFilters) {
		
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		Set<String> userBookmarks = null;
		if(null != userDetails){userBookmarks = learningDAO.getBookmarks(userDetails.getCecId());}
		LearningRecordsAndFiltersModel responseModel = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> learningCards = new ArrayList<>();
		responseModel.setLearningData(learningCards);
				
		List<LearningItemEntity> dbCards = null;
		if(searchToken!=null && !searchToken.trim().isEmpty())
			dbCards = productDocumentationDAO.getAllLearningCardsBySearch("%"+searchToken+"%");
		else if(applyFilters!=null && !applyFilters.isEmpty())
			dbCards = productDocumentationDAO.getAllLearningCardsByFilter(filterCards(applyFilters)); 
		else 
			dbCards=productDocumentationDAO.getAllLearningCards();
		
		LOG.info("dbCards={}",dbCards);
		learningCards.addAll(mapLearningEntityToCards(dbCards, userBookmarks));
		
		
		return responseModel;
	}
	
	private Set<String> filterCards(String applyFilters)
	{		String types = applyFilters.substring(0,applyFilters.indexOf("-"));
		String[] contentTypes = applyFilters.substring(applyFilters.indexOf("-")+1).split(",");
		LOG.info("types={}{}...{}",types,contentTypes.length,contentTypes);
		
		Set<String> mappedContentTypes = new HashSet<String>();
		Arrays.asList(contentTypes).forEach(ct -> {
			if(CONTENT_TYPE_MAP.keySet().contains(ct.trim())) mappedContentTypes.add(CONTENT_TYPE_MAP.get(ct.trim()));			
		});
				
		LOG.info("mapped = {} ",mappedContentTypes);				
		  
		
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
			
			card.setCreatedTimeStamp(learning.getPublished_date());//(String.valueOf(learning.getUpdated_timestamp().getTime()));			
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
		/** start all filters with 0 count **/
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, String> technologyFilter = new HashMap<>();		
		Arrays.asList(TECHNOLOGY_KEYS).forEach(type -> technologyFilter.put(type, "0"));
		filters.put("Technology", technologyFilter);	

		HashMap<String, HashMap<String, String>> successTrackFilter = new HashMap<String,HashMap<String, String>>();		
		for(int arr=0;arr<SUCCESSTRACK_KEYS.length;arr++)
		{
			HashMap<String, String> usecaseFilter = new HashMap<>();
			String[] arrVals = USECASE_KEYS[arr];
			Arrays.asList(arrVals).forEach(uc -> usecaseFilter.put(uc, "0"));			
			successTrackFilter.put(SUCCESSTRACK_KEYS[arr], usecaseFilter);
		}
		filters.put("Success Tracks", successTrackFilter);

		HashMap<String, String> contentTypeFilter = new HashMap<>();		
		CONTENT_TYPE_MAP.values().forEach(type -> contentTypeFilter.put(type, "0"));
		filters.put("Content Type", contentTypeFilter);

		/** end all filters with 0 count **/

		List<LearningItemEntity> dbCards = null;
		if(searchToken!=null && !searchToken.trim().isEmpty())
			dbCards=productDocumentationDAO.getAllLearningCardsBySearch("%"+searchToken+"%");

		if(dbCards!=null && dbCards.size()>0)
		{

			Set<String> cardIds = new HashSet<String>();
			dbCards.forEach( card -> cardIds.add(card.getLearning_item_id()));	

			List<Map<String,Object>> dbListTech = productDocumentationDAO.getAllTechnologyWithCountByCards(cardIds);
			technologyFilter.putAll(listToMap(dbListTech, Arrays.asList(TECHNOLOGY_KEYS)));

			List<Map<String,Object>> cardsST = productDocumentationDAO.getAllSuccesstrackByCards(cardIds);
			LOG.info("cardsST={}",cardsST);		
			
			Set<String> distinctST = new HashSet<String>();
			Set<String> stIds = new HashSet<String>();
			cardsST.forEach(map -> {distinctST.add(String.valueOf(map.get("dbvalue")));stIds.add(String.valueOf(map.get("dbkey")));});
			LOG.info("distinctST={} {}",distinctST, stIds);	

			List<Map<String, String>> stWithUc = productDocumentationDAO.getAllSuccesstrackWithUsecaseBySts(stIds);
			LOG.info("stWithUc={}",stWithUc);

			distinctST.forEach(st ->
			{
				if(Arrays.asList(SUCCESSTRACK_KEYS).contains(st)) 
				{

					HashMap<String, String> ucFilter = successTrackFilter.get(st);

					stWithUc.forEach( map ->
					{
						String stValue = map.get("dbkey");
						if(st.equals(stValue))
						{
							String ucValue = map.get("dbValue");
							if(ucFilter.keySet().contains(ucValue))
								ucFilter.put(ucValue, String.valueOf(Integer.valueOf(ucFilter.get(ucValue))+1));		
						}										
					});
				}			

			} );

			List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCountByCards(cardIds);
			contentTypeFilter.putAll(listToMap(dbList,CONTENT_TYPE_MAP.values()));
		}

		return filters;

	}
	

	
	/**
	 * filter=CT-LW,PDF,WP,PPT,XYZ,VOD,LM
	 * @param applyFilters
	 * @return
	 */
	public HashMap<String, Object> getAllLearningFiltersByApply(String applyFilters){
		
		String types = applyFilters.substring(0,applyFilters.indexOf("-"));
		String[] contentTypes = applyFilters.substring(applyFilters.indexOf("-")+1).split(",");
		LOG.info("types={}{}...{}",types,contentTypes.length,contentTypes);
		
		Set<String> mappedContentTypes = new HashSet<String>();
		Arrays.asList(contentTypes).forEach(ct -> {
			if(CONTENT_TYPE_MAP.keySet().contains(ct.trim())) mappedContentTypes.add(CONTENT_TYPE_MAP.get(ct.trim()));			
		});
				
		LOG.info("mapped = {} ",mappedContentTypes);				
		  
		
		Set<String> cardIds = productDocumentationDAO.getLearningsByContentType(mappedContentTypes);
		LOG.info("mapped = {} ",cardIds);	
		
		
		/** start all filters with 0 count **/		
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, String> technologyFilter = new HashMap<>();		
		Arrays.asList(TECHNOLOGY_KEYS).forEach(type -> technologyFilter.put(type, "0"));
		filters.put("Technology", technologyFilter);	

		HashMap<String, HashMap<String, String>> successTrackFilter = new HashMap<String,HashMap<String, String>>();		
		for(int arr=0;arr<SUCCESSTRACK_KEYS.length;arr++)
		{
			HashMap<String, String> usecaseFilter = new HashMap<>();
			String[] arrVals = USECASE_KEYS[arr];
			Arrays.asList(arrVals).forEach(uc -> usecaseFilter.put(uc, "0"));			
			successTrackFilter.put(SUCCESSTRACK_KEYS[arr], usecaseFilter);
		}
		filters.put("Success Tracks", successTrackFilter);

		HashMap<String, String> contentTypeFilter = new HashMap<>();		
		CONTENT_TYPE_MAP.values().forEach(type -> contentTypeFilter.put(type, "0"));
		filters.put("Content Type", contentTypeFilter);

		/** end all filters with 0 count **/
		
		List<Map<String,Object>> dbListTech = productDocumentationDAO.getAllTechnologyWithCountByCards(cardIds);
		technologyFilter.putAll(listToMap(dbListTech, Arrays.asList(TECHNOLOGY_KEYS)));
		filters.put("Technology", technologyFilter);	
		List<Map<String,Object>> cardsST = productDocumentationDAO.getAllSuccesstrackByCards(cardIds);
		LOG.info("cardsST={}",cardsST);		
		
		Set<String> distinctST = new HashSet<String>();
		Set<String> stIds = new HashSet<String>();
		cardsST.forEach(map -> {distinctST.add(String.valueOf(map.get("dbvalue")));stIds.add(String.valueOf(map.get("dbkey")));});
		LOG.info("distinctST={} {}",distinctST, stIds);	

		List<Map<String, String>> stWithUc = productDocumentationDAO.getAllSuccesstrackWithUsecaseBySts(stIds);
		LOG.info("stWithUc={}",stWithUc);

		distinctST.forEach(st ->
		{
			if(Arrays.asList(SUCCESSTRACK_KEYS).contains(st)) 
			{

				HashMap<String, String> ucFilter = successTrackFilter.get(st);

				stWithUc.forEach( map ->
				{
					String stValue = map.get("dbkey");
					if(st.equals(stValue))
					{
						String ucValue = map.get("dbValue");
						if(ucFilter.keySet().contains(ucValue))
							ucFilter.put(ucValue, String.valueOf(Integer.valueOf(ucFilter.get(ucValue))+1));		
					}										
				});
			}			

		} );
		
		List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCount();
		contentTypeFilter.putAll(listToMap(dbList,mappedContentTypes));//not CONTENT_TYPE_MAP.values()));
		
		return filters;		
	}
	
	public HashMap<String, Object> getAllLearningFilters(String searchToken,String applyFilters){
		
		if(searchToken!=null && !searchToken.trim().isEmpty()) return getAllLearningFiltersBySearch(searchToken);
		if(applyFilters!=null && !applyFilters.trim().isEmpty()) return getAllLearningFiltersByApply(applyFilters);
		
		HashMap<String, Object> filters = new HashMap<>();
		
		HashMap<String, String> technologyFilter = new HashMap<>();		
		Arrays.asList(TECHNOLOGY_KEYS).forEach(type -> technologyFilter.put(type, "0"));
		List<Map<String,Object>> dbListTech = productDocumentationDAO.getAllTechnologyWithCount();
		technologyFilter.putAll(listToMap(dbListTech, Arrays.asList(TECHNOLOGY_KEYS)));
		filters.put("Technology", technologyFilter);		
		
		HashMap<String, HashMap<String, String>> successTrackFilter = new HashMap<String,HashMap<String, String>>();		
		for(int arr=0;arr<SUCCESSTRACK_KEYS.length;arr++)
		{
			HashMap<String, String> usecaseFilter = new HashMap<>();
			String[] arrVals = USECASE_KEYS[arr];
			Arrays.asList(arrVals).forEach(uc -> usecaseFilter.put(uc, "0"));			
			successTrackFilter.put(SUCCESSTRACK_KEYS[arr], usecaseFilter);
		}
		filters.put("Success Tracks", successTrackFilter);
		
		List<String> distinctST = productDocumentationDAO.getAllSuccesstrack();
		LOG.info("distinctST={}",distinctST);
		
		List<Map<String, String>> stWithUc = productDocumentationDAO.getAllSuccesstrackWithUsecase();
		LOG.info("stWithUc={}",stWithUc);
		
		distinctST.forEach(st ->
		{
			if(Arrays.asList(SUCCESSTRACK_KEYS).contains(st)) 
			{
				
				HashMap<String, String> ucFilter = successTrackFilter.get(st);
				
				stWithUc.forEach( map ->
						{
							String stValue = map.get("dbkey");
							if(st.equals(stValue))
							{
								String ucValue = map.get("dbValue");
								if(ucFilter.keySet().contains(ucValue))
								ucFilter.put(ucValue, String.valueOf(Integer.valueOf(ucFilter.get(ucValue))+1));		
							}										
						});
			}			
							
		} );

		
		HashMap<String, String> contentTypeFilter = new HashMap<>();		
		CONTENT_TYPE_MAP.values().forEach(type -> contentTypeFilter.put(type, "0"));
		
		List<Map<String,Object>> dbList = productDocumentationDAO.getAllContentTypeWithCount();
		contentTypeFilter.putAll(listToMap(dbList,CONTENT_TYPE_MAP.values()));
		
		filters.put("Content Type", contentTypeFilter);
		
		
		return filters;
	}
	
	private Map<String,String> listToMap(List<Map<String,Object>> dbList, Collection<String> collection)
	{
		Map<String,String> countMap = new HashMap<String,String>();
		for(Map<String,Object> dbMap : dbList)
		{
			if(collection.contains(String.valueOf(dbMap.get("dbkey"))))
			countMap.put(String.valueOf(dbMap.get("dbkey")), String.valueOf(dbMap.get("dbvalue")));
		}
		return countMap;
	}
	
	private static final String[] SUCCESSTRACK_KEYS  = new String[] {"Campus Network","Security","Data Center","ABC"	};
	
	private static final String[][] USECASE_KEYS  = new String[][] 
			{ 
				{"Campus Software Image Management", "Onboard", "Implement", "Use", "Mobiltity","ABC","XYZ"},
				{"Firewall","Anti-Virus","Umbrella","ABC","XYZ"},
				{"Data1", "Data2"},
				{}
			};
	
	private static final String[] TECHNOLOGY_KEYS  = new String[] {"Enterprise Networks","Security","Data Center",
			"Collaboration", "Mobility", "IoT", "Cloud", "Analytics"};
			
	//private static final String[] CONTENT_TYPE_KEYS  = new String[] {"Live Webinar","Video On-Demand","Learning Map","PDF","PPT",	"Webpage","XYZ"};
		
	private static final Map<String,String> CONTENT_TYPE_MAP = new HashMap<String,String>();
	static {
		
		CONTENT_TYPE_MAP.put("LW","Live Webinar");
		CONTENT_TYPE_MAP.put("VOD","Video On-Demand");
		CONTENT_TYPE_MAP.put("LM","Learning Map");
		CONTENT_TYPE_MAP.put("PDF", "PDF");
		CONTENT_TYPE_MAP.put("PPT", "PPT");
		CONTENT_TYPE_MAP.put("WP","Webpage");
		CONTENT_TYPE_MAP.put("XYZ", "XYZ");
	};
	
}



