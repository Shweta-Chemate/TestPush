package com.cisco.cx.training.app.controller.test;

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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cisco.cx.training.app.TrainingAndEnablementApplication;
import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.config.Swagger2Config;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.filters.AuthFilter;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.app.rest.NewLearningContentController;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.swagger2.web.Swagger2Controller;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = { NewLearningContentController.class, Swagger2Controller.class })
@ContextConfiguration(classes = { TrainingAndEnablementApplication.class,
		PropertyConfiguration.class,Swagger2Config.class})

public class NewLearningContentControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	RBACFilter rbacFilter;

	@Autowired
	AuthFilter authFilter;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private CommunityDAO communityDAO;

	@Autowired
	ResourceLoader resourceLoader;

	@MockBean
	private LearningContentService learningContentService;

	private String XMasheryHeader;

	private String puid = "101";

	@BeforeEach
	public void init() throws IOException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(authFilter).build();
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));

	}

	@Test
	public void testGetAllPIWs() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/piws").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.param("filter", "region:APJC")
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/piws").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.param("filter", "region:APJC")
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetUserSuccessTalks() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/successTalks").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.param("filter", "test:test")
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/successTalks").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.param("filter", "test:test")
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testFetchIndexCounts() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/indexCounts").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/indexCounts").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});

	}

	@Test
	public void testGetNewLearningsFilters() throws Exception {
		this.mockMvc
		.perform(post("/v1/partner/learning/viewmore/new/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/viewmore/new/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.param("filter", "test")
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testUpdateStatus() throws Exception {
		LearningStatusSchema schema= new LearningStatusSchema();
		schema.setLearningItemId("test");
		schema.setViewed(true);
		Mockito.when(learningContentService.updateUserStatus(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())
				).thenReturn(new LearningStatusEntity());
		this.mockMvc
		.perform(post("/v1/partner/learning/user/status").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(asJsonString(schema))
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/user/status").contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(asJsonString(schema))
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetRecentlyViewedContent() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/recentlyviewed").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/recentlyviewed").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void getFiltersForRecentlyViewed() throws Exception {
		this.mockMvc
		.perform(post("/v1/partner/learning/viewmore/recentlyviewed/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/viewmore/recentlyviewed/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetBookmarkedContent() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/bookmarked").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/bookmarked").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetFiltersForBookmarked() throws Exception {
		this.mockMvc
		.perform(post("/v1/partner/learning/viewmore/bookmarked/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/viewmore/bookmarked/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetUpcomingContent() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/upcoming").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/upcoming").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetFiltersForUpcoming() throws Exception {
		this.mockMvc
		.perform(post("/v1/partner/learning/viewmore/upcoming/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/viewmore/upcoming/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetSuccessAcademyContent() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/successacademy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/successacademy").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetFiltersForSuccessacademy() throws Exception {
		this.mockMvc
		.perform(post("/v1/partner/learning/successacademy/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(post("/v1/partner/learning/successacademy/filters").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetCXInsightsContent() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/cxinsights").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/cxinsights").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	@Test
	public void testGetLearningMap() throws Exception {
		this.mockMvc
		.perform(get("/v1/partner/learning/learningmap").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader)
				.header("puid", this.puid)
				.param("id", "id")
				.characterEncoding("utf-8"))
		.andDo(print()).andExpect(status().isOk());

		assertThrows(Exception.class, () -> {
			this.mockMvc
			.perform(get("/v1/partner/learning/learningmap").contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("puid", this.puid)
					.param("id", "id")
					.characterEncoding("utf-8"))
			.andDo(print()).andExpect(status().isOk());
		});
	}

	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
