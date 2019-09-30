package com.cisco.cx.training.app.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.HealthCheckException;
import com.cisco.cx.training.app.service.CiscoProfileService;
import com.cisco.cx.training.app.service.EmailService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.LearningModel;
import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.util.ValidationUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v1/partner/training")
@Api(value = "Trainining and Enablement APIs", description = "Sample CRUD operation example")
public class TrainingAndEnablementController {
	private final Logger LOG = LoggerFactory.getLogger(TrainingAndEnablementController.class);

	private final Map<String, Callable<Boolean>> mandatoryDependencies = new HashMap<>();
	private final Map<String, Callable<Boolean>> optionalDependencies = new HashMap<>();

	@Autowired
	private EmailService emailService;

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	@Autowired
	private CiscoProfileService ciscoProfileService;

	@Autowired
	private PropertyConfiguration config;
	
	@Autowired
	private TrainingAndEnablementService trainingAndEnablementService;

	public TrainingAndEnablementController() {
		mandatoryDependencies.put("elasticsearch", () -> elasticSearchDAO.isElasticSearchRunning());
		optionalDependencies.put("email.api", () -> emailService.isEmailServerRunning());
	}

	@RequestMapping(path = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Template API Readiness probe", hidden = true)
	public Map<String, String> checkReady() throws HealthCheckException {
		Map<String, String> healthStatus = new HashMap<>();

		// process the mandatory dependencies first
		ValidationUtil.checkHealth(healthStatus, mandatoryDependencies);

		// next, check if any of the mandatory dependencies are DOWN
		boolean isMandatoryDepDown = healthStatus.values().contains("DOWN");

		//process optional dependencies AFTER checking if mandatory dependencies are DOWN
		ValidationUtil.checkHealth(healthStatus, optionalDependencies);

		if (isMandatoryDepDown) {
			throw new HealthCheckException(healthStatus);
		}

		return healthStatus;
	}
	
	@RequestMapping("/usecases")
	@ApiOperation(value = "gets usecases for solutions", hidden = true)
	public SuccessTrackAndUseCases getPitstop() {
		return trainingAndEnablementService.getUsecases();
	}
	
	@RequestMapping("/learnings")
	@ApiOperation(value = "gets learnings", hidden = true)
	public List<LearningModel> getLearning() {
		return trainingAndEnablementService.getLearning();
	}

	@RequestMapping("/live")
	@ApiOperation(value = "Training And Enablement API Liveness Probe", hidden = true)
	public String checkAlive() {
		return "Yes I am alive.";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/sample/entities")
	@ApiOperation(value = "Create New Entity", response = String.class, nickname = "createEntity")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Error during create", response = ErrorResponse.class)})
	public String createEntity(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake") String xMasheryHandshake,
			@ApiParam(value = "Body for the Request", required = true) @RequestBody String entityRequest) throws Exception {

		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}

		return "{\"createEntity\": \"success\"}";
	}

	@GetMapping( produces = MediaType.APPLICATION_JSON_VALUE, value = "/sample/entities")
	//@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/sample/entities")
	@ApiOperation(value = "Fetch Entity", response = String.class, nickname = "readEntity1")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 500, message = "Error during lookup", response = ErrorResponse.class)})
	public String readEntity1 (@ApiParam(value = "Mashery user credential header")  @RequestHeader(value = "X-Mashery-Handshake") String xMasheryHandshake,
			@ApiParam(value = "Request Param 1") @RequestParam(value = "param1", required = false) String param1) throws Exception {
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}

		return "{\"readEntity1\": \"response\"}";
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/sample/entities/{entityId}")
	@ApiOperation(value = "Fetch Entity By Id", response = String.class, nickname = "readEntity2")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during lookup", response = ErrorResponse.class)})
	public String readEntity2 (@ApiParam(value = "Mashery user credential header")  @RequestHeader(value = "X-Mashery-Handshake") String xMasheryHandshake,
			@ApiParam(value = "Path Param 1", required = true) @PathVariable(value = "entityId") String param1) throws Exception {

		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}

		return "{\"readEntity2\": \"response\"}";
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/sample/entities/{entityId}")
	@ApiOperation(value = "Update Entity", response = String.class, nickname = "updateEntity")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during update", response = ErrorResponse.class)})
	public String updateEntity(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake") String xMasheryHandshake,
			@ApiParam(value = "Path Param 1", required = true) @PathVariable(value = "entityId") String param1,
			@ApiParam(value = "New Body to be updated to the Request", required = true) @RequestBody String gtRequest) throws Exception {

		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}

		return "{\"updateEntity\": \"response\"}";
	}

	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE, path = "/sample/entities/{entityId}")
	@ApiOperation(value = "Delete Entity", response = String.class, nickname = "deleteEntity")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class)})
	public String deleteEntity(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake") String xMasheryHandshake,
			@ApiParam(value = "Entity Id to delete", required = true) @PathVariable(value = "entityId") String param1) throws Exception {

		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}

		return "delete_response";
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/community")
	@ApiOperation(value = "Create New Community", response = String.class, nickname = "createCommunity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Error during create", response = ErrorResponse.class) })
	public Community createCommunity(
			@ApiParam(value = "Body for the Request", required = true) @RequestBody Community community,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {

		return trainingAndEnablementService.insertCommunity(community);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/communities")
	@ApiOperation(value = "Fetch Communities", response = String.class, nickname = "fetchCommunities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<?> getAllCommunities(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		List<Community> communityList = trainingAndEnablementService.getAllCommunities();
		return new ResponseEntity<>(communityList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/communities/{solution}/{usecase}")
	@ApiOperation(value = "Fetch Communities", response = String.class, nickname = "fetchCommunities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<?> getAllCommunities(@PathVariable(value = "solution", required = false) String solution,
			@PathVariable(value = "usecase", required = false) String usecase, 
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		List<Community> communityList = trainingAndEnablementService.getFilteredCommunities(solution, usecase);
		return new ResponseEntity<>(communityList, HttpStatus.OK);
	}
}
