package com.cisco.cx.training.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 *
 * Constants for the project. Have the constants only if they are used in
 * multiple classes otherwise keep them in their own class.
 *
 */
public final class Constants {
	public static final String MASHERY_HANDSHAKE_HEADER_NAME = "X-Mashery-Handshake";
	public static final String PUID="puid";
	public static final String SORTDATE="sortByDate";
	public static final String USER_ID = "userId";
	public static final String ROLES_LIST = "roles";
	public static final String USER_PROFILE = "userProfile";
	public static final String APPLICATION_JSON = "application/json";
	public static final String AUTHORIZATION = "Authorization";
	public static final String COMPANY_ID = "companyId";
	public static final String RESOURCE_ID = "CXPP_CPM";
	public static final String COMMA= ",";
	public static final String SPACE= " ";
	public static final String ROLE_ID= "roleid";
	public static final String CUSTOMERLIST= "customer_list";
	public static final String SQUARE_OPEN_BRACKET= "[";
	public static final String SQUARE_CLOSE_BRACKET= "]";
	public static final String CUID= "cuid";
	public static final String SUCCESSTALK= "successtalk";
	public static final String PIW = "piw";
	public static final String DESC = "Desc";
	public static final String ASC = "asc";

	public static final String REGION = "region";
	public static final String MOST_POPULAR = "mostpopular";
	public static final String BOOKMARKED = "bookmarked";
	public static final String BOOKMARKED_FOR_YOU = "Bookmarked";
	public static final String REGISTERED = "registered";
	public static final int MINIMUM_SCORE = 4;
	public static final String SCORE_COLUMN = "score";
	public static final String TITLE = "title";
	public static final String SPEAKERS = "presenterName";
	public static final String LEARNING_TYPE = "learningType";
	public static final String DOCUMENTATION = "product_documentation";
	public static final String SUCCESSTIPS = "success_tips";
	public static final String CONTENT_TYPE = "Content Type";
	public static final String CONTENT_TYPE_PRM = "contentType";
	public static final String LIVE_EVENTS = "Live Events";
	public static final String LANGUAGE = "Language";
	public static final String LANGUAGE_PRM = "language";
	public static final String CONTENT_TYPE_FIELD = "contentType";
	public static final String LIVE_WEBINAR= "Live Webinar";
	public static final String ROLE = "Role";
	public static final String MODEL = "Model";
	public static final String SUCCESS_TRACK = "Success Tracks";
	public static final String TECHNOLOGY = "Technology";
	public static final String CAMPUS = "CAMPUS";
	public static final String CAMPUS_NETWORK = "Campus Network";
	public static final String ASSET_FACET = "assetFacet";
	public static final String FOR_YOU_FILTER = "For You";
	public static final String NEW = "New";
	public static final String ID = "id";
	public static final String UPCOMING_EVENTS = "Upcoming Events";
	public static final String RECENTLY_VIEWED = "Recently Viewed";
	public static final String DESCRIPTION = "description";
	public static final String LIFECYCLE = "Lifecycle";
	public static final String LEARNINGMODULE = "learningmodule";
	public static final String CX_INSIGHTS = "CX Insights";
	public static final String FILTER_KEY = "filter";
	public static final String ST_FILTER_KEY = "Success Tracks";
	public static final String POPULAR_ACROSS_PARTNERS = "Popular Across Partners";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String RESOURCE_ID_PARAM = "resourceId";
	public static final String RESOURCE_ID_LEARNING = "CXPP_LEARNING";
	public static final String BE_SPLIT_IO_FLAG = "be_PXC-219_OKTA";
	public static final String POPULAR_ACROSS_PARTNERS_PATH = "popularAcrossPartners";
	public static final String POPULAR_AT_PARTNER_PATH = "popularAtPartner";
	public static final String FEATURED_CONTENT = "Featured";
	public static final String SPECIALIZATION_FILTER = "specialization";
	public static final String PLS_SPEC_TYPE = "PLS";
	public static final String OFFER_SPEC_TYPE = "ATX/ACC";
	public static final String CISCO_PLUS_FILTER = "Cisco+";
	public static final String CISCO_PLUS_FILTER_PRM = "ciscoplus";
	public static final String SUCCESS_TIPS_VIDEO = "Video";
	public static final String RECOMMENDED_RESOURCE = "Recommended Resource";
	public static final String SUCCESS_TIPS_RECOMMENDED_RESOURCE = "web";
	public static final String HCAAS_SPECIALIZATION_QUERY_TERM = "hcaasStatus";
	public static final String PLS_SPECIALIZATION_QUERY_TERM = "plsStatus";
	public static final String HCAAS_SPECIALIZATION_TERM = "HcaaS";
	public static final String HCAAS_FLAG = "hcaasflag";
	public static final String SUCCESSTRACK_FLAG = "successTrackFlag";
	public static final String SUCCESS_TIPS_SPLIT_KEY="be_PXC-26267-displaySuccessTips";
	public static final String CISCO_PLUS_DB_FILED = "ciscoplus";
	
