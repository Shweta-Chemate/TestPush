package com.cisco.cx.training.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.cisco.cx.training.app.TrainingAndEnablementApplication;
import com.cisco.cx.training.app.config.ElasticSearchConfig;
import com.cisco.cx.training.app.config.FilterConfig;
import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.config.Swagger2Config;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.rest.TrainingAndEnablementController;
import com.cisco.cx.training.app.service.CiscoProfileService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalk.SuccessTalkStatusEnum;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import springfox.documentation.swagger2.web.Swagger2Controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = { TrainingAndEnablementController.class, Swagger2Controller.class })
@ContextConfiguration(classes = { TrainingAndEnablementApplication.class, CiscoProfileService.class,
		PropertyConfiguration.class, ElasticSearchConfig.class, Swagger2Config.class, FilterConfig.class })

public class TrainingAndEnablementControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommunityDAO communityDAO;

	@Autowired
	ResourceLoader resourceLoader;

	@MockBean
	private TrainingAndEnablementService trainingAndEnablementService;

	@InjectMocks
	private TrainingAndEnablementServiceImpl trainingAndEnablementServiceImpl;

	private String XMasheryHeader;

	@Before
	public void init() throws IOException {
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));

	}

	@Test
	public void testCreateCommunity() throws Exception {

		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(community);

		this.mockMvc.perform(post("/v1/partner/training/community").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader).content(requestJson).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testFetchCommunities() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/communities").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testLearning() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/learnings").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void testGetPitstops() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/usecases").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void testCheckReady() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/ready").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testLive() throws Exception {

		this.mockMvc
				.perform(get("/v1/partner/training/live").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void testCreateSuccessTalks() throws Exception {
		SuccessTalk successTalk = new SuccessTalk();
		successTalk.setBookmark(true);
		successTalk.setDescription("description");
		successTalk.setDocId("someId");
		successTalk.setDuration(100L);
		successTalk.setImageUrl("");
		successTalk.setRecordingUrl("");
		List<SuccessTalkSession> sessions = new ArrayList<SuccessTalkSession>();
		successTalk.setSessions(sessions);
		successTalk.setStatus(SuccessTalkStatusEnum.COMPLETED);
		successTalk.setSuccessTalkId("");
		successTalk.setTitle("title");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(successTalk);

		this.mockMvc.perform(post("/v1/partner/training/successTalk").contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("X-Mashery-Handshake", this.XMasheryHeader).content(requestJson).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testFetchCommunitiesWithFilter() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/communities/IBN/solution")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void testFetchSuccessTalks() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/successTalks").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void testFetchFilteredSuccessTalks() throws Exception {
		this.mockMvc
				.perform(get("/v1/partner/training/successTalks/IBN/solution").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("X-Mashery-Handshake", this.XMasheryHeader).characterEncoding("utf-8"))
				.andDo(print()).andExpect(status().isOk());
	}

	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}
}
