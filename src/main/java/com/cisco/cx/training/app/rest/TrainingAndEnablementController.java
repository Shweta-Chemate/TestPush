
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

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.HealthCheckException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

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

	@SuppressWarnings("unused")
	private final Map<String, Callable<Boolean>> mandatoryDependencies = new HashMap<>();
	@SuppressWarnings("unused")
	private final Map<String, Callable<Boolean>> optionalDependencies = new HashMap<>();

	
	@Autowired
	private TrainingAndEnablementService trainingAndEnablementService;
	
	@Autowired
	private PropertyConfiguration config;

	@RequestMapping(path = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Template API Readiness probe", hidden = true)
	public Map<String, String> checkReady() throws HealthCheckException {
		return new HashMap<>();
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
		LOG.info("Entering the getAllLearnings");
		long requestStartTime = System.currentTimeMillis();		
		List<SuccessAcademyLearning> sucessAcademyList = trainingAndEnablementService.getAllSuccessAcademyLearnings(xMasheryHandshake);
		LOG.info("Received learnings in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<SuccessAcademyLearning>>(sucessAcademyList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learnings/new")
	@ApiOperation(value = "Fetch New Learning Content", response = String.class, nickname = "fetchlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getNewLearningContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter)
			throws Exception {
		LOG.info("Entering the fetchlearningcontent method");
		long requestStartTime = System.currentTimeMillis();
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<LearningContentItem> newLearningContentList = trainingAndEnablementService.fetchNewLearningContent(ccoId, filter, puid);
		LOG.info("Received new learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(newLearningContentList, HttpStatus.OK);
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
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
            @ApiParam(value = "Event Name of selected session", required = true) @RequestParam(value = "title", required = true) @NotBlank @Size(max =1000) String title,
            @ApiParam(value = "Event Date of selected session", required = true) @RequestParam(value = "eventStartDate") @NotNull Long eventStartDate) throws Exception {

        if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
    	return trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, xMasheryHandshake,puid);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalk/registration")
    @ApiOperation(value = "Create New Success Talk Registration", nickname = "registerUserToSuccessTalk", response = SuccesstalkUserRegEsSchema.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Error during registration", response = ErrorResponse.class)})
    public SuccesstalkUserRegEsSchema registerToAtx(
    		@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
    		@ApiParam(value = "Event Name of selected session", required = true) @RequestParam(value = "title") @NotBlank @Size(max =1000) String title,
            @ApiParam(value = "Event Date of selected session", required = true) @RequestParam(value = "eventStartDate") @NotNull Long eventStartDate) throws Exception {

        if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException("X-Mashery-Handshake header missing in request");
        }
        return trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, xMasheryHandshake,puid);
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
		if(null != bookmarkRequestSchema && StringUtils.isNotBlank(bookmarkRequestSchema.getLearningid())){
			BookmarkResponseSchema learningBookmarkResponse = trainingAndEnablementService.bookmarkLearningForUser(bookmarkRequestSchema,xMasheryHandshake);
			if(null != learningBookmarkResponse){
				return new ResponseEntity<>(HttpStatus.OK);
			}else{
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }	
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/getAllLearningInfo")
	@ApiOperation(value = "Fetch All Learnings Information", response = String.class, nickname = "fetchalllearningsInfo")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<LearningRecordsAndFiltersModel> getAllLearningsInfoPost(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = true) String xMasheryHandshake,
			@ApiParam(value = "Search - tiltle, description, author") @RequestParam(value = "searchToken", required = false) String search,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filters,
			@ApiParam(value = "sortBy - date, title ") @RequestParam(value = "sortBy", required = false) String sortBy,
			@ApiParam(value = "sortOrder - asc, desc") @RequestParam(value = "sortOrder", required = false) String sortOrder
			)
			throws Exception {
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		LearningRecordsAndFiltersModel learningCardsAndFilters = trainingAndEnablementService.getAllLearningInfoPost(xMasheryHandshake,search,filters,sortBy,sortOrder);
		return new ResponseEntity<LearningRecordsAndFiltersModel>(learningCardsAndFilters, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/getAllLearningFilters")
	@ApiOperation(value = "Fetch All Learnings Filters", response = String.class, nickname = "fetchalllearningsFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getAllLearningsFiltersPost(
			@ApiParam(value = "Search - tiltle, description, author") @RequestParam(value = "searchToken", required = false) String searchToken,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filters
			)
			throws Exception {
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		HashMap<String, Object> learningFilters = trainingAndEnablementService.getAllLearningFiltersPost(searchToken,filters);
		return new ResponseEntity<HashMap<String, Object>>(learningFilters, HttpStatus.OK);
	}
	
}


