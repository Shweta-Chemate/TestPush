package com.cisco.cx.training.app.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.constants.ProductDocumentationConstants;

@SuppressWarnings({"java:S1448"})
public interface ProductDocumentationDAO extends JpaRepository<LearningItemEntity,String>{
	
	/** all cards **/
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARDS , nativeQuery=true )
	List<LearningItemEntity> getAllLearningCards(String joinTable,Sort sort);
	
	
	/** search **/
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARDS_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsBySearch(String joinTable, String likeToken, Sort sort);

	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARD_IDS_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsBySearch(String joinTable,String likeToken);
	
	
	/** Filter **/

	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_BY_CONTENT_TYPE , nativeQuery=true)
	Set<String> getLearningsByContentType(String joinTable,Set<String> contentTypeFilter);	
	
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARDS_BY_FILTER , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilter(String joinTable,Set<String> filterCards, Sort sort);
	
	
	/** filter + search */
	
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARDS_FILTERED_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilterSearch(String joinTable,Set<String> filteredCards, String likeToken, Sort sort);
	
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsByFilterSearch(String joinTable,Set<String> filteredCards, String likeToken);
	
	
	/** other filters **/
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_REGION , nativeQuery=true)
	Set<String> getCardIdsByRegion(String joinTable,Set<String> values);
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_LG , nativeQuery=true)
	Set<String> getCardIdsByLanguage(String joinTable,Set<String> values);
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_TC , nativeQuery=true)
	Set<String> getCardIdsByTC(String joinTable,Set<String> values);
	
	/** LM counts **/
		
	/** count by cards **/	
	
	@Query(value=ProductDocumentationConstants.GET_PD_TECHNOLOGY_WITH_COUNT_BY_CARD , nativeQuery=true)	
	List<Map<String, Object>> getAllTechnologyWithCountByCards(String joinTable,Set<String> cardIds);	
	
	@Query(value=ProductDocumentationConstants.GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(String joinTable,Set<String> cardIds);
	
	@Query(value=ProductDocumentationConstants.GET_PD_LANGUAGE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCountByCards(String joinTable,Set<String> cardIds);
	
	@Query(value=ProductDocumentationConstants.GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCountByCards(String joinTable,Set<String> cardIds);

	
	/** all counts **/
	
	@Query(value=ProductDocumentationConstants.GET_PD_CONTENT_TYPE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCount(String joinTable);	
	
	@Query(value=ProductDocumentationConstants.GET_PD_TECHNOLOGY_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllTechnologyWithCount(String joinTable);	
	
	@Query(value=ProductDocumentationConstants.GET_PD_LANGUAGE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCount(String joinTable);

	@Query(value=ProductDocumentationConstants.GET_PD_LIVE_EVENTS_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCount(String joinTable);
	
	/** skill  - for role no case clause required **/
	
	@Query(value=ProductDocumentationConstants.GET_PD_ROLE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllRoleWithCount(String joinTable);
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_ROLE , nativeQuery=true)
	Set<String> getCardIdsByRole(String joinTable, Set<String> values);
	
	@Query(value=ProductDocumentationConstants.GET_PD_ROLE_WITH_COUNT_BY_CARD , nativeQuery=true)	
	List<Map<String, Object>> getAllRoleWithCountByCards(String joinTable, Set<String> cardIds);
	
	/** For You - New **/	
	
	@Query(value=ProductDocumentationConstants.GET_PD_YOU_CARD_IDS_BY_CARD , nativeQuery=true)
	Set<String> getAllNewCardIdsByCards(String joinTable,Set<String> cardIds);
	
	/** learning map **/
	
	@Query(value=ProductDocumentationConstants.GET_PD_LEARNING_MAP_COUNTS , nativeQuery=true)
	List<Map<String, Object>> getLearningMapCounts();
	
	
	/** lifecycle **/
	
	@Query(value=ProductDocumentationConstants.GET_PD_ST_UC_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllStUcWithCount(String joinTable);
	
	@Query(value=ProductDocumentationConstants.GET_PD_PS_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllPsWithCount(String joinTable);
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_BY_PITSTOP , nativeQuery=true)	
	Set<String> getCardIdsByPsUcSt(String joinTable, HashSet<String> pitstopInp);
	
	@Query(value=ProductDocumentationConstants.GET_PD_CARD_IDS_BY_STUC , nativeQuery=true)	
	Set<String> getCardIdsByPsUcSt(String joinTable, String successtrackInp, Set<String> usecaseInp);
	
	@Query(value=ProductDocumentationConstants.GET_PD_ST_UC_WITH_COUNT_BY_CARDS , nativeQuery=true)
	List<Map<String, Object>> getAllStUcWithCountByCards(String joinTable,Set<String> cardIds);
	
	@Query(value=ProductDocumentationConstants.GET_PD_PS_WITH_COUNT_BY_CARDS , nativeQuery=true)
	List<Map<String, Object>> getAllPitstopsWithCountByCards(String joinTable, Set<String> cardIds);	
	

	/** for preferences **/
	
	@Query(value="select distinct roles from cxpp_db.cxpp_learning_roles where roles is not null order by roles", nativeQuery=true)
	List<String> getAllRolesForPreferences();

	@Query(value="select distinct technology from cxpp_db.cxpp_learning_technology where technology is not null order by technology", nativeQuery=true)
	List<String> getAllTechnologyForPreferences();

	@Query(value="select distinct piw_region from cxpp_db.cxpp_learning_item where piw_region is not null order by piw_region", nativeQuery=true)
	List<String> getAllRegionForPreferences();

	@Query(value="select distinct piw_language from cxpp_db.cxpp_learning_item where piw_language is not null order by piw_language", nativeQuery=true)
	List<String> getAllLanguagesForPreferences();

	@Query(value="select role from cxpp_db_um.cxpp_platform_roles where roleid=:userRoleId", nativeQuery=true)
	String getUserRole(String userRoleId);
	
	@Query(value=ProductDocumentationConstants.GET_UPCOMING_WEBINARS, nativeQuery=true)
	List<LearningItemEntity>  getUpcomingWebinars(String joinTable);

	@Query(value=ProductDocumentationConstants.GET_SPECIALIZED_CARDS, nativeQuery=true)
	Set<String> getCardIdsBySpecialization(Set<String> specializations);

}





