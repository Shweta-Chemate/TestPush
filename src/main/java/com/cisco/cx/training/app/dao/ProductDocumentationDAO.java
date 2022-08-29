package com.cisco.cx.training.app.dao;

import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.constants.ProductDocumentationConstants;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@SuppressWarnings({"java:S1448"})
public interface ProductDocumentationDAO extends JpaRepository<LearningItemEntity, String> {

  /** all cards * */
  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_CARDS, nativeQuery = true)
  List<LearningItemEntity> getAllLearningCards(String joinTable, Sort sort, String hcaasStatus);

  /** search * */
  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_CARDS_SEARCH, nativeQuery = true)
  List<LearningItemEntity> getAllLearningCardsBySearch(
      String joinTable, String likeToken, Sort sort, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_CARD_IDS_SEARCH, nativeQuery = true)
  Set<String> getAllLearningCardIdsBySearch(String joinTable, String likeToken, String hcaasStatus);

  /** Filter * */
  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_BY_CONTENT_TYPE, nativeQuery = true)
  Set<String> getLearningsByContentType(
      String joinTable, Set<String> contentTypeFilter, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_CARDS_BY_FILTER, nativeQuery = true)
  List<LearningItemEntity> getAllLearningCardsByFilter(
      String joinTable, Set<String> filterCards, Sort sort, String hcaasStatus);

  /** filter + search */
  @Query(
      value = ProductDocumentationConstants.GET_PD_LEARNING_CARDS_FILTERED_SEARCH,
      nativeQuery = true)
  List<LearningItemEntity> getAllLearningCardsByFilterSearch(
      String joinTable, Set<String> filteredCards, String likeToken, Sort sort, String hcaasStatus);

  @Query(
      value = ProductDocumentationConstants.GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH,
      nativeQuery = true)
  Set<String> getAllLearningCardIdsByFilterSearch(
      String joinTable, Set<String> filteredCards, String likeToken, String hcaasStatus);

  /** other filters * */
  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_REGION, nativeQuery = true)
  Set<String> getCardIdsByRegion(String joinTable, Set<String> values, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_LG, nativeQuery = true)
  Set<String> getCardIdsByLanguage(String joinTable, Set<String> values, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_TC, nativeQuery = true)
  Set<String> getCardIdsByTC(String joinTable, Set<String> values, String hcaasStatus);

  /** LM counts * */

  /** count by cards * */
  @Query(
      value = ProductDocumentationConstants.GET_PD_TECHNOLOGY_WITH_COUNT_BY_CARD,
      nativeQuery = true)
  List<Map<String, Object>> getAllTechnologyWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  @Query(
      value = ProductDocumentationConstants.GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD,
      nativeQuery = true)
  List<Map<String, Object>> getAllContentTypeWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  @Query(
      value = ProductDocumentationConstants.GET_PD_LANGUAGE_WITH_COUNT_BY_CARD,
      nativeQuery = true)
  List<Map<String, Object>> getAllLanguageWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  @Query(
      value = ProductDocumentationConstants.GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD,
      nativeQuery = true)
  List<Map<String, Object>> getAllLiveEventsWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  /** all counts * */
  @Query(value = ProductDocumentationConstants.GET_PD_CONTENT_TYPE_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllContentTypeWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_TECHNOLOGY_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllTechnologyWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_LANGUAGE_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllLanguageWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_LIVE_EVENTS_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllLiveEventsWithCount(String joinTable, String hcaasStatus);

  /** skill - for role no case clause required * */
  @Query(value = ProductDocumentationConstants.GET_PD_ROLE_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllRoleWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CISCOPLUS_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllCiscoPlusWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_ROLE, nativeQuery = true)
  Set<String> getCardIdsByRole(String joinTable, Set<String> values, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_CISCOPLUS, nativeQuery = true)
  Set<String> getCardIdsByCiscoPlus(String joinTable, Set<String> values, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_ROLE_WITH_COUNT_BY_CARD, nativeQuery = true)
  List<Map<String, Object>> getAllRoleWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  @Query(
      value = ProductDocumentationConstants.GET_PD_CISCOPLUS_WITH_COUNT_BY_CARD,
      nativeQuery = true)
  List<Map<String, Object>> getAllCiscoPlusWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  /** For You - New * */
  @Query(value = ProductDocumentationConstants.GET_PD_YOU_CARD_IDS_BY_CARD, nativeQuery = true)
  Set<String> getAllNewCardIdsByCards(String joinTable, Set<String> cardIds, String hcaasStatus);

  /** learning map * */
  @Query(value = ProductDocumentationConstants.GET_PD_LEARNING_MAP_COUNTS, nativeQuery = true)
  List<Map<String, Object>> getLearningMapCounts();

  /** lifecycle * */
  @Query(value = ProductDocumentationConstants.GET_PD_ST_UC_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllStUcWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_PS_WITH_COUNT, nativeQuery = true)
  List<Map<String, Object>> getAllPsWithCount(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_BY_PITSTOP, nativeQuery = true)
  Set<String> getCardIdsByPsUcSt(String joinTable, HashSet<String> pitstopInp, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_CARD_IDS_BY_STUC, nativeQuery = true)
  Set<String> getCardIdsByPsUcSt(
      String joinTable, String successtrackInp, Set<String> usecaseInp, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_ST_UC_WITH_COUNT_BY_CARDS, nativeQuery = true)
  List<Map<String, Object>> getAllStUcWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_PD_PS_WITH_COUNT_BY_CARDS, nativeQuery = true)
  List<Map<String, Object>> getAllPitstopsWithCountByCards(
      String joinTable, Set<String> cardIds, String hcaasStatus);

  /** for preferences * */
  @Query(
      value =
          "select distinct roles from cxpp_db.cxpp_learning_roles where "
              + ProductDocumentationConstants.HCAAS_CLAUSE
              + " and roles is not null order by roles",
      nativeQuery = true)
  List<String> getAllRolesForPreferences(String hcaasStatus);

  @Query(
      value =
          "select distinct technology from cxpp_db.cxpp_learning_technology where "
              + ProductDocumentationConstants.HCAAS_CLAUSE
              + " and technology is not null order by technology",
      nativeQuery = true)
  List<String> getAllTechnologyForPreferences(String hcaasStatus);

  @Query(
      value =
          "select distinct piw_region from cxpp_db.cxpp_learning_item where "
              + ProductDocumentationConstants.HCAAS_CLAUSE
              + " and piw_region is not null order by piw_region",
      nativeQuery = true)
  List<String> getAllRegionForPreferences(String hcaasStatus);

  @Query(
      value =
          "select distinct piw_language from cxpp_db.cxpp_learning_item where "
              + ProductDocumentationConstants.HCAAS_CLAUSE
              + " and piw_language is not null order by piw_language",
      nativeQuery = true)
  List<String> getAllLanguagesForPreferences(String hcaasStatus);

  @Query(
      value = "select role from cxpp_db_um.cxpp_platform_roles where roleid=:userRoleId",
      nativeQuery = true)
  String getUserRole(String userRoleId);

  @Query(value = ProductDocumentationConstants.GET_UPCOMING_WEBINARS, nativeQuery = true)
  List<LearningItemEntity> getUpcomingWebinars(String joinTable, String hcaasStatus);

  @Query(value = ProductDocumentationConstants.GET_SPECIALIZED_CARDS, nativeQuery = true)
  Set<String> getCardIdsBySpecialization(Set<String> specializations);

  /** for toppicks viewmore * */
  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_REGION, nativeQuery = true)
  Set<String> getTpCardIdsByRegion(
      String joinTable, Set<String> values, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_LG, nativeQuery = true)
  Set<String> getTpCardIdsByLanguage(
      String joinTable, Set<String> values, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_TC, nativeQuery = true)
  Set<String> getTpCardIdsByTC(
      String joinTable, Set<String> values, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_ROLE, nativeQuery = true)
  Set<String> getTpCardIdsByRole(
      String joinTable, Set<String> values, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_LEARNING_BY_CONTENT_TYPE, nativeQuery = true)
  Set<String> getTpLearningsByContentType(
      String joinTable, Set<String> contentTypeFilter, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_BY_PITSTOP, nativeQuery = true)
  Set<String> getTpCardIdsByPs(
      String joinTable, Set<String> pitstopInp, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_CISCOPLUS, nativeQuery = true)
  Set<String> getTpCardIdsByCiscoPlus(
      String joinTable, Set<String> values, String hcaasStatus, Set<String> cardIds);

  @Query(value = ProductDocumentationConstants.GET_TP_CARD_IDS_BY_STUC, nativeQuery = true)
  Set<String> getTpCardIdsByPsUcSt(
      String joinTable,
      String successtrackInp,
      Set<String> usecaseInp,
      String hcaasStatus,
      Set<String> cardIds);
}
