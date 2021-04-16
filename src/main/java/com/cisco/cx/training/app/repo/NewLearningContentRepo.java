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

	public static final String GET_RECENTLY_VIEWED_CONTENT = "select content.* from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id and content.id in (:learningItemIds) "
			+ " order by status.viewed_timestamp desc limit 25;";

	@Query(value = GET_RECENTLY_VIEWED_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> getRecentlyViewedContent(String puid, String userId, Set<String> learningItemIds);

}
