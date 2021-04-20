package com.cisco.cx.training.constants;

public class SQLConstants {
	
	public static final String GET_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where asset_type IS NOT NULL and id in (:learningItemIds) "
			+ " group by asset_type;";
	
	public static final String GET_REGION_WITH_COUNT_BY_CARD = "select piw_region as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_region IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_region;";
	
	public static final String GET_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_language IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_language;";
	
	public static final String GET_RECENTLY_VIEWED_CONTENT = "select content.* from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id and content.id in (:learningItemIds) "
			+ " order by status.viewed_timestamp desc limit 25;";
	
	public static final String GET_CONTENT_FILTERED_RECENTLY_VIEWED = "select content.asset_type as label, count(*) as count from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where content.asset_type IS NOT NULL and status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id and content.id in (:learningItemIds) "
			+ " group by content.asset_type order by status.viewed_timestamp desc limit 25;";

	public static final String GET_REGION_FILTERED_RECENTLY_VIEWED = "select content.piw_region as label, count(*) as count from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where content.piw_region IS NOT NULL and status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id and content.id in (:learningItemIds) "
			+ " group by content.piw_region order by status.viewed_timestamp desc limit 25;";

	public static final String GET_LANGUAGE_FILTERED_RECENTLY_VIEWED = "select content.piw_language as label, count(*) as count from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where content.piw_language IS NOT NULL and status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id and content.id in (:learningItemIds) "
			+ " group by content.piw_language order by status.viewed_timestamp desc limit 25;";

}
