package com.cisco.cx.training.app.rest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.ErrorResponse;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.PIW;
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
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake)
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
		
		SuccessTalkResponseSchema successTalkResponseSchema = learningContentService.fetchSuccesstalks(sortField, sortType, filter, search);
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
			@ApiParam(value = "Mashery user credential header") @RequestHeader(value = "X-Mashery-Handshake", required = false) String xMasheryHandshake){
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
		List<NewLearningContentEntity> piw_items = learningContentService.fetchPIWs(region, sortField, sortType, filter, search);
		LOG.info("Received PIWs content in {} ", (System.currentTimeMillis() - requestStartTime));
		return piw_items.stream().map(piw_item -> new PIW(piw_item)).collect(Collectors.toList());
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
		CountResponseSchema countResponseSchema = learningContentService.getIndexCounts();
		return new ResponseEntity<CountResponseSchema>(countResponseSchema, HttpStatus.OK);
	}

}
