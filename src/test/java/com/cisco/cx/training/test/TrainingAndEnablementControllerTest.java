package com.cisco.cx.training.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.cisco.cx.training.app.TrainingAndEnablementApplication;
import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.config.Swagger2Config;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.filters.AuthFilter;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.app.repo.BookmarkCountsRepo;
import com.cisco.cx.training.app.rest.TrainingAndEnablementController;
import com.cisco.cx.training.app.service.SplitClientService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import springfox.documentation.swagger2.web.Swagger2Controller;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = { TrainingAndEnablementController.class, Swagger2Controller.class })
@ContextConfiguration(classes = { TrainingAndEnablementApplication.class,
		PropertyConfiguration.class, Swagger2Config.class})

public class TrainingAndEnablementControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	RBACFilter rbacFilter;
	
	@MockBean
	private SplitClientService splitService;
	
	@Autowired
	AuthFilter authFilter;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private CommunityDAO communityDAO;
	@Autowired
	ResourceLoader resourceLoader;
	
	@MockBean
	private TrainingAndEnablementService trainingAndEnablementService;

	@Mock
	private BookmarkCountsRepo bookmarkCountsRepo;

	private String XMasheryHeader;
	
	private String puid = "101";

	@BeforeEach
	public void init() throws IOException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(authFilter).build();
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));

	}

	@Test
	void testFetchCommunities() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/communities").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	void testFetchCommunitiesNoMasheryHeader() throws Exception {
		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/training/communities").contentType(MediaType.APPLICATION_JSON_VALUE)
					.characterEncoding("utf-8"));
		});
	}

	@Test
	void testCheckReady() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/ready").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void testLive() throws Exception {

		this.mockMvc
				.perform(get("/v1/partner/training/live").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	
	/*@Test
	public void testFetchSuccessTalks() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/successTalks").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}*/
	
	@Test
	void createorUpdateLearningBookmarkstatus500() throws Exception {
		BookmarkRequestSchema bookMark = new BookmarkRequestSchema();
		bookMark.setBookmark(true);
		bookMark.setId("1");
		bookMark.setLearningid("1");
		bookMark.setTitle("title");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(bookMark);

		this.mockMvc.perform(post("/v1/partner/training/learning/bookmark")
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson).param("email", "")
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isInternalServerError());
	}
	
	@Test
	void createorUpdateLearningBookmarkstatus200() throws Exception {
		BookmarkRequestSchema bookMark = new BookmarkRequestSchema();
		bookMark.setBookmark(true);
		bookMark.setId("1");
		bookMark.setLearningid("1");
		bookMark.setTitle("title");
		Mockito.when(trainingAndEnablementService.bookmarkLearningForUser(Mockito.any(BookmarkRequestSchema.class), Mockito.anyString(), Mockito.anyString())).thenReturn(new BookmarkResponseSchema());
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(bookMark);

		this.mockMvc.perform(post("/v1/partner/training/learning/bookmark")
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson).param("email", "")
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	void createorUpdateLearningBookmarkFailure() throws Exception {
		BookmarkRequestSchema bookMark = new BookmarkRequestSchema();
		bookMark.setBookmark(true);
		bookMark.setId("1");
		bookMark.setTitle("title");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = "";

		this.mockMvc.perform(post("/v1/partner/training/learning/bookmark")
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson).param("email", "")
						.header("X-Mashery-Handshake", this.XMasheryHeader)
						.header("puid", this.puid)
						.characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isBadRequest());
	}


	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}
	
	
	@Test
	void getAllLearningsInfoPost() throws Exception {
		this.mockMvc
				.perform(post("/v1/partner/training/getAllLearningInfo/Technology").contentType(MediaType.APPLICATION_JSON_VALUE)
						.with(new RequestPostProcessor() {
							public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
								request.getServletContext().setAttribute(Constants.HCAAS_FLAG, true);
								return request;
							}
						})
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	void getAllLearningFiltersPost() throws Exception {
		this.mockMvc
				.perform(post("/v1/partner/training/getAllLearningFilters/Technology").contentType(MediaType.APPLICATION_JSON_VALUE)
						.with(new RequestPostProcessor() {
							public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
								request.getServletContext().setAttribute(Constants.HCAAS_FLAG, true);
								return request;
							}
						})
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	void testLearningPreferences() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/myLearningPreferences").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).content(getUserPreference()).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
		
		this.mockMvc
		.perform(post("/v1/partner/training/myLearningPreferences").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader).content(getUserPreference()).characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	void testLearningPreferencesErr() throws Exception {	
		
        assertThatThrownBy(
            () -> mockMvc.perform(post("/v1/partner/training/myLearningPreferences").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("X-Mashery-Handshake", this.XMasheryHeader)
					.content(getUserPreferenceErr1()).characterEncoding("utf-8")))
        .hasCauseInstanceOf(BadRequestException.class);


		assertThrows(NestedServletException.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/training/myLearningPreferences").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("X-Mashery-Handshake", this.XMasheryHeader)
					.content(getUserPreferenceErr2()).characterEncoding("utf-8"))
			.andDo(print());
		});
		
	     assertThatThrownBy(
	             () -> mockMvc.perform(post("/v1/partner/training/myLearningPreferences").contentType(MediaType.APPLICATION_JSON_VALUE)
	 					.header("X-Mashery-Handshake", this.XMasheryHeader)
	 					.content(getUserPreferenceErr3()).characterEncoding("utf-8")))
	     .hasMessageContaining("Invalid Preference Time");

	
	}
	
	@Test
	void testTopPicks() throws Exception {
		
		this.mockMvc
		.perform(get("/v1/partner/training/myPreferredLearnings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(new RequestPostProcessor() {
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.getServletContext().setAttribute(Constants.HCAAS_FLAG, true);
						return request;
					}
				})
				.header("X-Mashery-Handshake", this.XMasheryHeader).header("puid", this.puid).characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());
		
		assertThrows(NestedServletException.class, () -> {
		this.mockMvc
		.perform(get("/v1/partner/training/myPreferredLearnings").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(new RequestPostProcessor() {
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.getServletContext().setAttribute(Constants.HCAAS_FLAG, true);
						return request;
					}
				})
				.header("X-Mashery-Handshake", this.XMasheryHeader).header("puid", this.puid).characterEncoding("utf-8")
				.param("limit", "-10")
				);
		});
		
	
	}	
	
	private String getUserPreference() {
		return "{\"role\":[{\"name\":\"Customer Success Manager\",\"selected\":true,\"timeMap\":null},"+
				"{\"name\":\"Customer Success Practice Lead\",\"selected\":true,\"timeMap\":null},{\"name\":\"Customer Success Specialist\",\"selected\":false,\"timeMap\":null},{\"name\":\"Partner Executive\",\"selected\":false,\"timeMap\":null},{\"name\":\"Renewals Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Success Programs Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Systems Engineer\",\"selected\":false,\"timeMap\":null}],\"language\":[{\"name\":\"English\",\"selected\":true,\"timeMap\":null},{\"name\":\"Japanese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Korean\",\"selected\":false,\"timeMap\":null},{\"name\":\"Portuguese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Spanish\",\"selected\":false,\"timeMap\":null}],\"technology\":[{\"name\":\"Analytics\",\"selected\":true,\"timeMap\":null},{\"name\":\"Cloud\",\"selected\":false,\"timeMap\":null},{\"name\":\"Collaboration\",\"selected\":false,\"timeMap\":null},{\"name\":\"Data Center\",\"selected\":false,\"timeMap\":null},{\"name\":\"Enterprise Networks\",\"selected\":false,\"timeMap\":null},{\"name\":\"IoT\",\"selected\":false,\"timeMap\":null},{\"name\":\"Mobility\",\"selected\":false,\"timeMap\":null},{\"name\":\"Security\",\"selected\":false,\"timeMap\":null}],\"region\":[{\"name\":\"AMER\",\"selected\":false,\"timeMap\":null},{\"name\":\"APJC\",\"selected\":false,\"timeMap\":null},{\"name\":\"EMEAR\",\"selected\":false,\"timeMap\":null}],\"timeinterval\":[{\"timeMap\":{\"startTime\":\"12:00 AM\",\"endTime\":\"12:10 AM\",\"timeZone\":\"HST (UTC-10)\"}}]}'";
	}

	private String getUserPreferenceErr1() {
		return "{\"role\":[{\"name\":\"Customer Success Manager\",\"selected\":true,\"timeMap\":null},"+
				"{\"name\":\"Customer Success Practice Lead\",\"selected\":true,\"timeMap\":null},{\"name\":\"Customer Success Specialist\",\"selected\":false,\"timeMap\":null},{\"name\":\"Partner Executive\",\"selected\":false,\"timeMap\":null},{\"name\":\"Renewals Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Success Programs Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Systems Engineer\",\"selected\":false,\"timeMap\":null}],\"language\":[{\"name\":\"English\",\"selected\":true,\"timeMap\":null},{\"name\":\"Japanese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Korean\",\"selected\":false,\"timeMap\":null},{\"name\":\"Portuguese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Spanish\",\"selected\":false,\"timeMap\":null}],\"technology\":[{\"name\":\"Analytics\",\"selected\":true,\"timeMap\":null},{\"name\":\"Cloud\",\"selected\":false,\"timeMap\":null},{\"name\":\"Collaboration\",\"selected\":false,\"timeMap\":null},{\"name\":\"Data Center\",\"selected\":false,\"timeMap\":null},{\"name\":\"Enterprise Networks\",\"selected\":false,\"timeMap\":null},{\"name\":\"IoT\",\"selected\":false,\"timeMap\":null},{\"name\":\"Mobility\",\"selected\":false,\"timeMap\":null},{\"name\":\"Security\",\"selected\":false,\"timeMap\":null}],\"region\":[{\"name\":\"AMER\",\"selected\":false,\"timeMap\":null},{\"name\":\"APJC\",\"selected\":false,\"timeMap\":null},{\"name\":\"EMEAR\",\"selected\":false,\"timeMap\":null}],\"timeinterval\":[{\"timeMap\":{\"startTime\":\"12:00 AM\",\"endTime\":\"12:00 AM\",\"timeZone\":\"HST (UTC-10)\"}}]}'";
	}
	
	private String getUserPreferenceErr2() {
		return "{\"role\":[{\"name\":\"Customer Success Manager\",\"selected\":true,\"timeMap\":null},"+
				"{\"name\":\"Customer Success Practice Lead\",\"selected\":true,\"timeMap\":null},{\"name\":\"Customer Success Specialist\",\"selected\":false,\"timeMap\":null},{\"name\":\"Partner Executive\",\"selected\":false,\"timeMap\":null},{\"name\":\"Renewals Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Success Programs Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Systems Engineer\",\"selected\":false,\"timeMap\":null}],\"language\":[{\"name\":\"English\",\"selected\":true,\"timeMap\":null},{\"name\":\"Japanese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Korean\",\"selected\":false,\"timeMap\":null},{\"name\":\"Portuguese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Spanish\",\"selected\":false,\"timeMap\":null}],\"technology\":[{\"name\":\"Analytics\",\"selected\":true,\"timeMap\":null},{\"name\":\"Cloud\",\"selected\":false,\"timeMap\":null},{\"name\":\"Collaboration\",\"selected\":false,\"timeMap\":null},{\"name\":\"Data Center\",\"selected\":false,\"timeMap\":null},{\"name\":\"Enterprise Networks\",\"selected\":false,\"timeMap\":null},{\"name\":\"IoT\",\"selected\":false,\"timeMap\":null},{\"name\":\"Mobility\",\"selected\":false,\"timeMap\":null},{\"name\":\"Security\",\"selected\":false,\"timeMap\":null}],\"region\":[{\"name\":\"AMER\",\"selected\":false,\"timeMap\":null},{\"name\":\"APJC\",\"selected\":false,\"timeMap\":null},{\"name\":\"EMEAR\",\"selected\":false,\"timeMap\":null}],\"timeinterval\":[{\"timeMap\":{\"startTime\":\"12:00 AM\",\"endTime\":\"12:10 AM\",\"timeZone\":\"HST\"}}]}'";
	}
	
	private String getUserPreferenceErr3() {
		return "{\"role\":[{\"name\":\"Customer Success Manager\",\"selected\":true,\"timeMap\":null},"+
				"{\"name\":\"Customer Success Practice Lead\",\"selected\":true,\"timeMap\":null},{\"name\":\"Customer Success Specialist\",\"selected\":false,\"timeMap\":null},{\"name\":\"Partner Executive\",\"selected\":false,\"timeMap\":null},{\"name\":\"Renewals Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Success Programs Manager\",\"selected\":false,\"timeMap\":null},{\"name\":\"Systems Engineer\",\"selected\":false,\"timeMap\":null}],\"language\":[{\"name\":\"English\",\"selected\":true,\"timeMap\":null},{\"name\":\"Japanese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Korean\",\"selected\":false,\"timeMap\":null},{\"name\":\"Portuguese\",\"selected\":false,\"timeMap\":null},{\"name\":\"Spanish\",\"selected\":false,\"timeMap\":null}],\"technology\":[{\"name\":\"Analytics\",\"selected\":true,\"timeMap\":null},{\"name\":\"Cloud\",\"selected\":false,\"timeMap\":null},{\"name\":\"Collaboration\",\"selected\":false,\"timeMap\":null},{\"name\":\"Data Center\",\"selected\":false,\"timeMap\":null},{\"name\":\"Enterprise Networks\",\"selected\":false,\"timeMap\":null},{\"name\":\"IoT\",\"selected\":false,\"timeMap\":null},{\"name\":\"Mobility\",\"selected\":false,\"timeMap\":null},{\"name\":\"Security\",\"selected\":false,\"timeMap\":null}],\"region\":[{\"name\":\"AMER\",\"selected\":false,\"timeMap\":null},{\"name\":\"APJC\",\"selected\":false,\"timeMap\":null},{\"name\":\"EMEAR\",\"selected\":false,\"timeMap\":null}],\"timeinterval\":[{\"timeMap\":{\"startTime\":\"ohhh\",\"endTime\":\"12:10 AM\",\"timeZone\":\"HST (UTC-10)\"}}]}'";
	}
	
}
