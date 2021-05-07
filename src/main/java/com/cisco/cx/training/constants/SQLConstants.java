package com.cisco.cx.training.constants;

public class SQLConstants {
	
	public static final String GET_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where asset_type IS NOT NULL and learning_type!='successacademy' and id in (:learningItemIds) "
			+ " group by asset_type;";
	
	public static final String GET_REGION_WITH_COUNT_BY_CARD = "select piw_region as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_region IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_region;";
	
	public static final String GET_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_language IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_language;";
	
	public static final String GET_NEW_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where sort_by_date  between (current_date() - interval 1 month) and  current_date() order by sort_by_date desc limit 25";

	public static final String GET_NEW_CONTENT = "select * from (\n" + GET_NEW_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";
	
	public static final String GET_UPCOMING_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where asset_type='Live Webinar' and  sort_by_date > current_date() order by sort_by_date asc limit 25";

	public static final String GET_UPCOMING_CONTENT = "select * from (\n" + GET_UPCOMING_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";

	public static final String GET_RECENTLY_VIEWED_CONTENT_BASE =  "select content.* from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where status.puid=:puid and status.user_id=:userId  and content.id=status.learning_item_id "
			+ " order by status.viewed_timestamp desc limit 25";
	
	public static final String GET_RECENTLY_VIEWED_CONTENT = "select * from (\n" + GET_RECENTLY_VIEWED_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";
	
	public static final String GET_SUCCESSACADEMY_FILTER_WITH_COUNT = "select asset_facet as label, count(*) as count \n" + 
			" from cxpp_db.cxpp_learning_content where learning_type='successacademy' and asset_model IS NOT NULL and asset_facet IS NOT NULL and asset_model = :asset_model and id in (:learningItemIds)\n" + 
			" group by asset_facet;";

	public static final String GET_PD_CARDS__BY_ST = "select lc.* "
			+ "from cxpp_db.cxpp_learning_successtrack st "
			+ "inner join cxpp_db.cxpp_learning_content lc "
			+ "on st.learning_item_id=lc.id "
			+ "where learning_item_id in (:cardIds) "
			+ "and st.successtrack in (:successTracks)";

	public static final String GET_PD_ST_WITH_COUNT_BY_CARDS = "select successtrack as label, count(*) as count "
			+ "from cxpp_db.cxpp_learning_successtrack st  "
			+ "where learning_item_id in (:cardIds) "
			+ "group by successtrack "
			+ "order by successtrack";
}
