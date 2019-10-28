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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.HealthCheckException;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.util.ValidationUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v1/partner/training")
@Api(value = "Trainining and Enablement APIs", description = "REST APIs for Training And Enablement")
public class TrainingAndEnablementController {
	@SuppressWarnings("unused")
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

		ValidationUtil.checkHealth(healthStatus, optionalDependencies);

		if (isMandatoryDepDown) {
			throw new HealthCheckException(healthStatus);
		}

		return healthStatus;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/usecases")
	@ApiOperation(value = "Fetch usecases and solutions", response = SuccessTrackAndUseCases.class)
	public SuccessTrackAndUseCases getPitstop() {
		return trainingAndEnablementService.getUsecases();
	}

	@RequestMapping("/live")
	@ApiOperation(value = "Training And Enablement API Liveness Probe", hidden = true)
	public String checkAlive() {
		return "Yes I am alive.";
	}

	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/community")
	@ApiOperation(value = "Create New Community", response = Community.class, nickname = "createCommunity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully inserted community"),
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
	@ApiOperation(value = "Fetch Communities", response = Community.class, responseContainer = "List", nickname = "fetchCommunities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<List<Community>> getAllCommunities(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		List<Community> communityList = trainingAndEnablementService.getAllCommunities();
		return new ResponseEntity<List<Community>>(communityList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/communities/{solution}/{usecase}")
	@ApiOperation(value = "Fetch Communities For Solution and Usecase Filter", response = Community.class, responseContainer = "List", nickname = "fetchFilteredCommunities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<List<Community>> getAllCommunities(@PathVariable(value = "solution", required = false) String solution,
			@PathVariable(value = "usecase", required = false) String usecase, 
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		List<Community> communityList = trainingAndEnablementService.getFilteredCommunities(solution, usecase);
		return new ResponseEntity<List<Community>>(communityList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalk")
	@ApiOperation(value = "Create New SuccessTalk", response = SuccessTalk.class, nickname = "creatSuccessTalk")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully inserted success talk"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Error during create", response = ErrorResponse.class) })
	public SuccessTalk createSuccessTalk(
			@ApiParam(value = "Body for the Request", required = true) @RequestBody SuccessTalk successTalk,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {

		return trainingAndEnablementService.insertSuccessTalk(successTalk);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalks")
	@ApiOperation(value = "Fetch SuccessTalks", response = SuccessTalkResponseSchema.class, nickname = "fetchSuccessTalks")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<SuccessTalkResponseSchema> getAllSuccessTalks(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		SuccessTalkResponseSchema successTalkResponseSchema = trainingAndEnablementService.getAllSuccessTalks();
		return new ResponseEntity<SuccessTalkResponseSchema>(successTalkResponseSchema, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalks/{solution}/{usecase}")
	@ApiOperation(value = "Fetch SuccessTalks For Solution and Usecase Filters", response = SuccessTalkResponseSchema.class, nickname = "fetchFilteredSuccessTalks")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<SuccessTalkResponseSchema> getFilteredSuccessTalks(@PathVariable(value = "solution", required = false) String solution,
			@PathVariable(value = "usecase", required = false) String usecase, 
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		SuccessTalkResponseSchema successTalkResponseSchema = trainingAndEnablementService.getFilteredSuccessTalks(solution, usecase);
		return new ResponseEntity<SuccessTalkResponseSchema>(successTalkResponseSchema, HttpStatus.OK);
	}
	

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "successTalk/register")
    @ApiOperation(value = "Create New SuccessTalk Registration", nickname = "registerUserToSuccessTalk", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Error during registration", response = ErrorResponse.class)})
    public ResponseEntity<String> registerToSuccessTalk(@ApiParam(value = "successTalkId", required = true) String successTalkId,
    		@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required=false) String xMasheryHandshake,
            @ApiParam(value = "The SessionId of this SuccessTalk", required = true) String sessionId) throws Exception {
         String successTalkResultId = trainingAndEnablementService.registerUserToSuccessTalkSession(sessionId, successTalkId);
         return new ResponseEntity<String>(successTalkResultId, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "successTalk/cancel")
    @ApiOperation(value = "Cancel SuccessTalk Registration", nickname = "cancelUserToSuccessTalk", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully cancelled"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Error during cancellation", response = ErrorResponse.class)})
    public ResponseEntity<String> cancelToSuccessTalk(@ApiParam(value = "successTalkId", required = true) String successTalkId,
    		@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required=false) String xMasheryHandshake,
            @ApiParam(value = "The SessionId of this SuccessTalk", required = true) String sessionId) throws Exception {
    	 String successTalkResultId =  trainingAndEnablementService.cancelUserToSuccessTalkSession(sessionId, successTalkId);
         return new ResponseEntity<String>(successTalkResultId, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learning")
	@ApiOperation(value = "Create New Learning", response = String.class, nickname = "createLearning")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Error during create", response = ErrorResponse.class) })
	public Learning createLearning(
			@ApiParam(value = "Body for the Request", required = true) @RequestBody Learning learning,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
			throws Exception {

		return trainingAndEnablementService.insertLearning(learning);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learnings")
	@ApiOperation(value = "Fetch learnings", response = String.class, nickname = "fetchlearnings")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningModel>> getAllLeanings(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
			throws Exception {
		List<LearningModel> learningList = trainingAndEnablementService.getAllLearning();
		return new ResponseEntity<List<LearningModel>>(learningList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learnings/{solution}/{usecase}")
	@ApiOperation(value = "Fetch Learnings with filter", response = String.class, nickname = "fetchFilteredLearnings")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningModel>> getAllLearnings(@PathVariable(value = "solution", required = false) String solution,
			@PathVariable(value = "usecase", required = false) String usecase,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
			throws Exception {		
		List<LearningModel> learningList = trainingAndEnablementService.getFilteredLearning(solution, usecase);
		return new ResponseEntity<List<LearningModel>>(learningList, HttpStatus.OK);
	}

}
