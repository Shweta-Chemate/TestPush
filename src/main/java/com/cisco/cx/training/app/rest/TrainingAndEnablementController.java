package com.cisco.cx.training.app.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.HealthCheckException;
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
	private TrainingAndEnablementService trainingAndEnablementService;


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
