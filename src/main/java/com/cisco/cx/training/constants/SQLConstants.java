package com.cisco.cx.training.constants;

public class SQLConstants {
	
	public static final String GET_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as label, count(*) as count from cxpp_db.cxpp_item_link where asset_type IS NOT NULL and asset_type!='null' and learning_item_id in (:learningItemIds) \n"
			+ " group by asset_type;";
	
	public static final String GET_REGION_WITH_COUNT_BY_CARD = "select piw_region as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_region IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_region;";
	
	public static final String GET_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_language IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_language;";
	
	public static final String GET_NEW_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where sort_by_date  between (current_date() - interval 1 month) and  current_date() and status!='cancelled' order by sort_by_date desc limit 25";

	public static final String GET_NEW_CONTENT = "select * from (\n" + GET_NEW_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";
	
	public static final String GET_UPCOMING_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where asset_type='Live Webinar' and  sort_by_date > current_date() and status!='cancelled' order by sort_by_date asc limit 25";

	public static final String GET_UPCOMING_CONTENT = "select * from (\n" + GET_UPCOMING_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";

	public static final String GET_RECENTLY_VIEWED_CONTENT_BASE =  "select content.* from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where status.user_id=:userId  and content.id=status.learning_item_id "
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

	public static final String GET_DOC_WITH_COUNT_BY_CARD = "select archetype as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content where archetype IS NOT NULL and id in (:learningItemIds) "
			+ " group by archetype";

	public static final String GET_SA_CAMPUS_COUNT = "select count(*) \n" +
			" from cxpp_db.cxpp_learning_content where learning_type='successacademy' and asset_model='Success Track' and asset_facet='CAMPUS' and id in (:learningItemIdsList) \n";

	public static final String GET_CARD_IDs_CT =  "select distinct learning_item_id from cxpp_db.cxpp_item_link  "
			+ " where asset_type  in (:values) and learning_item_id in (:learningItemIdsList)" ;

	public static final String GET_CARD_IDs_LANG = "select id from cxpp_db.cxpp_learning_content "
			+ " where piw_language in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_REG = "select id from cxpp_db.cxpp_learning_content  "
			+ " where piw_region in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_FACET = "select id from cxpp_db.cxpp_learning_content  "
			+ " where asset_facet in (:values) and id in (:learningItemIdsList)";

	public static final String GET_ASSET_MODEL = "select distinct asset_model from cxpp_db.cxpp_learning_content  "
			+ " where asset_facet=(:value) ";

	public static final String GET_CARD_IDs_ST = "select lc.id "
			+ "from cxpp_db.cxpp_learning_content lc "
			+ "left join cxpp_db.cxpp_learning_successtrack st "
			+ "on lc.id=st.learning_item_id "
			+ "where lc.id in (:learningItemIdsList) "
			+ " and (st.successtrack in (:values) or lc.asset_facet in (:values)) ";

	public static final String GET_CARD_IDs_DOC = "select id from cxpp_db.cxpp_learning_content  "
			+ " where archetype in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_PITSTOP_TAGGED = "select distinct ptview.learning_item_id from\n" +
			"(SELECT learning_item_id,pitstop FROM cxpp_db.cxpp_learning_pitstop_temp\n" +
			"UNION\n" +
			"SELECT learning_map_id as learning_item_id,pitstop from cxpp_db.cxpp_learning_pitstop_temp pt left join cxpp_db.cxpp_learning_item item on pt.learning_item_id=item.learning_item_id) \n" +
			" as ptview where ptview.pitstop is not null ";

	public static final String GET_CARD_IDs_PITSTOP_TAGGED_FILTER = "select distinct ptview.learning_item_id from\n" +
			"(SELECT learning_item_id,pitstop FROM cxpp_db.cxpp_learning_pitstop_temp\n" +
			"UNION\n" +
			"SELECT learning_map_id as learning_item_id,pitstop from cxpp_db.cxpp_learning_pitstop_temp pt left join cxpp_db.cxpp_learning_item item on pt.learning_item_id=item.learning_item_id) \n" +
	        " as ptview where ptview.pitstop is not null and ptview.pitstop in (:lfcFilters)";

	public static final String GET_SORTED_BY_TITLE_ASC = "SELECT * FROM\n" + 
			"cxpp_learning_content\n" + 
			"where id IN (:learningItemIdsList)\n" + 
			"ORDER BY CASE \n" + 
			"WHEN title REGEXP '^[A-Za-z0-9]' THEN 1\n" + 
			"WHEN title IS NULL THEN 3\n" + 
			"ELSE 2 END , title asc";
	
	public static final String GET_SORTED_BY_TITLE_DESC = "SELECT * FROM\n" + 
			"cxpp_learning_content\n" + 
			"where id IN (:learningItemIdsList)\n" + 
			"ORDER BY CASE \n" + 
			"WHEN title REGEXP '^[A-Za-z0-9]' THEN 1\n" + 
			"WHEN title IS NULL THEN 3\n" + 
			"ELSE 2 END , title desc";
}
