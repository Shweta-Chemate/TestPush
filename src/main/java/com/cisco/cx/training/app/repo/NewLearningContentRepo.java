package com.cisco.cx.training.app.repo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

@Repository
public interface NewLearningContentRepo
		extends JpaRepository<NewLearningContentEntity, String>, JpaSpecificationExecutor<NewLearningContentEntity> {

	List<NewLearningContentEntity> findAllByLearningType(String learning_type);

	Integer countByLearningType(String learning_type);

	public static final String GET_CONTENT_TYPE_WITH_COUNT = "select asset_type as label, count(asset_type) as count"
			+ " from cxpp_db.cxpp_item_link where asset_type IS NOT NULL AND asset_type!='' group by asset_type;";

	@Query(value = GET_CONTENT_TYPE_WITH_COUNT, nativeQuery = true)
	List<Map<String, Object>> getAllContentTypeWithCount();

	public static final String GET_REGION_WITH_COUNT = "select piw_region as label, count(piw_region) as count "
			+ "from cxpp_db.cxpp_learning_content where piw_region IS NOT NULL group by piw_region;";

	@Query(value = GET_REGION_WITH_COUNT, nativeQuery = true)
	List<Map<String, Object>> getAllRegionsTypeWithCount();

	public static final String GET_LANGUAGE_WITH_COUNT = "select piw_language as label, count(piw_language) as count"
			+ " from cxpp_db.cxpp_learning_content where piw_language IS NOT NULL group by piw_language;";

	@Query(value = GET_LANGUAGE_WITH_COUNT, nativeQuery = true)
	List<Map<String, Object>> getAllLanguagesTypeWithCount();

	public static final String GET_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where asset_type IS NOT NULL and id in (:learningItemIds) "
			+ " group by asset_type;";

	@Query(value = GET_CONTENT_TYPE_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(Set<String> learningItemIds);

	public static final String GET_REGION_WITH_COUNT_BY_CARD = "select piw_region as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_region IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_region;";

	@Query(value = GET_REGION_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllRegionsWithCountByCards(Set<String> learningItemIds);

	public static final String GET_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_language IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_language;";

	@Query(value = GET_LANGUAGE_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllLanguagesWithCountByCards(Set<String> learningItemIds);

}
