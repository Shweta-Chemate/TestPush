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
	
	
	public static final String CASE_CLAUSE = " ( "
			+ " case when :joinTable='Technology' then learning_item_id in (select ct.learning_item_id from cxpp_db.cxpp_learning_technology ct) "
			+ " when :joinTable='Skill' then learning_item_id in (select cr.learning_item_id from cxpp_db.cxpp_learning_roles cr) "
			+ " else 1=1 end "
			+ " ) ";
	
	public static final String CASE_CLAUSE_WHERE = " where " + CASE_CLAUSE;			
	public static final String CASE_CLAUSE_AND = " and " + CASE_CLAUSE;
	
	public static final String CASE_CLAUSE_CL = " ( "
			+ " case when :joinTable='Technology' then cl.learning_item_id in (select ct.learning_item_id from cxpp_db.cxpp_learning_technology ct) "
			+ " when :joinTable='Skill' then cl.learning_item_id in (select cr.learning_item_id from cxpp_db.cxpp_learning_roles cr) "
			+ " else 1=1 end "
			+ " ) ";
	
	public static final String DYNAMIC_FROM_SUBQUERY = " from ( select cl.* "
			+ " from cxpp_db.cxpp_learning_item cl "
			+ " where "
			+ CASE_CLAUSE_CL
			+ " ) as cl ";
	
	/** all cards **/
	
	public static final String GET_PD_LEARNING_CARDS = "select cl.*, CT.asset_types,CT.asset_links, mp.title as learning_map  "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " left join "
			+ "	(select learning_item_id, "
			+ "	group_concat(ifnull(asset_type,'') separator ',') as asset_types, "
			+ "	group_concat(ifnull(link,'') separator ',') AS asset_links "
			+ "	from cxpp_db.cxpp_item_link  "
			+ "	group by learning_item_id) as CT "
			+ "	on cl.learning_item_id = CT.learning_item_id "
			+ " left join cxpp_db.cxpp_learning_map mp on cl.learning_map_id=mp.learning_map_id "
			+ "  \n-- #sort\n";	
	@Query(value=GET_PD_LEARNING_CARDS , nativeQuery=true )
	List<LearningItemEntity> getAllLearningCards(String joinTable,Sort sort);
	
	
	/** search **/
	
	public static final String GET_PD_LEARNING_CARDS_SEARCH = "select cl.*, CT.asset_types,CT.asset_links, mp.title as learning_map  "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " left join "
			+ "	(select learning_item_id, "
			+ "	group_concat(ifnull(asset_type,'') separator ',') as asset_types, "
			+ "	group_concat(ifnull(link,'') separator ',') AS asset_links "
			+ "	from cxpp_db.cxpp_item_link  "
			+ "	group by learning_item_id) as CT "
			+ "	on cl.learning_item_id = CT.learning_item_id "
			+ " left join cxpp_db.cxpp_learning_map mp on cl.learning_map_id=mp.learning_map_id "
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken "
			+ "  \n-- #sort\n";	
	@Query(value=GET_PD_LEARNING_CARDS_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsBySearch(String joinTable, String likeToken, Sort sort);

	public static final String GET_PD_LEARNING_CARD_IDS_SEARCH = " select cl.learning_item_id "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken " ;		
	@Query(value=GET_PD_LEARNING_CARD_IDS_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsBySearch(String joinTable,String likeToken);
	
	
	/** Filter **/
	
	public static final String GET_PD_LEARNING_BY_CONTENT_TYPE = "select distinct learning_item_id "
			+ " from cxpp_db.cxpp_item_link "
			+ " where asset_type in (:contentTypeFilter) "
			+ CASE_CLAUSE_AND ;
	
	@Query(value=GET_PD_LEARNING_BY_CONTENT_TYPE , nativeQuery=true)
	Set<String> getLearningsByContentType(String joinTable,Set<String> contentTypeFilter);	
	
	public static final String GET_PD_LEARNING_CARDS_BY_FILTER = "select cl.*, CT.asset_types,CT.asset_links, mp.title as learning_map  "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " left join "
			+ "	(select learning_item_id, "
			+ "	group_concat(ifnull(asset_type,'') separator ',') as asset_types, "
			+ "	group_concat(ifnull(link,'') separator ',') AS asset_links "
			+ "	from cxpp_db.cxpp_item_link  "
			+ "	group by learning_item_id) as CT "
			+ "	on cl.learning_item_id = CT.learning_item_id "
			+ " left join cxpp_db.cxpp_learning_map mp on cl.learning_map_id=mp.learning_map_id "
			+ " where cl.learning_item_id in (:filterCards) "
			+ "  \n-- #sort\n";
	
	@Query(value=GET_PD_LEARNING_CARDS_BY_FILTER , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilter(String joinTable,Set<String> filterCards, Sort sort);
	
	
	/** filter + search */
	
	public static final String GET_PD_LEARNING_CARDS_FILTERED_SEARCH = "select cl.*, CT.asset_types,CT.asset_links, mp.title as learning_map  "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " left join "
			+ "	(select learning_item_id, "
			+ "	group_concat(ifnull(asset_type,'') separator ',') as asset_types, "
			+ "	group_concat(ifnull(link,'') separator ',') AS asset_links "
			+ "	from cxpp_db.cxpp_item_link  "
			+ "	group by learning_item_id) as CT "
			+ "	on cl.learning_item_id = CT.learning_item_id "
			+ " left join cxpp_db.cxpp_learning_map mp on cl.learning_map_id=mp.learning_map_id "
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) "
			+ "  \n-- #sort\n";
	
	@Query(value=GET_PD_LEARNING_CARDS_FILTERED_SEARCH , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsByFilterSearch(String joinTable,Set<String> filteredCards, String likeToken, Sort sort);
	
	
	public static final String GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH = " select cl.learning_item_id "
			//+ " from cxpp_db.cxpp_learning_item cl "
			+ DYNAMIC_FROM_SUBQUERY
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) " ;
			
	
	@Query(value=GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH , nativeQuery=true)
	Set<String> getAllLearningCardIdsByFilterSearch(String joinTable,Set<String> filteredCards, String likeToken);
	
	
	/** other filters **/
	public static final String GET_PD_CARD_IDS_REGION = " select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ " where  piw_region in (:values)" 
			+ CASE_CLAUSE_AND ;					
	@Query(value=GET_PD_CARD_IDS_REGION , nativeQuery=true)
	Set<String> getCardIdsByRegion(String joinTable,Set<String> values);
	
	public static final String GET_PD_CARD_IDS_LG = " select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ " where  piw_language in (:values)" 
			+ CASE_CLAUSE_AND ;					
	@Query(value=GET_PD_CARD_IDS_LG , nativeQuery=true)
	Set<String> getCardIdsByLanguage(String joinTable,Set<String> values);
	
	public static final String GET_PD_CARD_IDS_AT = " select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ " where  archetype in (:values)" 
			+ CASE_CLAUSE_AND ;					
	@Query(value=GET_PD_CARD_IDS_AT , nativeQuery=true)
	Set<String> getCardIdsByAT(String joinTable,Set<String> values);
	
	public static final String GET_PD_CARD_IDS_TC = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_technology "
			+ " where technology in (:values) "
			+ CASE_CLAUSE_AND ;					
	@Query(value=GET_PD_CARD_IDS_TC , nativeQuery=true)
	Set<String> getCardIdsByTC(String joinTable,Set<String> values);
	
	public static final String GET_PD_CARD_IDS_BY_stUcPs = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc"
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " inner join cxpp_db.cxpp_learning_pitstop ps "
			+ " on ps.usecase_id = uc.usecase_id "
			+ " where ps.pitstop in (:pitstopInp) and uc.usecase = :usecaseInp and st.successtrack = :successtrackInp"
			+ CASE_CLAUSE_AND ;							
	@Query(value=GET_PD_CARD_IDS_BY_stUcPs , nativeQuery=true)	
	Set<String> getCardIdsByPsUcSt(String joinTable,String successtrackInp, String usecaseInp, Set<String> pitstopInp);
	
	/** count by cards **/	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT_BY_CARD = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by technology "
			+ " order by technology ";	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT_BY_CARD , nativeQuery=true)	
	List<Map<String, Object>> getAllTechnologyWithCountByCards(String joinTable,Set<String> cardIds);	
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by asset_type "
			+ " order by asset_type ";	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(String joinTable,Set<String> cardIds);
	
	public static final String GET_PD_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by piw_language "
			+ " order by piw_language ";	
	@Query(value=GET_PD_LANGUAGE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCountByCards(String joinTable,Set<String> cardIds);
	
	
	public static final String GET_PD_DOCUMENTATION_WITH_COUNT_BY_CARD = "select archetype as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by archetype "
			+ " order by archetype ";
	@Query(value=GET_PD_DOCUMENTATION_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllDocumentationWithCountByCards(String joinTable,Set<String> cardIds);
	
	
	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by piw_region "
			+ " order by piw_region ";
	@Query(value=GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCountByCards(String joinTable,Set<String> cardIds);

	
	/** all counts **/
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ CASE_CLAUSE_WHERE 
			+ " group by asset_type order by asset_type ";	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCount(String joinTable);	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology "
			+ CASE_CLAUSE_WHERE 
			+ " group by technology order by technology;";	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllTechnologyWithCount(String joinTable);	
	
	public static final String GET_PD_LANGUAGE_WITH_COUNT = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "		
			+ CASE_CLAUSE_WHERE 
			+ " group by piw_language "
			+ " order by piw_language ";	
	@Query(value=GET_PD_LANGUAGE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLanguageWithCount(String joinTable);
	
	
	public static final String GET_PD_DOCUMENTATION_WITH_COUNT = "select archetype as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ CASE_CLAUSE_WHERE 
			+ " group by archetype "
			+ " order by archetype ";
	@Query(value=GET_PD_DOCUMENTATION_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllDocumentationWithCount(String joinTable);
	
	
	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ CASE_CLAUSE_WHERE 
			+ " group by piw_region "
			+ " order by piw_region ";
	@Query(value=GET_PD_LIVE_EVENTS_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllLiveEventsWithCount(String joinTable);
	
	
	/** ST **/	
	
	public static final String GET_PD_ST_UC_PS_WITH_COUNT = "select count(*) as dbvalue ,  pitstop, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ CASE_CLAUSE_WHERE   // WHERE !
			+ " group by pitstop,usecase,successtrack "
			+ " order by successtrack,usecase,pitstop ";	
	@Query(value=GET_PD_ST_UC_PS_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllStUcPsWithCount(String joinTable);
	
	
	public static final String GET_PD_ST_UC_PS_WITH_COUNT_BY_CARDS = "select count(*) as dbvalue ,  pitstop, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND    // AND !
			+ " group by pitstop,usecase,successtrack "
			+ " order by successtrack,usecase,pitstop ";	
	@Query(value=GET_PD_ST_UC_PS_WITH_COUNT_BY_CARDS , nativeQuery=true)
	List<Map<String, Object>> getAllStUcPsWithCountByCards(String joinTable,Set<String> cardIds);
	
	
	/** skill  - for role no case clause required **/
	
	public static final String GET_PD_ROLE_WITH_COUNT = "select roles as dbkey, count(*) as dbvalue "
			+ "	from cxpp_db.cxpp_learning_roles 		"
			+ " group by roles order by roles ";	
	@Query(value=GET_PD_ROLE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllRoleWithCount();
	
	public static final String GET_PD_CARD_IDS_ROLE = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_roles "
			+ " where roles in (:values) " ;							
	@Query(value=GET_PD_CARD_IDS_ROLE , nativeQuery=true)
	Set<String> getCardIdsByRole(Set<String> values);
	
	public static final String GET_PD_ROLE_WITH_COUNT_BY_CARD = "select roles as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_roles "
			+ " where learning_item_id in (:cardIds) "		
			+ " group by roles "
			+ " order by roles ";	
	@Query(value=GET_PD_ROLE_WITH_COUNT_BY_CARD , nativeQuery=true)	
	List<Map<String, Object>> getAllRoleWithCountByCards(Set<String> cardIds);
	
	/** For You - New **/	
	public static final String GET_PD_YOU_CARD_IDS_BY_CARD = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND ;			
	@Query(value=GET_PD_YOU_CARD_IDS_BY_CARD , nativeQuery=true)
	Set<String> getAllNewCardIdsByCards(String joinTable,Set<String> cardIds);
	
	/** learning map **/
	
	public static final String GET_PD_LEARNING_MAP_COUNTS = "select mp.learning_map_id as dbkey, count(*) as dbvalue"
			+ " from cxpp_db.cxpp_learning_item cl "
			+ " inner join cxpp_db.cxpp_learning_map mp "
			+ " on cl.learning_map_id=mp.learning_map_id "
			+ " where cl.learning_map_id is not null "
			+ " group by mp.learning_map_id;";			
	@Query(value=GET_PD_LEARNING_MAP_COUNTS , nativeQuery=true)
	List<Map<String, Object>> getLearningMapCounts();
		
}





