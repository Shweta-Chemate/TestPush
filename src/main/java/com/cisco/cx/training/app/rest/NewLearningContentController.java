package com.cisco.cx.training.app.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.MasheryObject;
import com.cisco.cx.training.models.PIW;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SuppressWarnings({"squid:S00112","java:S3740"})
@RestController
@Validated
@RequestMapping("/v1/partner/learning")
@Api(value = "New Learning Content APIs")
public class NewLearningContentController {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	private static final String MASHERY_MISSING_MSG = "X-Mashery-Handshake header missing in request";
	private static final String API_NOT_FOUND_MSG = "API Not Found.";

	@Autowired
	private LearningContentService learningContentService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalks")
	@ApiOperation(value = "Fetch SuccessTalks For User", response = SuccessTalkResponseSchema.class, nickname = "fetchUserSuccessTalks")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<SuccessTalkResponseSchema> getUserSuccessTalks(
			@RequestParam(value = "sortField", required = false) String sortField,
			@RequestParam(value = "sortType", required = false) String sortType,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "search", required = false) String search,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
					throws Exception {

		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		// Providing default sorting
		if (sortField == null) {
			sortField = "title";
		}
		// Providing default sort-type
		if (sortType == null) {
			sortType = "asc";
		}
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		SuccessTalkResponseSchema successTalkResponseSchema = learningContentService.fetchSuccesstalks(ccoId, sortField, sortType, filter, search);
		return new ResponseEntity<>(successTalkResponseSchema, HttpStatus.OK);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/piws")
	@ApiOperation(value = "Fetch PIWs", response = String.class, nickname = "listByRegion")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during lookup", response = ErrorResponse.class) })
	public List<PIW> getAllPIWs(@RequestParam(value = "region", required = false) String region,
			@RequestParam(value = "sortField", required = false) String sortField,
			@RequestParam(value = "sortType", required = false) String sortType,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "search", required = false) String search,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid){
		LOG.info("PIWs API called");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		// Providing default sorting
		if (sortField == null) {
			sortField = "title";
		}
		// Providing default sort-type
		if (sortType == null) {
			sortType = "asc";
		}
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<PIW> piwItems = learningContentService.fetchPIWs(ccoId, region, sortField, sortType, filter, search);
		LOG.info("Received PIWs content in {} ", (System.currentTimeMillis() - requestStartTime));
		return piwItems;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/indexCounts")
	@ApiOperation(value = "Fetch all index counts", response = SuccessTalkResponseSchema.class, nickname = "fetchIndexCounts")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during retrieve", response = ErrorResponse.class) })
	public ResponseEntity<CountResponseSchema> getIndexCounts(@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake" , required=false) String xMasheryHandshake)
			throws Exception {
		
		if (StringUtils.isBlank(xMasheryHandshake)) {
            throw new BadRequestException(MASHERY_MISSING_MSG);
        }
		CountResponseSchema countResponseSchema = learningContentService.getIndexCounts();
		return new ResponseEntity<>(countResponseSchema, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/new")
	@ApiOperation(value = "Fetch New Learning Content", nickname = "fetchlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getNewLearningContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
			throws Exception {
		LOG.info("Entering the fetchlearningcontent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String ccoId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> newLearningContentList = learningContentService.fetchNewLearningContent(ccoId, filtersSelected);
		LOG.info("Received new learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(newLearningContentList, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/new/filters")
	@ApiOperation(value = "Fetch All Learnings Filters", response = String.class, nickname = "fetchallViewMoreFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getNewLearningsFilters(
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid) {
		LOG.info("Entering the getNewLearningsFilters method");
		long requestStartTime = System.currentTimeMillis();
		Map<String, Object> learningFilters = learningContentService.getViewMoreNewFiltersWithCount(filtersSelected);
		LOG.info("Received new learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/user/status")
	@ApiOperation(value = "Update registration or view status for users", nickname = "updateUserStatus", response = LearningStatusEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully updated"),
			@ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
			@ApiResponse(code = 403, message = "Operation forbidden due to business policies", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Error during updation", response = ErrorResponse.class) })
	public ResponseEntity updateStatus(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "JSON Body to update user status", required = true) @Valid @RequestBody LearningStatusSchema learningStatusSchema)
			throws Exception {
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		LearningStatusEntity learningStatusEntity=learningContentService.updateUserStatus(userId, puid, learningStatusSchema, xMasheryHandshake);
		if(null != learningStatusEntity){
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/recentlyviewed")
	@ApiOperation(value = "Fetch recently viewed Learning Content", response = String.class, nickname = "fetchrecentlyviewedlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getRecentlyViewedContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
					throws Exception {
		LOG.info("Entering the getRecentlyViewedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchRecentlyViewedContent(userId, filtersSelected);
		LOG.info("Received recently viewed learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/recentlyviewed/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallrecentlyViewedFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getFiltersForRecentlyViewed(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected )
			throws Exception {
		LOG.info("Entering the getFiltersForRecentlyViewed method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		Map<String, Object> learningFilters = learningContentService.getRecentlyViewedFiltersWithCount(userId, filtersSelected);
		LOG.info("Received recently viewed filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}
	
	@PostMapping( produces = MediaType.APPLICATION_JSON_VALUE, path = "/bookmarked")
	@ApiOperation(value = "Fetch bookmarked Learning Content", response = String.class, nickname = "fetchbookmarkedlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getBookmarkedContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
					throws Exception {
		LOG.info("Entering the getBookmarkedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchBookMarkedContent(userId, filtersSelected);
		LOG.info("Received bookmarked learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/bookmarked/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallBookmarkedFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getFiltersForBookmarked(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
			throws Exception {
		LOG.info("Entering the getFiltersForBookmarked method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		Map<String, Object> learningFilters = learningContentService.getBookmarkedFiltersWithCount(userId, filtersSelected);
		LOG.info("Received bookmarked filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/upcoming")
	@ApiOperation(value = "Fetch bookmarked Learning Content", response = String.class, nickname = "fetchupcominglearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getUpcomingContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
					throws Exception {
		LOG.info("Entering the getBookmarkedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchUpcomingContent(userId, filtersSelected);
		LOG.info("Received bookmarked learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/upcoming/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallUpcomingFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getFiltersForUpcoming(
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected ,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getFiltersForUpcoming method");
		long requestStartTime = System.currentTimeMillis();
		Map<String, Object> learningFilters = learningContentService.getUpcomingFiltersWithCount(filtersSelected);
		LOG.info("Received upcoming filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/cxinsights")
	@ApiOperation(value = "Fetch CX Insights Content", response = String.class, nickname = "fetchcxinsightscontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getCXInsightsContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "search") @RequestParam(value = "search", required = false) String searchToken,
            @ApiParam(value = "sortfield") @RequestParam(value = "sortfield", required = false) String sortField,
            @ApiParam(value = "sorttype") @RequestParam(value = "sorttype", required = false) String sortType,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
					throws Exception {
		LOG.info("Entering the getCXInsightsContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		if(sortField==null) {
			sortField="sortByDate";}
		if(sortType==null) {
			sortType="desc";}
		if(searchToken!=null) {
			searchToken=searchToken.trim();}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchCXInsightsContent(userId, filtersSelected, searchToken, sortField, sortType);
		LOG.info("Received cxinsights content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/cxinsights/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for cx insights section", response = String.class, nickname = "fetchallCXInsightsFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getFiltersForCXInsights(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected ,
			@ApiParam(value = "search") @RequestParam(value = "search", required = false) String searchToken,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getFiltersForCXInsights method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		if(searchToken!=null) {
			searchToken=searchToken.trim();}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		Map<String, Object> learningFilters = learningContentService.getCXInsightsFiltersWithCount(userId, searchToken, filtersSelected);
		LOG.info("Received cx insights filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/learningmap")
	@ApiOperation(value = "Fetch learning map", response = String.class, nickname = "fetchlearningmap")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<LearningMap> getLearningMap(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Learning Map ID", required = false) @RequestParam(value = "id", required = false) String id,
			@ApiParam(value = "Learning Map Title", required = false) @RequestParam(value = "title", required = false) String title)
					throws Exception {
		LOG.info("Entering the fetchlearningmap method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		LearningMap learningMap = learningContentService.getLearningMap(id, title);
		LOG.info("Retrieved Learning Map in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningMap, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/popular/{popularityType}")
	@ApiOperation(value = "Fetch popular content across or within a partner company", response = String.class, nickname = "fetchPopularContent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getPopularContent(
			@ApiParam(value = "popularity type. It can be 'popularAcrossPartners' or 'popularAtPartner'") @PathVariable(value = "popularityType", required = true) String popularityType,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
					throws Exception {
		if(!(popularityType.equals(Constants.POPULAR_ACROSS_PARTNERS_PATH) || popularityType.equals(Constants.POPULAR_AT_PARTNER_PATH))) 
		{
			throw new NotFoundException(API_NOT_FOUND_MSG);
		}
		LOG.info("Entering the getPopularContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchPopularContent(userId, filtersSelected, popularityType, puid);
		LOG.info("Received popular content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/popular/{popularityType}/filters")
	@ApiOperation(value = "Fetch Learning Filters for popularAcrossPartners or popularAtPartners section", response = String.class, nickname = "fetchPopularContentFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getPopularContentFilters(
			@ApiParam(value = "popularity type. It can be 'popularAcrossPartners' or 'popularAtPartner'") @PathVariable(value = "popularityType", required = true) String popularityType,
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected)
			throws Exception {
		if(!(popularityType.equals(Constants.POPULAR_ACROSS_PARTNERS_PATH) || popularityType.equals(Constants.POPULAR_AT_PARTNER_PATH)))
		{
			throw new NotFoundException(API_NOT_FOUND_MSG);
		}
		LOG.info("Entering the getPopularContentFilters method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		Map<String, Object> learningFilters = learningContentService.getPopularContentFiltersWithCount(filtersSelected, puid, popularityType, userId);
		LOG.info("Received popular content filters counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/featured")
	@ApiOperation(value = "Fetch bookmarked Learning Content", response = String.class, nickname = "fetchfeaturedlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getFeaturedContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters") @RequestBody(required = false) HashMap<String, Object> filtersSelected)
					throws Exception {
		LOG.info("Entering the getFeaturedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException(MASHERY_MISSING_MSG);
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		List<LearningContentItem> learningContentList = learningContentService.fetchFeaturedContent(userId, filtersSelected);
		LOG.info("Received featured learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningContentList, HttpStatus.OK);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/featured/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallFeaturedFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Object>> getFiltersForFeatured(
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, Object> filtersSelected ,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getFiltersForFeatured method");
		long requestStartTime = System.currentTimeMillis();
		Map<String, Object> learningFilters = learningContentService.getFeaturedFiltersWithCount(filtersSelected);
		LOG.info("Received featured filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<>(learningFilters, HttpStatus.OK);
	}

}
