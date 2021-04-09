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
	
	
	public static final String GET_PD_LEARNING_CARDS = "select * from cxpp_db.cxpp_learning_item "
			+ "  \n-- #sort\n";
	
	@Query(value=GET_PD_LEARNING_CARDS , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCards(Sort sort);
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link group by asset_type;";
	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCount();
	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology group by technology order by technology;";
	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllTechnologyWithCount();	
	
	
	public static final String GET_PD_SUCCESSTRACK_DISTINCT = "select distinct successtrack "
			+ "from cxpp_db.cxpp_learning_successtrack st;";
	
	@Query(value=GET_PD_SUCCESSTRACK_DISTINCT , nativeQuery=true)
	List<String> getAllSuccesstrack();
	
	
	public static final String GET_PD_SUCCESSTRACK_WITH_USECASE = "select successtrack as dbkey, usecase as dbvalue "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc "
			+ " on uc.successtrack_id = st.successtrack_id; ";
	
	@Query(value=GET_PD_SUCCESSTRACK_WITH_USECASE , nativeQuery=true)
	List<Map<String, String>> getAllSuccesstrackWithUsecase();
	
	
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
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT_SEARCH = "select technology as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_technology "
			+ " where learning_item_id in (:cardIds) "
			+ " group by technology "
			+ " order by technology;";
	
	@Query(value=GET_PD_TECHNOLOGY_WITH_COUNT_SEARCH , nativeQuery=true)	
	List<Map<String, Object>> getAllTechnologyWithCountByCards(Set<String> cardIds);

	public static final String GET_PD_SUCCESSTRACK_DISTINCT_BY_CARD = "select successtrack_id as dbkey,  successtrack as dbvalue "
			+ "from cxpp_db.cxpp_learning_successtrack "
			+ " where learning_item_id in (:cardIds) ";	
	
	@Query(value=GET_PD_SUCCESSTRACK_DISTINCT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllSuccesstrackByCards(Set<String> cardIds);

	
	public static final String GET_PD_USECASE_BY_ST = "select successtrack as dbkey, usecase as dbvalue "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc "
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " where st.successtrack_id in (:stIds) ";	
	
	@Query(value=GET_PD_USECASE_BY_ST , nativeQuery=true)
	List<Map<String, String>> getAllSuccesstrackWithUsecaseBySts(Set<String> stIds);

	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ " where learning_item_id in (:cardIds) "
			+ " group by asset_type;";
	
	@Query(value=GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD , nativeQuery=true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(Set<String> cardIds);
	
	
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
	
		
}





