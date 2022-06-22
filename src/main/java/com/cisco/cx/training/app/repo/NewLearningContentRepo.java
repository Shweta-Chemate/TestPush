package com.cisco.cx.training.app.repo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.constants.SQLConstants;

@SuppressWarnings({"squid:S1448"})
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

	@Query(value = SQLConstants.GET_ROLE_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllRoleCountByCards(Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_CISCOPLUS_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllCiscoPlusCountByCards(Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_TECH_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllTechCountByCards(Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_LFC_WITH_COUNT_BY_CARD, nativeQuery = true)
	List<Map<String, Object>> getAllLFCWithCountByCards(Set<String> learningItemIds);

	@Query(value=SQLConstants.GET_PD_ST_UC_WITH_COUNT , nativeQuery=true)
	List<Map<String, Object>> getAllStUcWithCount(Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_RECENTLY_VIEWED_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> getRecentlyViewedContent(String userId, String hcaasStatus);

	@Query(value = SQLConstants.GET_RECENTLY_VIEWED_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> getRecentlyViewedContentFiltered(String userId, Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_RECENTLY_VIEWED_IDs, nativeQuery = true)
	Set<String> getRecentlyViewedContentFilteredIds(String userId, Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_NEW_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> findNew(String hcaasStatus);

	@Query(value = SQLConstants.GET_NEW_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> findNewFiltered(Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_NEW_CONTENT_IDs, nativeQuery = true)
	Set<String> findNewFilteredIds(Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_UPCOMING_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> findUpcoming(String hcaasStatus);

	@Query(value = SQLConstants.GET_UPCOMING_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> findUpcomingFiltered(Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_CARD_IDs_CT, nativeQuery = true)
	Set<String> getCardIdsByCT(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_LANG, nativeQuery = true)
	Set<String> getCardIdsByLang(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_REG, nativeQuery = true)
	Set<String> getCardIdsByReg(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_ROLE, nativeQuery = true)
	Set<String> getCardIdsByRole(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_CISCOPLUS, nativeQuery = true)
	Set<String> getCardIdsByCiscoPlus(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_TECH, nativeQuery = true)
	Set<String> getCardIdsByTech(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_DOC, nativeQuery = true)
	Set<String> getCardIdsByDoc(Set<String> values, Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_CARD_IDs_ROLE_FILT, nativeQuery = true)
	Set<String> getCardIdsByRole(Set<String> values);

	@Query(value = SQLConstants.GET_CARD_IDs_TECH_FILT, nativeQuery = true)
	Set<String> getCardIdsByTech(Set<String> values);

	@Query(value = SQLConstants.GET_CARD_IDs_LFC, nativeQuery = true)
	Set<String> getCardIdsByLFC(Set<String> values, Set<String> learningItemIds);

	@Query(value=SQLConstants.GET_PD_CARD_IDS_BY_STUC , nativeQuery=true)
	Set<String> getCardIdsByUcSt(String successtrackInp, Set<String> usecaseInp);

	@Query(value=SQLConstants.GET_PD_CARD_IDS_BY_STUC_FILTER , nativeQuery=true)
	Set<String> getCardIdsByUcStFilter(String successtrackInp, Set<String> usecaseInp, Set<String> learningItemIds);

	@Query(value = SQLConstants.GET_SORTED_BY_TITLE_ASC, nativeQuery = true)
	List<NewLearningContentEntity> getSortedByTitleAsc(Set<String> learningItemIdsList);

	@Query(value = SQLConstants.GET_SORTED_BY_TITLE_DESC, nativeQuery = true)
	List<NewLearningContentEntity> getSortedByTitleDesc(Set<String> learningItemIdsList);

	List<NewLearningContentEntity> findByLearningTypeAndLearningMap(String learning_type, String learning_map);

	@Query(value = SQLConstants.GET_CARD_IDs_PITSTOP_TAGGED, nativeQuery = true)
	List<String> getPitstopTaggedContent();

	@Query(value = SQLConstants.GET_CARD_IDs_PITSTOP_TAGGED_FILTER, nativeQuery = true)
	List<String> getPitstopTaggedContentFilter(Set<String> lfcFilters);
	
	NewLearningContentEntity findByTitle(String title);
	
	@Query(value = SQLConstants.GET_SUCCESSTRACKS_COUNT, nativeQuery = true)
	Integer getSuccessTracksCount();

	@Query(value = SQLConstants.GET_LIFECYCLE_COUNT, nativeQuery = true)
	Integer getLifecycleCount();
	
	@Query(value = SQLConstants.GET_TECHNOLOGY_COUNT, nativeQuery = true)
	Integer getTechnologyount();
	
	@Query(value = SQLConstants.GET_ROLES_COUNT, nativeQuery = true)
	Integer getRolesCount();

	@Query(value = SQLConstants.GET_CISCOPLUS_COUNT, nativeQuery = true)
	Integer getCiscoPlusCount();

	@Query(value = SQLConstants.GET_POPULAR_ACCROSS_PARTNERS, nativeQuery = true)
	List<NewLearningContentEntity> getPopularAcrossPartners(Integer limitNormal, Integer limitExtended, Integer mx, Set<String> userBookmarks, String hcaasStatus);

	@Query(value = SQLConstants.GET_POPULAR_ACCROSS_PARTNERS_FILTERED, nativeQuery = true)
	List<NewLearningContentEntity> getPopularAcrossPartnersFiltered(Set<String> learningItemIds, Integer limitNormal, Integer limitExtended, Integer mx, Set<String> userBookmarks, String hcaasStatus);

	@Query(value = SQLConstants.GET_POPULAR_AT_PARTNER, nativeQuery = true)
	List<NewLearningContentEntity> getPopularAtPartner(String puid, Integer limit, Integer limitExtended, Set<String> userBookmarks, String hcaasStatus);

	@Query(value = SQLConstants.GET_POPULAR_AT_PARTNER_FILTERED, nativeQuery = true)
	List<NewLearningContentEntity> getPopularAtPartnerFiltered(String puid, Set<String> learningItemIds, Integer limit, Integer limitExtended, Set<String> userBookmarks, String hcaasStatus);

	@Query(value = SQLConstants.GET_FEATURED_CONTENT_BASE, nativeQuery = true)
	List<NewLearningContentEntity> findFeatured(String hcaasStatus);
	
	@Query(value = SQLConstants.GET_FEATURED_CONTENT, nativeQuery = true)
	List<NewLearningContentEntity> findFeaturedFiltered(Set<String> learningItemIds, String hcaasStatus);

	@Query(value = SQLConstants.GET_MAX_BOOKMARK, nativeQuery = true)
	Integer getMaxBookmark();

}
