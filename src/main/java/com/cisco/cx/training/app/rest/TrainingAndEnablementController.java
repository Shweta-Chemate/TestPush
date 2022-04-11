
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.UserLearningPreference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SuppressWarnings({"squid:S00112"})
@RestController
@Validated
@RequestMapping("/v1/partner/training")
@Api(value = "Trainining and Enablement APIs", description = "REST APIs for Training And Enablement")
public class TrainingAndEnablementController {
	private static final String LIMIT_MSG = "Invalid limit.";

	@SuppressWarnings("unused")
	private final Map<String, Callable<Boolean>> mandatoryDependencies = new HashMap<>();
	@SuppressWarnings("unused")
	private final Map<String, Callable<Boolean>> optionalDependencies = new HashMap<>();

	
	@Autowired
	private TrainingAndEnablementService trainingAndEnablementService;
	
	@GetMapping(path = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Template API Readiness probe", hidden = true)
	public Map<String, String> checkReady() throws HealthCheckException {
		return new HashMap<>();
	}
	
	@GetMapping(path="/live")
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
 
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learning/bookmark")
    @ApiOperation(value = "Create or remove bookmark for one of the learnings", response = BookmarkResponseSchema.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated", response = BookmarkResponseSchema.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorResponse.class)})
    public ResponseEntity addOrRemoveLearningBookmarks(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
            @ApiParam(value = "JSON Body to Bookmark", required = true) @RequestBody BookmarkRequestSchema bookmarkRequestSchema) {
		if(null != bookmarkRequestSchema && StringUtils.isNotBlank(bookmarkRequestSchema.getLearningid())){
			BookmarkResponseSchema learningBookmarkResponse = trainingAndEnablementService.bookmarkLearningForUser(bookmarkRequestSchema, xMasheryHandshake, puid);
			if(null != learningBookmarkResponse){
				return new ResponseEntity<>(HttpStatus.OK);
			}else{
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }	
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/getAllLearningInfo/{learningTab}")
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
			@ApiParam(value = "sortOrder - asc, desc") @RequestParam(value = "sortOrder", required = false) String sortOrder,
			@ApiParam(value = "learningTab - Technology, Skill") @PathVariable(value = "learningTab", required = true) String learningTab
			)
			throws Exception {
		LearningRecordsAndFiltersModel learningCardsAndFilters = trainingAndEnablementService.
				getAllLearningInfoPost(xMasheryHandshake,search,filters,sortBy,sortOrder,learningTab);
		return new ResponseEntity<LearningRecordsAndFiltersModel>(learningCardsAndFilters, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/getAllLearningFilters/{learningTab}")
	@ApiOperation(value = "Fetch All Learnings Filters", response = String.class, nickname = "fetchalllearningsFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getAllLearningsFiltersPost(
			@ApiParam(value = "Search - tiltle, description, author") @RequestParam(value = "searchToken", required = false) String searchToken,
			@ApiParam(value = "learningTab - Technology, Skill") @PathVariable(value = "learningTab", required = true) String learningTab,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filters
			)
			throws Exception {
		Map<String, Object> learningFilters = trainingAndEnablementService.getAllLearningFiltersPost(searchToken,filters,learningTab);
		return new ResponseEntity<Map<String, Object>>(learningFilters, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/myLearningPreferences")
	@ApiOperation(value = "Fetch All Learnings Preferences", response = String.class, nickname = "fetchMyLearningPreferences")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, List<UserLearningPreference>>> getMyLearningPreferences(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake
			)
			throws Exception {
		Map<String,List<UserLearningPreference>> userPreferences = trainingAndEnablementService.getUserLearningPreferences(xMasheryHandshake);
		return new ResponseEntity<Map<String, List<UserLearningPreference>>>(userPreferences, HttpStatus.OK);		
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/myLearningPreferences")
	@ApiOperation(value = "Set User Learnings Preferences", response = String.class, nickname = "setMyLearningPreferences")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Preferences updated successfully."),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<String> updateMyLearningPreferences(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=true) String xMasheryHandshake,
			@ApiParam(value = "preferences") @RequestBody(required = false) Map<String, List<UserLearningPreference>> userPreferences
			)
			throws Exception {    
		Map<String, List<UserLearningPreference>> userPreferencesDb = trainingAndEnablementService.postUserLearningPreferences(xMasheryHandshake,userPreferences);
		return new ResponseEntity<String>("Preferences updated successfully.", HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/myPreferredLearnings")
	@ApiOperation(value = "Fetch Preferred Learnings Information", response = String.class, nickname = "fetchPreferredLearningsInfo")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<LearningRecordsAndFiltersModel> getTopPicks(
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = true) String xMasheryHandshake,
			@ApiParam(value = "Search - tiltle, description, author") @RequestParam(value = "searchToken", required = false) String search,
			@ApiParam(value = "sortBy - date, title ") @RequestParam(value = "sortBy", required = false) String sortBy,
			@ApiParam(value = "sortOrder - asc, desc") @RequestParam(value = "sortOrder", required = false) String sortOrder,
			@ApiParam(value = "limit - Number of cards") @RequestParam(value = "limit", required = false) Integer limit			
			)
			throws Exception {
		HashMap<String, Object> filters = new HashMap<String, Object>();
		LearningRecordsAndFiltersModel learningCards = trainingAndEnablementService.
				getMyPreferredLearnings(xMasheryHandshake,search,filters,sortBy,sortOrder,puid, limit);
		if(limit!=null && limit < 0) {throw new BadRequestException(LIMIT_MSG);}
		return new ResponseEntity<LearningRecordsAndFiltersModel>(learningCards, HttpStatus.OK);
	}
	
}