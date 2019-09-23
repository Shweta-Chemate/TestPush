package com.cisco.services.test;

import com.cisco.services.app.dao.ElasticSearchDAO;
import com.cisco.services.app.service.HttpService;
import com.cisco.services.app.service.OAuthService;
import com.cisco.services.models.OAuthBearerToken;
import com.cisco.services.models.ElasticSearchResults;
import com.cisco.services.models.EmailContent;
import com.cisco.services.models.EmailRequest;
import com.cisco.services.util.HasId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.cisco.services.app.TrainingAndEnablementApplication;
import com.cisco.services.app.config.PropertyConfiguration;
import com.cisco.services.app.rest.TrainingAndEnablementController;
import com.cisco.services.app.service.CiscoProfileService;
import com.cisco.services.app.service.EmailService;

import org.springframework.util.MultiValueMap;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = { TrainingAndEnablementController.class, Swagger2Controller.class})
@ContextConfiguration(classes = { TrainingAndEnablementApplication.class, EmailService.class, HttpService.class, ElasticSearchDAO.class, CiscoProfileService.class, PropertyConfiguration.class})
public class TrainingAndEnablementControllerTest {
    private final static Logger LOG = LoggerFactory.getLogger(TrainingAndEnablementControllerTest.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticSearchDAO elasticSearchDAO;

    @MockBean
    private CiscoProfileService ciscoProfileService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private HttpService httpService;

    @MockBean
    private OAuthService oAuthService;

    @Autowired
    ResourceLoader resourceLoader;

    private String XMasheryHeader;

    @Before
    public void init() throws IOException {
        this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    }

    @Test
    public void testApiReady() throws Exception {
        when(elasticSearchDAO.isElasticSearchRunning()).thenReturn(true);
        when(emailService.isEmailServerRunning()).thenReturn(true);

        this.mockMvc.perform(get("/v1/training/ready"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"elasticsearch\": \"OK\", \"email.api\": \"OK\"}"));
    }

    @Test
    public void testApiNOTReady() throws Exception {
        when(elasticSearchDAO.isElasticSearchRunning()).thenReturn(true);
        when(emailService.isEmailServerRunning()).thenReturn(false);

        this.mockMvc.perform(get("/v1/training/ready"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"elasticsearch\": \"OK\", \"email.api\": \"DOWN\"}"));
    }

    @Test
    public void testCreateEntity() throws Exception {
        /* Tips
           1. Refactor your code such that all your external API calls and DAO (DB, Elasticsearch) access are in separate service classes.
           2. Mock the DAO and service classes and declare them using @MockBean annotation (see example above)
           3. DO NOT mock any delegate or util classes. That is misuse of Mocking since the unit tests will not cover the mocked classes.
         */

        /** step 1 - set up the mocks for this unit test **/

        // following are just examples. customize these for your test scenario needs

        /* mocking the elasticsearch dao layer */
        when(elasticSearchDAO.isElasticSearchRunning()).thenReturn(true);
        when(elasticSearchDAO.doesIndexExist(eq("your_index_name_here"))).thenReturn(true);
        when(elasticSearchDAO.saveEntry(any(String.class), any(), any(Class.class))).thenReturn(new HasId() {
            @Override
            public String getDocId() { return "dummy-id"; }

            @Override
            public void setDocId(String id) { }
        });
        when(elasticSearchDAO.getDocument(any(String.class), any(String.class), any(Class.class))).thenReturn(new Object());
        when(elasticSearchDAO.query(any(String.class), any(SearchSourceBuilder.class), any(Class.class)))
				.thenReturn(new ElasticSearchResults(1, 1)
						.addDocument(
								new HasId() {
									@Override public String getDocId() {
										return "id-1";
									}

									@Override public void setDocId(String id) {
									}
								}));
        when(elasticSearchDAO.deleteByQuery(any(String.class), any(QueryBuilder.class))).thenReturn(5L);
        when(elasticSearchDAO.deleteOne(any(String.class), any(String.class))).thenReturn(DocWriteResponse.Result.DELETED);

        /* mocking the cisco profile service */
        when(ciscoProfileService.getUserProfile(any(String.class))).thenCallRealMethod();
        when(ciscoProfileService.getLDAPInfo(any(String.class))).thenReturn(mapper.readValue(loadFromFile("mock/user-ldap-response1.json"), HashMap.class));
        // example of mocking the ldap call throwing an exception
        //when(ciscoProfileService.getLDAPInfo(any(String.class))).thenThrow(new NamingException("Mock LDAP exception"));

        /* mocking the email service */
        when(emailService.isEmailServerRunning()).thenReturn(true);
        when(emailService.sendEmail(any(EmailRequest.class), any(MultiValueMap.class))).thenReturn("Email Sent");
        when(emailService.sendEmail(any(EmailRequest.class), any(MultiValueMap.class), any(String.class))).thenReturn("Email Sent");
        when(emailService.saveTemplate(any(String.class), any(EmailContent.class), any(MultiValueMap.class))).thenReturn("Template Saved");

        /* mocking the http service */
        when(httpService.makeHttpGetCall(eq("https://www.google.com"), any(Map.class), any(HttpHeaders.class), any(Class.class))).thenReturn("Mocked Google response");
        when(httpService.makeHttpPostCallUrlEncoded(eq("https://www.google.com"), any(MultiValueMap.class), any(HttpHeaders.class), any(Class.class))).thenReturn("Google Post response");
        when(httpService.makeHttpPostCallWithBody(eq("https://www.google.com"), any(Object.class), any(HttpHeaders.class), any(Class.class))).thenReturn("Google Post w/ body response");

        /* mocking the oauth service */
        when(oAuthService.getCiscoOAuthToken(any(String.class), any(String.class))).thenReturn(new OAuthBearerToken());
        when(oAuthService.getOAuthToken(any(String.class), any(MultiValueMap.class))).thenReturn(new OAuthBearerToken());

        /** step 2 - fire the api call with the necessary headers and body **/
        /** step 3 - expect response to determine if test is passed or failed **/
        this.mockMvc.perform(post("/v1/training/sample/entities")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Mashery-Handshake", this.XMasheryHeader)
                    .content("Test Post Body")
                    .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"createEntity\": \"success\"}"));
    }


    // ----------------------------------------------------------------------------Helper methods--------------------------------------------------------------

    private String loadFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
    }
}
