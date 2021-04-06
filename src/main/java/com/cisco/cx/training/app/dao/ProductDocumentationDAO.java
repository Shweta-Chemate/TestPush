package com.cisco.cx.training.app.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.cx.training.app.entities.LearningItemEntity;

public interface ProductDocumentationDAO extends JpaRepository<LearningItemEntity,String>{
	
	
	public static final String GET_PD_LEARNING_CARDS = "select * from cxpp_db.cxpp_learning_item;";
	
	@Query(value=GET_PD_LEARNING_CARDS , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCards();
	
	public static final String GET_PD_LEARNING_CARDS_WITH_LINKS = "select cl.*, il.link from cxpp_db.cxpp_learning_item cl "
			+ " left join cxpp_db.cxpp_item_link il "
			+ " on il.learning_item_id = cl.learning_item_id "
			+ "  order by cl.published_date desc ";
	
	@Query(value=GET_PD_LEARNING_CARDS_WITH_LINKS , nativeQuery=true)
	List<LearningItemEntity> getAllLearningCardsWithLinks();
	
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
		
}