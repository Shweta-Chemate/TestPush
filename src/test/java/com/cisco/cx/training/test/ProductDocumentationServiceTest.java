package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.ProductDocumentationDAO;
import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntityPK;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.app.repo.PeerViewedRepo;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.services.common.featureflag.FeatureFlagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductDocumentationServiceTest {
  @Mock private PropertyConfiguration config;

  @Mock private LearningBookmarkDAO learningDAO;

  @Mock private NewLearningContentRepo learningContentRepo;

  @Mock private ProductDocumentationDAO productDocumentationDAO;

  @Mock private PartnerProfileService partnerProfileService;

  @Mock PeerViewedRepo peerViewedRepo;

  @Mock private HttpServletRequest request;

  @Mock private ServletContext servletContext;

  @Mock private FeatureFlagService featureFlagService;

  private static final String MASHERY_TEST = "test-mashery";

  @InjectMocks private ProductDocumentationService productDocumentationService;

  @Autowired ResourceLoader resourceLoader;

  String learningTab = "Technology";

  private String XMasheryHeader;

  @BeforeEach
  public void init() throws IOException {
    ServletContext context = Mockito.mock(ServletContext.class);
    when(request.getServletContext()).thenReturn(context);
    when(context.getAttribute(Constants.MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn("test");
    this.XMasheryHeader =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
  }

  private String loadFromFile(String filePath) throws IOException {
    return new String(
        Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
  }

  @Test
  void getAllLearningInfo() {
    LearningRecordsAndFiltersModel a1 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, null, null, "sortBy", "sortOrder", learningTab, true);
    assertEquals(0, a1.getLearningData().size());

    LearningRecordsAndFiltersModel a2 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, "searchToken", null, "sortBy", "sortOrder", learningTab, true);
    assertEquals(0, a2.getLearningData().size());

    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("For You", Arrays.asList(new String[] {"New"}));

    LearningRecordsAndFiltersModel a3 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, null, aMock, "sortBy", "sortOrder", learningTab, true);
    assertEquals(0, a3.getLearningData().size());

    LearningRecordsAndFiltersModel a4 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, "searchToken", aMock, "sortBy", "sortOrder", learningTab, true);
    assertEquals(0, a4.getLearningData().size());

    NewLearningContentEntity n1 = new NewLearningContentEntity();
    n1.setId("101");
    List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();
    result.add(n1);
    when(learningContentRepo.findNew(Mockito.anyString())).thenReturn(result);
    Set<String> hs = new HashSet<String>();
    hs.add("101");
    when(productDocumentationDAO.getAllNewCardIdsByCards(
            Mockito.anyString(), Mockito.anySet(), Mockito.anyString()))
        .thenReturn(hs);

    LearningRecordsAndFiltersModel a5 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, "searchToken", aMock, "sortBy", "sortOrder", learningTab, true);
    assertEquals(0, a5.getLearningData().size());

    List<LearningItemEntity> dbCards = new ArrayList<LearningItemEntity>();
    LearningItemEntity learningItemEntity = new LearningItemEntity();
    learningItemEntity.setSortByDate("2016-02-03 00:00:00.0");
    learningItemEntity.setLearning_type("product_documentation");
    dbCards.add(learningItemEntity);
    LearningItemEntity learningItemEntitySuccesstips = new LearningItemEntity();
    learningItemEntitySuccesstips.setSortByDate("2016-02-03 00:00:00.0");
    learningItemEntitySuccesstips.setLearning_type("success_tips");
    learningItemEntitySuccesstips.setAsset_types("Video,test2");
    learningItemEntitySuccesstips.setAsset_links("testVideo,test2");
    learningItemEntitySuccesstips.setAsset_description("testVideo:test2");
    learningItemEntitySuccesstips.setAsset_titles("testVideo:test2");
    dbCards.add(learningItemEntitySuccesstips);
    when(productDocumentationDAO.getAllLearningCardsByFilter(
            Mockito.anyString(), Mockito.anySet(), Mockito.any(Sort.class), Mockito.anyString()))
        .thenReturn(dbCards);
    when(featureFlagService.isOn(Mockito.anyString())).thenReturn(true);
    LearningRecordsAndFiltersModel a6 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, null, aMock, "sortBy", "sortOrder", learningTab, true);
    assertEquals(2, a6.getLearningData().size());
  }

  @Test
  void getAllLearningFilters() {

    Map<String, Object> a1 =
        productDocumentationService.getAllLearningFilters(null, null, learningTab, true);
    assertTrue(a1.size() >= 1); // st=7

    Map<String, Object> a2 =
        productDocumentationService.getAllLearningFilters("searchToken", null, learningTab, true);
    assertTrue(a2.size() >= 1); // st=7

    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("For You", Arrays.asList(new String[] {"New"}));
    Map<String, Object> a3 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a3.size() >= 1); // st=7

    Map<String, Object> a4 =
        productDocumentationService.getAllLearningFilters("searchToken", aMock, learningTab, true);
    assertTrue(a4.size() >= 1); // st=7
  }

  @Test
  void testLGFilter() {
    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("Language", Arrays.asList(new String[] {"English"}));
    List<Map<String, Object>> dbListLG = new ArrayList<Map<String, Object>>();
    Map<String, Object> lgMap = new HashMap<String, Object>();
    lgMap.put("dbkey", "English");
    lgMap.put("dbvalue", "2");
    dbListLG.add(lgMap);
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(productDocumentationDAO.getAllLanguageWithCount(learningTab, "true")).thenReturn(dbListLG);
    Map<String, Object> a3 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a3.size() >= 1);
  }

  @Test
  void testYouFilter() {
    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("For You", Arrays.asList(new String[] {"New"}));
    aMock.put("Language", Arrays.asList(new String[] {"English"}));
    String hcaasStatus = "True";
    NewLearningContentEntity n1 = new NewLearningContentEntity();
    n1.setId("101");
    List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();
    result.add(n1);
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(learningContentRepo.findNew(hcaasStatus)).thenReturn(result);

    Map<String, Object> a3 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a3.size() >= 1); // st=7

    aMock.put("For You", Arrays.asList(new String[] {"New", "Bookmarked", "Sth"}));
    Map<String, Object> a32 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a32.size() >= 1); // st=7

    when(learningContentRepo.findNew(hcaasStatus)).thenReturn(null);
    Map<String, Object> a31 =
        productDocumentationService.getAllLearningFilters(null, null, learningTab, true);
    assertTrue(a31.size() >= 1); // st=7
  }

  @Test
  void testAllFilters() {
    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("For You", Arrays.asList(new String[] {"New"}));
    aMock.put("Language", Arrays.asList(new String[] {"English"}));
    aMock.put("Technology", Arrays.asList(new String[] {"Enterprise Network"}));
    aMock.put("Documentation", Arrays.asList(new String[] {"Device setup"}));
    aMock.put("Live Events", Arrays.asList(new String[] {"APAC"}));
    aMock.put("Content Type", Arrays.asList(new String[] {"PPT"}));
    aMock.put("Success Tracks", mockST());
    String hcaasStatus = "True";
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    NewLearningContentEntity n1 = new NewLearningContentEntity();
    n1.setId("101");
    List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();
    result.add(n1);
    when(learningContentRepo.findNew(hcaasStatus)).thenReturn(result);
    /*
    when(productDocumentationDAO.getAllStUcPsWithCount(Mockito.anyString())).thenReturn(mockDbST());
    when(productDocumentationDAO.getAllStUcPsWithCountByCards(Mockito.anyString(),Mockito.anySet())).thenReturn(mockDbST());
    */
    Map<String, Object> a3 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a3.size() >= 1); // st=7
  }

  private List mockDbST() {
    List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
    Map<String, Object> oneRecord = new HashMap<String, Object>();
    oneRecord.put("successtrack", "Campus Network");
    oneRecord.put("usecase", "CSIM");
    oneRecord.put("pitstop", "Implement");
    oneRecord.put("dbvalue", "10");
    value.add(oneRecord);
    return value;
  }

  private Map mockST() {
    Map<String, Map<String, List<String>>> st = new HashMap<String, Map<String, List<String>>>();
    // map.put("Success Tracks", st);

    Map<String, List<String>> ucCN = new HashMap<String, List<String>>();
    List<String> csimPS = new ArrayList<String>();
    csimPS.add("Onboard");
    csimPS.add("Implement");
    ucCN.put("CSIM", csimPS);
    List<String> xyzPS = new ArrayList<String>();
    xyzPS.add("Use");
    ucCN.put("XYZ", xyzPS);
    st.put("Campus Network", ucCN);

    Map<String, List<String>> ucSY = new HashMap<String, List<String>>();
    List<String> sy1PS = new ArrayList<String>();
    sy1PS.add("Anti-Virus");
    sy1PS.add("Firewall");
    ucSY.put("Security1", sy1PS);
    List<String> abcPS = new ArrayList<String>();
    abcPS.add("Umbrella");
    ucSY.put("ABC", abcPS);
    st.put("Security", ucSY);

    return st;
  }

  @Test
  void testSortSpecial() {
    when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(null);
    List<LearningItemEntity> aL = new ArrayList<LearningItemEntity>();
    LearningItemEntity le1 = new LearningItemEntity();
    LearningItemEntity le2 = new LearningItemEntity();
    le1.setLearning_type("product_documentation");
    le2.setLearning_type("product_documentation");
    le1.setTitle("abc");
    le2.setTitle("xyz");
    aL.add(le1);
    aL.add(le2);
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(productDocumentationDAO.getAllLearningCards(
            Mockito.anyString(), Mockito.any(Sort.class), Mockito.anyString()))
        .thenReturn(aL);
    when(featureFlagService.isOn(Mockito.anyString())).thenReturn(true);
    LearningRecordsAndFiltersModel a2t =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, null, null, "title", "asc", learningTab, true);
    assertEquals("abc", a2t.getLearningData().get(0).getTitle());

    LearningRecordsAndFiltersModel a2t2 =
        productDocumentationService.getAllLearningInfo(
            this.XMasheryHeader, null, null, "title", "desc", learningTab, true);
    assertEquals("xyz", a2t2.getLearningData().get(0).getTitle());
  }

  @Test
  void fetchMyPreferredLearnings() throws JsonProcessingException {
    HashMap<String, Object> preferences = new HashMap<String, Object>();
    List<String> roles = new ArrayList<String>();
    preferences.put("role", roles);
    roles.add("Customer Success Manager");
    List<String> ti = new ArrayList<String>();
    preferences.put("timeinterval", ti);
    Map<String, String> time = new HashMap<String, String>();
    time.put("startTime", "9:00 AM");
    time.put("endTime", "4:00 PM");
    time.put("timeZone", "PDT(UTC-07:30)");
    ti.add(new ObjectMapper().writeValueAsString(time));

    when(request.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");

    Assertions.assertNotNull(
        productDocumentationService
            .fetchMyPreferredLearnings("userId", null, null, preferences, 25, true)
            .getLearningData());
  }

  @Test
  void addPeerLearnings() throws JsonProcessingException {
    when(request.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
    productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid");
    PeerViewedEntity en = new PeerViewedEntity();
    en.setCardId("cardId");
    en.setRoleName("role");
    en.setUpdatedTime(Timestamp.valueOf("2019-10-24 18:30:00"));
    Optional<PeerViewedEntity> enOp = Optional.of(en);
    when(peerViewedRepo.findById(Mockito.any(PeerViewedEntityPK.class))).thenReturn(enOp);
    Assertions.assertDoesNotThrow(
        () -> productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid"));
  }

  @Test
  void codeCoverTest() throws JsonProcessingException {
    when(request.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");

    when(peerViewedRepo.save(Mockito.any(PeerViewedEntity.class)))
        .thenThrow(new RuntimeException("Some test Exc"));
    productDocumentationService.addLearningsViewedForRole("userId", "cardId", "puid");

    HashMap<String, Object> preferences = new HashMap<String, Object>();
    List<String> ti = new ArrayList<String>();
    preferences.put("timeinterval", ti);
    Map<String, String> time = new HashMap<String, String>();
    time.put("startTime", "9:00 AM");
    time.put("endTime", "4:00 PM");
    time.put("timeZone", "PDT(UTC-7)");
    ti.add(new ObjectMapper().writeValueAsString(time));

    Assertions.assertNotNull(
        productDocumentationService.fetchMyPreferredLearnings(
            "userId", null, null, preferences, 25, true));
  }

  @Test
  void codeCovertest2() {

    List<PeerViewedEntity> a = new ArrayList<PeerViewedEntity>();
    for (int i = 0; i <= 55; i++) {
      PeerViewedEntity en = new PeerViewedEntity();
      en.setCardId(100 + i + "");
      en.setRoleName("role101");
      en.setUpdatedTime(Timestamp.valueOf("2019-10-24 18:30:00"));
      a.add(en);
    }
    List<LearningItemEntity> v = new ArrayList<LearningItemEntity>();
    for (int i = 0; i <= 55; i++) {
      LearningItemEntity ln = new LearningItemEntity();
      ln.setLearning_item_id(100 + i + "");
      ln.setLearning_type("product_documentation");
      v.add(ln);
    }
    when(request.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");
    when(featureFlagService.isOn(Mockito.anyString())).thenReturn(true);
    when(productDocumentationDAO.getUserRole(Mockito.anyString())).thenReturn("role101");
    when(peerViewedRepo.findByRoleName(Mockito.anyString())).thenReturn(a);
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(productDocumentationDAO.getAllLearningCardsByFilter(
            Mockito.anyString(), Mockito.anySet(), Mockito.any(Sort.class), Mockito.anyString()))
        .thenReturn(v);
    Assertions.assertNotNull(
        productDocumentationService.fetchMyPreferredLearnings("userId", null, null, null, 5, true));
  }

  private List mockDbSTUCOnly() {
    List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
    Map<String, Object> oneRecord = new HashMap<String, Object>();
    oneRecord.put("successtrack", "Campus Network");
    oneRecord.put("usecase", "CSIM");
    oneRecord.put("dbvalue", "10");
    value.add(oneRecord);
    return value;
  }

  private Map mockSTUCOnly() {
    Map<String, Map<String, List<String>>> st = new HashMap<String, Map<String, List<String>>>();
    // map.put("Success Tracks", st);

    Map<String, List<String>> ucCN = new HashMap<String, List<String>>();
    List<String> csimPS = new ArrayList<String>();
    csimPS.add("Onboard");
    csimPS.add("Implement");
    ucCN.put("CSIM", csimPS);
    List<String> xyzPS = new ArrayList<String>();
    xyzPS.add("Use");
    ucCN.put("XYZ", xyzPS);
    st.put("Campus Network", ucCN);

    Map<String, List<String>> ucSY = new HashMap<String, List<String>>();
    List<String> sy1PS = new ArrayList<String>();
    sy1PS.add("Anti-Virus");
    sy1PS.add("Firewall");
    ucSY.put("Security1", sy1PS);
    List<String> abcPS = new ArrayList<String>();
    abcPS.add("Umbrella");
    ucSY.put("ABC", abcPS);
    st.put("Security", ucSY);

    return st;
  }

  @Test
  void testAllFiltersCount() {
    NewLearningContentEntity n1 = new NewLearningContentEntity();
    n1.setId("101");
    List<NewLearningContentEntity> result = new ArrayList<NewLearningContentEntity>();
    result.add(n1);
    String hcaasStatus = "True";
    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(learningContentRepo.findNew(hcaasStatus)).thenReturn(result);

    when(productDocumentationDAO.getAllStUcWithCount(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(mockDbSTUCOnly());
    when(productDocumentationDAO.getAllStUcWithCountByCards(
            Mockito.anyString(), Mockito.anySet(), Mockito.anyString()))
        .thenReturn(mockDbSTUCOnly());

    Map<String, Object> a3 =
        productDocumentationService.getAllLearningFilters(null, null, learningTab, true);
    assertTrue(a3.size() >= 1); // st=7

    HashMap<String, Object> aMock = new HashMap<String, Object>();
    aMock.put("Success Tracks", mockSTUCOnly());
    Map<String, Object> a4 =
        productDocumentationService.getAllLearningFilters(null, aMock, learningTab, true);
    assertTrue(a4.size() >= 1); // st=7
  }

  @Test
  void getRangeLWTest() throws JsonProcessingException {
    HashMap<String, Object> preferences = new HashMap<String, Object>();
    List<String> ti = new ArrayList<String>();
    preferences.put("timeinterval", ti);
    Map<String, String> time = new HashMap<String, String>();
    time.put("startTime", "12:00 AM");
    time.put("endTime", "12:30 AM");
    time.put("timeZone", "PDT(UTC-7)");
    ti.add(new ObjectMapper().writeValueAsString(time));

    List<LearningItemEntity> len = new ArrayList<LearningItemEntity>();
    LearningItemEntity ln = new LearningItemEntity();
    len.add(ln);
    ln.setLearning_item_id("101");
    ln.setSortByDate("2019-10-24 18:30:00");

    when(partnerProfileService.getHcaasStatusForPartner(MASHERY_TEST)).thenReturn(true);
    when(productDocumentationDAO.getUpcomingWebinars(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(len);

    when(request.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute(Constants.ROLE_ID)).thenReturn("101");

    Assertions.assertNotNull(
        productDocumentationService.fetchMyPreferredLearnings(
            "userId", null, null, preferences, 25, true));
  }
}
