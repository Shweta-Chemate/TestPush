package com.cisco.cx.training.app.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.HealthCheckException;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessAcademyModel;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.util.ValidationUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Validated
@RequestMapping("/v1/partner/training")
@Api(value = "Trainining and Enablement APIs", description = "REST APIs for Training And Enablement")
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
	
	@RequestMapping("/live")
	@ApiOperation(value = "Training And Enablement API Liveness Probe", hidden = true)
	public String checkAlive() {
		return "Yes I am alive.";
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
    
   
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learnings")
	@ApiOperation(value = "Fetch SuccessAcademy", response = String.class, nickname = "fetchsuccessacademy")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<SuccessAcademyLearning>> getAllLearnings(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
			throws Exception {
		List<SuccessAcademyLearning> sucessAcademyList = trainingAndEnablementService.getAllSuccessAcademyLearnings();
		return new ResponseEntity<List<SuccessAcademyLearning>>(sucessAcademyList, HttpStatus.OK);
	}

	

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalks")
	@ApiOperation(value = "Fetch SuccessTalks For User", response = SuccessTalkResponseSchema.class, nickname = "fetchUserSuccessTalks")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<SuccessTalkResponseSchema> getUserSuccessTalks(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		
		if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
		SuccessTalkResponseSchema successTalkResponseSchema = trainingAndEnablementService.getUserSuccessTalks(xMasheryHandshake);
		return new ResponseEntity<SuccessTalkResponseSchema>(successTalkResponseSchema, HttpStatus.OK);
	}
	
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalk/registration")
    @ApiOperation(value = "Request a cancellation for a scheduled Success Talk session", nickname = "cancelUserToSucessTalk")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully cancelled"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorResponse.class)})
    public SuccesstalkUserRegEsSchema cancelUserAtxRegistration(
            @ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "Event Name of selected session", required = true) @RequestParam(value = "title", required = true) @NotBlank @Size(max =1000) String title,
            @ApiParam(value = "Event Date of selected session", required = true) @RequestParam(value = "eventStartDate") @NotNull Long eventStartDate) throws Exception {

        if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
    	return trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, xMasheryHandshake);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalk/registration")
    @ApiOperation(value = "Create New Success Talk Registration", nickname = "registerUserToSuccessTalk", response = SuccesstalkUserRegEsSchema.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Error during registration", response = ErrorResponse.class)})
    public SuccesstalkUserRegEsSchema registerToAtx(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
    		@ApiParam(value = "Event Name of selected session", required = true) @RequestParam(value = "title") @NotBlank @Size(max =1000) String title,
            @ApiParam(value = "Event Date of selected session", required = true) @RequestParam(value = "eventStartDate") @NotNull Long eventStartDate) throws Exception {

        if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
        return trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, xMasheryHandshake);
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalk/bookmarks")
    @ApiOperation(value = "Create or update bookmark for one of the lifecycle categories", response = BookmarkResponseSchema.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated", response = BookmarkResponseSchema.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorResponse.class)})
    public BookmarkResponseSchema createOrUpdate(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
    											 
                                                 @ApiParam(value = "JSON Body to Bookmark", required = true) @RequestBody BookmarkRequestSchema bookmarkRequestSchema) {

        LOG.info("API_BOOKMARKS Call start");
        long startTime = System.currentTimeMillis();

        if (!bookmarkRequestSchema.isNotBlank()) {
            throw new BadRequestException("Bad Request");
        }
        
        if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
        BookmarkResponseSchema bookmarkResponseSchema = trainingAndEnablementService.createOrUpdateBookmark(bookmarkRequestSchema, xMasheryHandshake);

        long endTime = System.currentTimeMillis() - startTime;
        LOG.info("PERF_TIME_TAKEN | API_BOOKMARKS | " + endTime);
        LOG.info("API_BOOKMARKS Call end");

        return bookmarkResponseSchema;
    }
    
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/indexCounts")
	@ApiOperation(value = "Fetch all index counts", response = SuccessTalkResponseSchema.class, nickname = "fetchIndexCounts")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<CountResponseSchema> getIndexCounts(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		
		if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
		CountResponseSchema countResponseSchema = trainingAndEnablementService.getIndexCounts();
		return new ResponseEntity<CountResponseSchema>(countResponseSchema, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/getLearningFilters")
	@ApiOperation(value = "Fetch SuccessAcademy Filters", response = String.class, nickname = "fetchsuccessacademyfilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<SuccessAcademyFilter>> getAllLearningFilters(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
			throws Exception {
		List<SuccessAcademyFilter> sucessAcademyFilterList = trainingAndEnablementService.getSuccessAcademyFilters();
		return new ResponseEntity<List<SuccessAcademyFilter>>(sucessAcademyFilterList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learning/bookmark")
    @ApiOperation(value = "Create or remove bookmark for one of the learnings", response = BookmarkResponseSchema.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated", response = BookmarkResponseSchema.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorResponse.class)})
    public ResponseEntity addOrRemoveLearningBookmarks(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,    											 
                                                 @ApiParam(value = "JSON Body to Bookmark", required = true) @RequestBody BookmarkRequestSchema bookmarkRequestSchema) {
		return new ResponseEntity<>(HttpStatus.OK);
    }

}
