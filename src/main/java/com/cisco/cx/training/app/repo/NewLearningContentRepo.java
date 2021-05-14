package com.cisco.cx.training.app.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.constants.SQLConstants;

@Repository
public interface NewLearningContentRepo
		extends JpaRepository<NewLearningContentEntity, String>, JpaSpecificationExecutor<NewLearningContentEntity> {

	List<NewLearningContentEntity> findAllByLearningType(String learning_type);

	Integer countByLearningType(String learning_type);

	@Query(value = SQLConstants.GET_CONTENT_TYPE_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllContentTypeWithCountByCards(Set<String> learningItemIds);


	@Query(value = SQLConstants.GET_REGION_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllRegionsWithCountByCards(Set<String> learningItemIds);


	@Query(value = SQLConstants.GET_LANGUAGE_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllLanguagesWithCountByCards(Set<String> learningItemIds);


	@Query(value = SQLConstants.GET_RECENTLY_VIEWED_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> getRecentlyViewedContent(String puid, String userId);
	
	@Query(value = SQLConstants.GET_RECENTLY_VIEWED_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> getRecentlyViewedContentFiltered(String puid, String userId, Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_NEW_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> findNew();

	@Query(value = SQLConstants.GET_NEW_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> findNewFiltered(Set<String> learningItemIds);
	
	@Query(value = SQLConstants.GET_UPCOMING_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> findUpcoming();

	@Query(value = SQLConstants.GET_UPCOMING_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> findUpcomingFiltered(Set<String> learningItemIds);
	
	@Query(value = SQLConstants.GET_SUCCESSACADEMY_FILTER_WITH_COUNT, nativeQuery = true)
	List<Map<String, Object>> findSuccessAcademyFiltered(String asset_model, Set<String> learningItemIds);

	@Query(value= SQLConstants.GET_PD_CARDS__BY_ST , nativeQuery=true)
	List<NewLearningContentEntity> getCardsBySt(Set<String> successTracks, Set<String> cardIds);

	@Query(value=SQLConstants.GET_PD_ST_WITH_COUNT_BY_CARDS , nativeQuery=true)
	List<Map<String, Object>> getAllStWithCountByCards(Set<String> cardIds);

	@Query(value = SQLConstants.GET_DOC_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getDocFilterCountByCards(Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_SA_CAMPUS_COUNT, nativeQuery = true)
	int getSACampusCount(Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_CT, nativeQuery = true)
	Set<String> getCardIdsByCT(HashSet<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_LANG, nativeQuery = true)
	Set<String> getCardIdsByLang(HashSet<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_REG, nativeQuery = true)
	Set<String> getCardIdsByReg(HashSet<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_FACET, nativeQuery = true)
	Set<String> getCardIdsByfacet(HashSet<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_ASSET_MODEL, nativeQuery = true)
	String getAssetModelByValue(String value);

	@Query(value = SQLConstants.GET_CARD_IDs_ST, nativeQuery = true)
	Set<String> getCardIdsByST(HashSet<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_DOC, nativeQuery = true)
	Set<String> getCardIdsByDoc(HashSet<String> values, Set<String> learningItemIdsList);

}
