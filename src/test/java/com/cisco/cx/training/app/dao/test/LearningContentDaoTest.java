package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;

import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.impl.FilterCountsDAOImpl;
import com.cisco.cx.training.app.dao.impl.NewLearningContentDAOImpl;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.LearningContentItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class LearningContentDaoTest {

  @Mock private NewLearningContentRepo learningContentRepo;

  @Mock private FilterCountsDAOImpl filterCountsDao;

  @Mock private LearningBookmarkDAO learningBookmarkDAO;

  @InjectMocks
  private NewLearningContentDAO learningContentDAO =
      new NewLearningContentDAOImpl(learningContentRepo);

  private static final String hcaasStatusTest = "true";

  private static final boolean hcaasStatusBoolean = true;

  @Test
  void testListPIWs() {
    List<NewLearningContentEntity> piwList = new ArrayList<>();
    LinkedHashMap<String, String> filter = new LinkedHashMap();
    filter.put("testKey", "testValue");
    Specification<NewLearningContentEntity> specification =
        new SpecificationBuilderPIW().filter(filter, "testSearch", "testRegion");
    when(learningContentRepo.findAll(
            specification, Sort.by(Sort.Direction.fromString("asc"), "testField")))
        .thenReturn(piwList);
    Assertions.assertNotNull(
        learningContentDAO.listPIWs("testRegion", "testField", "asc", filter, "testSearch"));
  }

  @Test
  void testFetchSuccesstalks() {
    List<NewLearningContentEntity> successtalkList = new ArrayList<>();
    LinkedHashMap<String, String> filter = new LinkedHashMap();
    filter.put("testKey", "testValue");
    Specification<NewLearningContentEntity> specification =
        new SpecificationBuilderPIW().filter(filter, "testSearch", "testRegion");
    when(learningContentRepo.findAll(
            specification, Sort.by(Sort.Direction.fromString("asc"), "testField")))
        .thenReturn(successtalkList);
    Assertions.assertNotNull(
        learningContentDAO.fetchSuccesstalks("testField", "asc", filter, "testSearch"));
  }

  @Test
  void testFetchNewLearningContentWithoutFilter() {
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    String hcaasStatus = String.valueOf(true);
    when(learningContentRepo.findNew(hcaasStatus)).thenReturn(newContentList);
    Map stMapTest = new HashMap<>();
    List<NewLearningContentEntity> resp =
        learningContentDAO.fetchNewLearningContent(filterParams, stMapTest, hcaasStatusTest);
    Assertions.assertNotNull(resp);
  }

  @Test
  void testFetchNewLearningContentWithFilter() {
    Map<String, List<String>> filterParams = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testvalue");
    testValues.add(Constants.CAMPUS_NETWORK);
    filterParams.put("testkey", testValues);
    filterParams.put(Constants.SUCCESS_TRACK, testValues);
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    newContentList.add(getLearningEntity());
    when(learningContentRepo.findAll()).thenReturn(newContentList);
    when(learningContentRepo.findNewFiltered(Mockito.any(), Mockito.any()))
        .thenReturn(newContentList);
    Map stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchNewLearningContent(filterParams, stMapTest, hcaasStatusTest));
  }

  @Test
  void testGetSuccessTalkCount() {
    when(learningContentRepo.countByLearningType(Constants.SUCCESSTALK)).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getSuccessTalkCount());
  }

  @Test
  void testGetPIWCount() {
    when(learningContentRepo.countByLearningType(Constants.PIW)).thenReturn(1);
    Assertions.assertEquals(1, learningContentDAO.getPIWCount());
  }

  @Test
  void testGetProductDocumentationCount() {
    when(learningContentRepo.countByLearningType(Constants.DOCUMENTATION)).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getDocumentationCount());
  }

  @Test
  void testGetViewMoreNewFiltersWithCount() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreNewFiltersWithCountAndFilterAndNoCards() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    Assertions.assertNotNull(
        learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreNewFiltersWithCountAndFilterAndCards() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
    Assertions.assertNotNull(
        learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreRecentlyFiltersWithCount() {
    String userId = "testUserId";
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getRecentlyViewedFiltersWithCount(
            userId, filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreRecentlyFiltersWithCountAndNoCards() {
    String userId = "testUserId";
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    Assertions.assertNotNull(
        learningContentDAO.getRecentlyViewedFiltersWithCount(
            userId, filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreRecentlyFiltersWithCountAndFilterAndCards() {
    String userId = "testUserId";
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
    Assertions.assertNotNull(
        learningContentDAO.getRecentlyViewedFiltersWithCount(
            userId, filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreUpcomingFiltersWithCount() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getUpcomingFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreUpcomingFiltersWithCountAndNoCards() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    Assertions.assertNotNull(
        learningContentDAO.getUpcomingFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreUpcomingFiltersWithCountAndFilterAndCards() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
    Assertions.assertNotNull(
        learningContentDAO.getUpcomingFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreBookmarkedFiltersWithCount() {
    List<LearningContentItem> bookmarkedList = new ArrayList<>();
    bookmarkedList.add(getLearningItem());
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getBookmarkedFiltersWithCount(
            filterSelected, bookmarkedList, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreBookmarkedFiltersWithCountAndNoCards() {
    List<LearningContentItem> bookmarkedList = new ArrayList<>();
    bookmarkedList.add(getLearningItem());
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    Assertions.assertNotNull(
        learningContentDAO.getBookmarkedFiltersWithCount(
            filterSelected, bookmarkedList, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreBookmarkedFiltersWithCountAndFilterAndCards() {
    List<LearningContentItem> bookmarkedList = new ArrayList<>();
    bookmarkedList.add(getLearningItem());
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
    Assertions.assertNotNull(
        learningContentDAO.getBookmarkedFiltersWithCount(
            filterSelected, bookmarkedList, hcaasStatusTest));
  }

  @Test
  void testGetViewMoreCXInsightsFiltersWithCount() {
    String userId = "testUserId";
    String searchToken = "testSearchToken";
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getCXInsightsFiltersWithCount(
            userId, searchToken, filterSelected, hcaasStatusBoolean));
  }

  @Test
  void testGetViewMoreCXInsightsFiltersWithCountAndNoCards() {
    String userId = "testUserId";
    String searchToken = "testSearchToken";
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    Assertions.assertNotNull(
        learningContentDAO.getCXInsightsFiltersWithCount(
            userId, searchToken, filterSelected, hcaasStatusBoolean));
  }

  @Test
  void testGetViewMoreCXInsightsFiltersWithCountAndFilterAndCards() {
    String userId = "testUserId";
    String searchToken = "testSearchToken";
    HashMap<String, Object> filterSelected = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    testValues.add("testValue");
    filterSelected.put("testKey", testValues);
    when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
    Assertions.assertNotNull(
        learningContentDAO.getCXInsightsFiltersWithCount(
            userId, searchToken, filterSelected, hcaasStatusBoolean));
  }

  @Test
  void testFetchRecentlyViewedLearningContentWithoutFilter() {
    String userId = "testUserId";
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(newContentList);
    HashMap<String, String> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchRecentlyViewedContent(
            userId, filterParams, stMapTest, hcaasStatusTest));
  }

  @Test
  void testFetchRecentlyViewedLearningContentWithFilter() {
    String userId = "testUserId";
    Map<String, List<String>> filterParams = new HashMap<>();
    List<String> testValues = new ArrayList<>();
    List<String> testValuesForYouFilter = new ArrayList<>();
    testValuesForYouFilter.add(Constants.NEW);
    testValuesForYouFilter.add(Constants.BOOKMARKED_FOR_YOU);
    testValuesForYouFilter.add(Constants.RECENTLY_VIEWED);
    testValues.add("testvalue");
    testValues.add(Constants.CAMPUS_NETWORK);
    filterParams.put("testkey", testValues);
    filterParams.put(Constants.ROLE, testValues);
    filterParams.put(Constants.TECHNOLOGY, testValues);
    filterParams.put(Constants.LIFECYCLE, testValues);
    filterParams.put(Constants.FOR_YOU_FILTER, testValuesForYouFilter);
    filterParams.put(Constants.SUCCESS_TRACK, testValues);
    Set<String> testCardIds = new HashSet<>();
    testCardIds.add("testString");
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    newContentList.add(getLearningEntity());
    when(learningContentRepo.findAll()).thenReturn(newContentList);
    when(learningContentRepo.getRecentlyViewedContentFiltered(
            Mockito.anyString(), Mockito.any(), Mockito.anyString()))
        .thenReturn(newContentList);
    when(learningContentRepo.findNew(Mockito.anyString())).thenReturn(newContentList);
    when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(testCardIds);
    when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(newContentList);
    HashMap<String, String> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchRecentlyViewedContent(
            userId, filterParams, stMapTest, hcaasStatusTest));
  }

  @Test
  void testFetchCXInsightsContent() {
    String userId = "testUserId";
    String searchToken = "testSearchToken";
    String sortField = "title";
    String sortType = "asc";
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(newContentList);
    HashMap<String, String> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchCXInsightsContent(
            userId, filterParams, stMapTest, searchToken, sortField, sortType, false));
  }

  @Test
  void testFetchFilteredContent() {
    Map<String, List<String>> filterParams = new HashMap<>();
    HashMap<String, String> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchFilteredContent(filterParams, stMapTest, false));
  }

  @Test
  void testUpcomingContent() {
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    when(learningContentRepo.findUpcomingFiltered(Mockito.any(), Mockito.anyString()))
        .thenReturn(newContentList);
    HashMap<String, String> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchUpcomingContent(filterParams, stMapTest, hcaasStatusTest));
  }

  @Test
  void testFetchPopularAcrossPartnersContent() {
    ReflectionTestUtils.setField(
        learningContentDAO, "popularAcrossPartnersCategoryLiimit", Integer.valueOf(10));
    Map<String, List<String>> filterParams = new HashMap<>();
    NewLearningContentEntity entity = getLearningEntity();
    List<NewLearningContentEntity> newContentList = Stream.of(entity).collect(Collectors.toList());
    HashSet<String> bookmarks = new HashSet<String>();
    bookmarks.add("test");
    when(learningContentRepo.getMaxBookmark()).thenReturn(5);
    when(learningContentRepo.getPopularAcrossPartnersFiltered(
            new HashSet<>(), Integer.valueOf(10), 11, 5, bookmarks, "true"))
        .thenReturn(newContentList);
    HashMap<String, String> stMapTest = new HashMap<>();
    List<NewLearningContentEntity> resp =
        learningContentDAO.fetchPopularAcrossPartnersContent(
            filterParams, stMapTest, bookmarks, hcaasStatusTest);
    Assertions.assertTrue(!resp.isEmpty());
  }

  @Test
  void testGetPopularAcrossPartnersFiltersWithCount() {
    ReflectionTestUtils.setField(
        learningContentDAO, "popularAcrossPartnersCategoryLiimit", Integer.valueOf(10));
    HashSet<String> bookmarks = new HashSet<String>();
    bookmarks.add("test");
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getPopularAcrossPartnersFiltersWithCount(
            filterSelected, bookmarks, hcaasStatusTest));
  }

  @Test
  void testGetPopularAcrossPartnersFiltersSelectedWithCount() {
    ReflectionTestUtils.setField(
        learningContentDAO, "popularAcrossPartnersCategoryLiimit", Integer.valueOf(10));
    HashMap<String, Object> selection = new HashMap<>();
    selection.put("test", Stream.of("test").collect(Collectors.toList()));
    HashSet<String> bookmarks = new HashSet<String>();
    bookmarks.add("test");
    Map<String, Set<String>> filteredCardsMap = new HashMap<>();
    filteredCardsMap.put("test", Stream.of("test").collect(Collectors.toSet()));
    when(filterCountsDao.filterCards(selection, new HashSet<>(), null, "true"))
        .thenReturn(filteredCardsMap);
    HashMap<String, Object> resp =
        learningContentDAO.getPopularAcrossPartnersFiltersWithCount(
            selection, bookmarks, hcaasStatusTest);
    Assertions.assertNotNull(resp);
  }

  @Test
  void testFetchPopularAtPartnerContent() {
    ReflectionTestUtils.setField(
        learningContentDAO, "popularAtPartnerCompanyLimit", Integer.valueOf(10));
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> newContentList = new ArrayList<>();
    HashSet<String> bookmarks = new HashSet<String>();
    bookmarks.add("test");
    Map stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchPopularAtPartnerContent(
            filterParams, stMapTest, "puid", bookmarks, hcaasStatusTest));
  }

  @Test
  void testGetPopularAtPartnerFiltersWithCount() {
    ReflectionTestUtils.setField(
        learningContentDAO, "popularAtPartnerCompanyLimit", Integer.valueOf(10));
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getPopularAtPartnerFiltersWithCount(
            filterSelected, "test", new HashSet<String>(), hcaasStatusTest));
  }

  @Test
  void testGetLearningMap() {
    String id = "testId";
    String title = "testTitle";
    List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
    learningEntityList.add(getLearningEntity());
    when(learningContentRepo.findById(id)).thenReturn(Optional.of(getLearningEntity()));
    when(learningContentRepo.findByLearningTypeAndLearningMap(
            Constants.LEARNINGMODULE, getLearningEntity().getTitle()))
        .thenReturn(learningEntityList);
    Assertions.assertNotNull(learningContentDAO.getLearningMap(id, title));
  }

  @Test
  void testGetLearningMapIDNull() {
    String title = "testTitle";
    List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
    learningEntityList.add(getLearningEntity());
    when(learningContentRepo.findByLearningTypeAndLearningMap(
            Constants.LEARNINGMODULE, getLearningEntity().getTitle()))
        .thenReturn(learningEntityList);
    Assertions.assertNotNull(learningContentDAO.getLearningMap(null, title));
  }

  @Test
  void testGetSuccessTrackCount() {
    when(learningContentRepo.getSuccessTracksCount()).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getSuccessTracksCount());
  }

  @Test
  void testGetLifecycleCount() {
    when(learningContentRepo.getLifecycleCount()).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getLifecycleCount());
  }

  @Test
  void testGetTechnologyCount() {
    when(learningContentRepo.getTechnologyount()).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getTechnologyCount());
  }

  @Test
  void testGetRoleCount() {
    when(learningContentRepo.getRolesCount()).thenReturn(1);
    Assertions.assertNotNull(learningContentDAO.getRolesCount());
  }

  @Test
  void testFetchFeaturedContent() {
    Map<String, List<String>> filterParams = new HashMap<>();
    List<NewLearningContentEntity> featuredContentList = new ArrayList<>();
    when(learningContentRepo.findFeatured(Mockito.anyString())).thenReturn(featuredContentList);
    HashMap<String, Object> stMapTest = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.fetchFeaturedContent(filterParams, stMapTest, hcaasStatusTest));
  }

  @Test
  void testFetchFeaturedContentWithoutFilter() {
    List<NewLearningContentEntity> featuredContentList = new ArrayList<>();
    when(learningContentRepo.findFeatured(Mockito.anyString())).thenReturn(featuredContentList);
    Assertions.assertNotNull(
        learningContentDAO.fetchFeaturedContent(Collections.emptyMap(), null, hcaasStatusTest));
  }

  @Test
  void testGetFeaturedFiltersWithoutFilter() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    Assertions.assertNotNull(
        learningContentDAO.getFeaturedFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  @Test
  void testGetFeaturedFiltersWithCount() {
    HashMap<String, Object> filterSelected = new HashMap<>();
    filterSelected.put("Content Type", "PDF");
    Map<String, Set<String>> filteredCardsMap = new HashMap<>();
    filteredCardsMap.put("test", Stream.of("test").collect(Collectors.toSet()));
    when(filterCountsDao.filterCards(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(filteredCardsMap);
    Assertions.assertNotNull(
        learningContentDAO.getFeaturedFiltersWithCount(filterSelected, hcaasStatusTest));
  }

  NewLearningContentEntity getLearningEntity() {
    NewLearningContentEntity learning = new NewLearningContentEntity();
    learning.setId("testid");
    learning.setTitle("testTitle");
    learning.setSequence("1");
    return learning;
  }

  LearningContentItem getLearningItem() {
    LearningContentItem learning = new LearningContentItem(getLearningEntity());
    learning.setId("testid");
    return learning;
  }
}