	public static final String DEPLOY = "deploy";
	
	public static final String DEFAULT_SORT_FIELD = "sort_by_date";
	public static final Direction DEFAULT_SORT_ORDER = Sort.Direction.DESC;
	public static final String CONTENT_TYPE_FILTER = "Content Type";
	public static final String LANGUAGE_FILTER = "Language";
	public static final String LIVE_EVENTS_FILTER = "Live Events";
	public static final String SUCCESS_TRACKS_FILTER = "Success Tracks";  
	public static final String LIFECYCLE_FILTER="Lifecycle";
	public static final String TECHNOLOGY_FILTER = "Technology";
	public static final String ROLE_FILTER = "Role";
	public static final String CISCOPLUS_FILTER = "Cisco+";
	public static final String[] FILTER_CATEGORIES = new String[]{
			CISCOPLUS_FILTER, SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER, //DOCUMENTATION_FILTER,
			ROLE_FILTER, 
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER};

	public static final String[] FILTER_CATEGORIES_ROLE = new String[]{ 
			ROLE_FILTER, CISCOPLUS_FILTER, SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER,
			LIVE_EVENTS_FILTER, FOR_YOU_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER, CISCOPLUS_FILTER};

	public static final String[] FILTER_CATEGORIES_TOPPICKS = new String[]{ ROLE_FILTER, //CISCOPLUS_FILTER, 
			SUCCESS_TRACKS_FILTER, LIFECYCLE_FILTER, TECHNOLOGY_FILTER,
			LIVE_EVENTS_FILTER, CONTENT_TYPE_FILTER, LANGUAGE_FILTER};
	
	public static final String[] FOR_YOU_KEYS = new String[]{"New","Top Picks","Based on Your Customers",
			"Bookmarked","Popular with Partners"};

	/** lmap **/
	public static final String LEARNING_MAP_TYPE = "learningmap";
	
	public static final String REG_CHARS= "[\\Q!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\\E]";
	public static final String TECHNOLOGY_DB_TABLE = "Technology";
	public static final String ROLE_DB_TABLE = "Skill";
	public static final String TOPPICKS = "Toppicks";
	
	/** Preferences **/
	public static final String TIME_INTERVAL_FILTER = "Time Interval";
	public static final Map<String,String>PREFERENCE_FILTER_MAPPING = new HashMap<>(); 
	static {
		PREFERENCE_FILTER_MAPPING.put("role", ROLE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("technology", TECHNOLOGY_FILTER);
		PREFERENCE_FILTER_MAPPING.put("language", LANGUAGE_FILTER);
		PREFERENCE_FILTER_MAPPING.put("region", LIVE_EVENTS_FILTER);
		PREFERENCE_FILTER_MAPPING.put("timeinterval", TIME_INTERVAL_FILTER);
		PREFERENCE_FILTER_MAPPING.put("specialization", Constants.SPECIALIZATION_FILTER);

	}
	public static final Integer TOP_PICKS_LIMIT = 25;
	public static final String TI_START_TIME = "startTime";
	public static final String TI_END_TIME = "endTime";
	public static final String TI_TIME_ZONE = "timeZone";
	public static final int TWO = 2;	

}
