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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.LearningContentService;
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

@RestController
@Validated
@RequestMapping("/v1/partner/learning")
@Api(value = "New Learning Content APIs")
public class NewLearningContentController {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private LearningContentService learningContentService;
	
	@Autowired
	private PropertyConfiguration config;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successTalks")
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
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
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
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		SuccessTalkResponseSchema successTalkResponseSchema = learningContentService.fetchSuccesstalks(ccoId, sortField, sortType, filter, search);
		return new ResponseEntity<SuccessTalkResponseSchema>(successTalkResponseSchema, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/piws")
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
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
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
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<PIW> piw_items = learningContentService.fetchPIWs(ccoId, region, sortField, sortType, filter, search);
		LOG.info("Received PIWs content in {} ", (System.currentTimeMillis() - requestStartTime));
		return piw_items;
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
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		CountResponseSchema countResponseSchema = learningContentService.getIndexCounts();
		return new ResponseEntity<CountResponseSchema>(countResponseSchema, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/new/filters")
	@ApiOperation(value = "Fetch All Learnings Filters", response = String.class, nickname = "fetchallViewMoreFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Map<String,String>>> getNewLearningsFilters(
			@ApiParam(value = "Filter - multiple, multiple types e.g filter=contentType:PDF,Video") @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, HashMap<String,String>> filterCounts,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getNewLearningsFilters method");
		long requestStartTime = System.currentTimeMillis();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(filter!=null && filterCounts==null) {
			throw new  BadRequestException("filter counts required in the body");
		}
		Map<String, Map<String,String>> learningFilters = learningContentService.getViewMoreNewFiltersWithCount(filter, filterCounts);
		LOG.info("Received new learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<Map<String, Map<String,String>>>(learningFilters, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/user/status")
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
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		LearningStatusEntity learningStatusEntity=learningContentService.updateUserStatus(userId, puid, learningStatusSchema, xMasheryHandshake);
		if(null != learningStatusEntity){
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/recentlyviewed")
	@ApiOperation(value = "Fetch recently viewed Learning Content", response = String.class, nickname = "fetchrecentlyviewedlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getRecentlyViewedContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter)
					throws Exception {
		LOG.info("Entering the getRecentlyViewedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<LearningContentItem> learningContentList = learningContentService.fetchRecentlyViewedContent(userId, filter);
		LOG.info("Received recently viewed learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(learningContentList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/recentlyviewed/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallrecentlyViewedFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Map<String,String>>> getFiltersForRecentlyViewed(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filter - multiple, multiple types e.g filter=contentType:PDF,Video") @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, HashMap<String,String>> filterCounts)
			throws Exception {
		LOG.info("Entering the getFiltersForRecentlyViewed method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(filter!=null && filterCounts==null) {
			throw new  BadRequestException("filter counts required in the body");
		}
		Map<String, Map<String,String>> learningFilters = learningContentService.getRecentlyViewedFiltersWithCount(userId, filter, filterCounts);
		LOG.info("Received recently viewed filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<Map<String, Map<String,String>>>(learningFilters, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/bookmarked")
	@ApiOperation(value = "Fetch bookmarked Learning Content", response = String.class, nickname = "fetchbookmarkedlearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getBookmarkedContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter)
					throws Exception {
		LOG.info("Entering the getBookmarkedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<LearningContentItem> learningContentList = learningContentService.fetchBookMarkedContent(userId, filter);
		LOG.info("Received bookmarked learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(learningContentList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/bookmarked/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallBookmarkedFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Map<String,String>>> getFiltersForBookmarked(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filter - multiple, multiple types e.g filter=contentType:PDF,Video") @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, HashMap<String,String>> filterCounts)
			throws Exception {
		LOG.info("Entering the getFiltersForBookmarked method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(filter!=null && filterCounts==null) {
			throw new  BadRequestException("filter counts required in the body");
		}
		Map<String, Map<String,String>> learningFilters = learningContentService.getBookmarkedFiltersWithCount(userId, filter, filterCounts);
		LOG.info("Received bookmarked filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<Map<String, Map<String,String>>>(learningFilters, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/upcoming")
	@ApiOperation(value = "Fetch bookmarked Learning Content", response = String.class, nickname = "fetchupcominglearningcontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getUpcomingContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter)
					throws Exception {
		LOG.info("Entering the getBookmarkedContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<LearningContentItem> learningContentList = learningContentService.fetchUpcomingContent(userId, filter);
		LOG.info("Received bookmarked learning content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(learningContentList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/viewmore/upcoming/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for recently viewed section", response = String.class, nickname = "fetchallUpcomingFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Map<String,String>>> getFiltersForUpcoming(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "Filter - multiple, multiple types e.g filter=contentType:PDF,Video") @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, HashMap<String,String>> filterCounts,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getFiltersForUpcoming method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(filter!=null && filterCounts==null) {
			throw new  BadRequestException("filter counts required in the body");
		}
		Map<String, Map<String,String>> learningFilters = learningContentService.getUpcomingFiltersWithCount(filter, filterCounts);
		LOG.info("Received upcoming filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<Map<String, Map<String,String>>>(learningFilters, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successacademy")
	@ApiOperation(value = "Fetch successacademy Content", response = String.class, nickname = "fetchsuccessacademycontent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<List<LearningContentItem>> getSuccessAcademyContent(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter)
					throws Exception {
		LOG.info("Entering the getSuccessAcademyContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		List<LearningContentItem> learningContentList = learningContentService.fetchSuccessAcademyContent(userId, filter);
		LOG.info("Received SuccessAcademy content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(learningContentList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, path = "/successacademy/filters")
	@ApiOperation(value = "Fetch All Learnings Filters for cx collection section", response = String.class, nickname = "fetchallSuccessAcademyFilters")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<Map<String, Map<String,String>>> getFiltersForSuccessacademy(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
			@ApiParam(value = "Filter - multiple, multiple types e.g filter=contentType:PDF,Video") @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "JSON Body to update filters", required = false) @RequestBody(required=false) HashMap<String, HashMap<String,String>> filterCounts,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
			throws Exception {
		LOG.info("Entering the getFiltersForSuccessacademy method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(filter!=null && filterCounts==null) {
			throw new  BadRequestException("filter counts required in the body");
		}
		Map<String, Map<String,String>> learningFilters = learningContentService.getSuccessAcademyFiltersWithCount(filter, filterCounts);
		LOG.info("Received successacademy filter counts in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<Map<String, Map<String,String>>>(learningFilters, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/cxinsights")
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
			@ApiParam(value = "Filters", required = false) @RequestParam(value = "filter", required = false) String filter,
			@ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid)
					throws Exception {
		LOG.info("Entering the getCXInsightsContent method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		if(sortField==null)
			sortField="sortByDate";
		if(sortType==null)
			sortType="desc";
		if(searchToken!=null)
			searchToken=searchToken.trim();
		List<LearningContentItem> learningContentList = learningContentService.fetchCXInsightsContent(userId, filter, searchToken, sortField, sortType);
		LOG.info("Received cxinsights content in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<List<LearningContentItem>>(learningContentList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/learningmap")
	@ApiOperation(value = "Fetch learning map", response = String.class, nickname = "fetchlearningmap")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved results"),
			@ApiResponse(code = 400, message = "Bad Input", response = ErrorResponse.class),
			@ApiResponse(code = 404, message = "Entity Not Found"),
			@ApiResponse(code = 500, message = "Error during delete", response = ErrorResponse.class) })
	public ResponseEntity<LearningMap> getLearningMap(
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake,
            @ApiParam(value = "puid") @RequestHeader(value = "puid", required = true) String puid,
			@ApiParam(value = "Learning Map ID", required = false) @RequestParam(value = "id", required = true) String id)
					throws Exception {
		LOG.info("Entering the fetchlearningmap method");
		long requestStartTime = System.currentTimeMillis();
		if (StringUtils.isBlank(xMasheryHandshake)) {
			throw new BadRequestException("X-Mashery-Handshake header missing in request");
		}
		String userId = MasheryObject.getInstance(xMasheryHandshake).getCcoId();
		if(!config.isNewLearningFeature())
		{
			throw new NotFoundException("API Not Found.");
		}
		LearningMap learningMap = learningContentService.getLearningMap(id);
		LOG.info("Retrieved Learning Map in {} ", (System.currentTimeMillis() - requestStartTime));
		return new ResponseEntity<LearningMap>(learningMap, HttpStatus.OK);
	}

}
