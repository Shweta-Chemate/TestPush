package com.cisco.cx.training.app.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cisco.cx.training.app.entities.LearningItemEntity;

public interface ProductDocumentationDAO extends JpaRepository<LearningItemEntity,String>{
	
	
	/** all cards **/
	
	public static final String GET_PD_LEARNING_CARDS = "select * from cxpp_db.cxpp_learning_item "
			+ "  \n-- #sort\n";	
	@Query(value=GET_PD_LEARNING_CARDS , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCards(Sort sort);
	
	
	/** search **/
	
	public static final String GET_PD_LEARNING_CARDS_SEARCH = " select * from cxpp_db.cxpp_learning_item cl "
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken "
			+ "  \n-- #sort\n";	
	@Query(value=GET_PD_LEARNING_CARDS_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsBySearch(String likeToken, Sort sort);

	public static final String GET_PD_LEARNING_CARD_IDS_SEARCH = " select cl.learning_item_id from cxpp_db.cxpp_learning_item cl "
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken " ;		
	@Query(value=GET_PD_LEARNING_CARD_IDS_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsBySearch(String likeToken);
	
	
	/** Filter **/
	
	public static final String GET_PD_LEARNING_BY_CONTENT_TYPE = "select distinct learning_item_id "
			+ " from cxpp_db.cxpp_item_link "
			+ " where asset_type in (:contentTypeFilter)"  ;
	
	@Query(value=GET_PD_LEARNING_BY_CONTENT_TYPE , nativeQuery=true)
	Set<String> getLearningsByContentType(Set<String> contentTypeFilter);	
	
	public static final String GET_PD_LEARNING_CARDS_BY_FILTER = "select * from cxpp_db.cxpp_learning_item cl "
			+ " where learning_item_id in (:filterCards) "
			+ "  \n-- #sort\n";
	
	@Query(value=GET_PD_LEARNING_CARDS_BY_FILTER , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilter(Set<String> filterCards, Sort sort);
	
	
	/** filter + search */
	
	public static final String GET_PD_LEARNING_CARDS_FILTERED_SEARCH = " select * from cxpp_db.cxpp_learning_item cl "
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) "
			+ "  \n-- #sort\n";
	
	@Query(value=GET_PD_LEARNING_CARDS_FILTERED_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilterSearch(Set<String> filteredCards, String likeToken, Sort sort);
	
	
	public static final String GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH = " select cl.learning_item_id from cxpp_db.cxpp_learning_item cl "
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) " ;
			
	
	@Query(value=GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsByFilterSearch(Set<String> filteredCards, String likeToken);
	
	
	/** other filters **/
	public static final String GET_PD_CARD_IDS_REGION = " select learning_item_id from cxpp_db.cxpp_learning_item  "
			+ " where  piw_region in (:values)" ;			
	@Query(value=GET_PD_CARD_IDS_REGION , nativeQuery=true)
	Set<String> getCardIdsByRegion(Set<String> values);
	
	public static final String GET_PD_CARD_IDS_LG = " select learning_item_id from cxpp_db.cxpp_learning_item  "
			+ " where  piw_language in (:values)" ;			
	@Query(value=GET_PD_CARD_IDS_LG , nativeQuery=true)
	Set<String> getCardIdsByLanguage(Set<String> values);
	
	public static final String GET_PD_CARD_IDS_AT = " select learning_item_id from cxpp_db.cxpp_learning_item  "
			+ " where  archetype in (:values)" ;			
	@Query(value=GET_PD_CARD_IDS_AT , nativeQuery=true)
	Set<String> getCardIdsByAT(Set<String> values);
	
	public static final String GET_PD_CARD_IDS_TC = "select learning_item_id from cxpp_db.cxpp_learning_technology "
			+ " where technology in (:values) ";					
	@Query(value=GET_PD_CARD_IDS_TC , nativeQuery=true)
	Set<String> getCardIdsByTC(Set<String> values);
	
	public static final String GET_PD_CARD_IDS_BY_stUcPs = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc"
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " inner join cxpp_db.cxpp_learning_pitstop ps "
			+ " on ps.usecase_id = uc.usecase_id "
			+ " where ps.pitstop in (:pitstopInp) and uc.usecase = :usecaseInp and st.successtrack = :successtrackInp";					
	@Query(value=GET_PD_CARD_IDS_BY_stUcPs , nativeQuery=true)	
	Set<String> getCardIdsByPsUcSt(String successtrackInp, String usecaseInp, Set<String> pitstopInp);
	
	/** count by cards **/	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT_SEARCH = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology "
			+ " where learning_item_id in (:cardIds) "
			+ " group by technology "
			+ " order by technology ";	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT_SEARCH , nativeQuery=true)	
	List<Map<String, Object>> getAllTechnologyWithCountByCards(Set<String> cardIds);	
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ " where learning_item_id in (:cardIds) "
			+ " group by asset_type "
			+ " order by asset_type ";	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(Set<String> cardIds);
	
	public static final String GET_PD_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ " group by piw_language "
			+ " order by piw_language ";	
	@Query(value=GET_PD_LANGUAGE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCountByCards(Set<String> cardIds);
	
	
	public static final String GET_PD_DOCUMENTATION_WITH_COUNT_BY_CARD = "select archetype as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ " group by archetype "
			+ " order by archetype ";
	@Query(value=GET_PD_DOCUMENTATION_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllDocumentationWithCountByCards(Set<String> cardIds);
	
	
	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ " group by piw_region "
			+ " order by piw_region ";
	@Query(value=GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCountByCards(Set<String> cardIds);
	
	/** all counts **/
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link group by asset_type order by asset_type ";	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCount();	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology group by technology order by technology;";	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllTechnologyWithCount();	
	
	public static final String GET_PD_LANGUAGE_WITH_COUNT = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "			
			+ " group by piw_language "
			+ " order by piw_language ";	
	@Query(value=GET_PD_LANGUAGE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCount();
	
	
	public static final String GET_PD_DOCUMENTATION_WITH_COUNT = "select archetype as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " group by archetype "
			+ " order by archetype ";
	@Query(value=GET_PD_DOCUMENTATION_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllDocumentationWithCount();
	
	
	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " group by piw_region "
			+ " order by piw_region ";
	@Query(value=GET_PD_LIVE_EVENTS_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCount();
	
	
	/** ST **/
	
	
	public static final String GET_PD_SUCCESSTRACK_DISTINCT = "select distinct successtrack "
			+ "from cxpp_db.cxpp_learning_successtrack st;";	
	@Query(value=GET_PD_SUCCESSTRACK_DISTINCT , nativeQuery=true)
	List<String> getAllSuccesstrack();
	
	public static final String GET_PD_ST_UC_PS = "       select learning_item_id ,  pitstop, usecase, successtrack"
			+ "			  from cxpp_db.cxpp_learning_successtrack st "
			+ "			  inner join cxpp_db.cxpp_learning_usecase uc "
			+ "			  on uc.successtrack_id = st.successtrack_id "
			+ "			  inner join cxpp_db.cxpp_learning_pitstop ps "
			+ "			  on ps.usecase_id = uc.usecase_id  ";	
	@Query(value=GET_PD_ST_UC_PS , nativeQuery=true)
	List<Map<String, String>> getAllStUcPs();
	
	
	
	
		
}





