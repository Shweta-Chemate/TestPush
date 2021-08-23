package com.cisco.cx.training.constants;

public class SQLConstants {
	
	public static final String GET_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as label, count(*) as count from cxpp_db.cxpp_item_link where asset_type IS NOT NULL and asset_type!='null' and learning_item_id in (:learningItemIds) \n"
			+ " group by asset_type order by asset_type";
	
	public static final String GET_REGION_WITH_COUNT_BY_CARD = "select piw_region as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_region IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_region order by piw_region";
	
	public static final String GET_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as label, count(*) as count "
			+ " from cxpp_db.cxpp_learning_content " + " where piw_language IS NOT NULL and id in (:learningItemIds) "
			+ " group by piw_language order by piw_language";
	
	public static final String GET_NEW_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where sort_by_date  between (current_date() - interval 1 month) and  current_date() and status!='cancelled' order by sort_by_date desc limit 25";

	public static final String GET_NEW_CONTENT = "select * from (\n" + GET_NEW_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";
	
	public static final String GET_NEW_CONTENT_IDs = "select id from (\n" + GET_NEW_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";;

	public static final String GET_UPCOMING_CONTENT_BASE = "select * from cxpp_db.cxpp_learning_content where asset_type='Live Webinar' and  sort_by_date > current_date() and status!='cancelled' order by sort_by_date asc limit 25";

	public static final String GET_UPCOMING_CONTENT = "select * from (\n" + GET_UPCOMING_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";

	public static final String GET_RECENTLY_VIEWED_CONTENT_BASE =  "select content.* from cxpp_db.cxpp_learning_content content,cxpp_db.cxpp_learning_status status"
			+ " where status.user_id=:userId  and content.id=status.learning_item_id "
			+ " order by status.viewed_timestamp desc limit 25";
	
	public static final String GET_RECENTLY_VIEWED_CONTENT = "select * from (\n" + GET_RECENTLY_VIEWED_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";
	
	public static final String GET_RECENTLY_VIEWED_IDs = "select id from (\n" + GET_RECENTLY_VIEWED_CONTENT_BASE + ") base\n" +
			"where base.id in (:learningItemIds)";;

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
			+ " group by archetype order by archetype";

	public static final String GET_CARD_IDs_CT =  "select distinct learning_item_id from cxpp_db.cxpp_item_link  "
			+ " where asset_type  in (:values) and learning_item_id in (:learningItemIdsList)" ;

	public static final String GET_CARD_IDs_LANG = "select id from cxpp_db.cxpp_learning_content "
			+ " where piw_language in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_REG = "select id from cxpp_db.cxpp_learning_content  "
			+ " where piw_region in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_DOC = "select id from cxpp_db.cxpp_learning_content  "
			+ " where archetype in (:values) and id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_PITSTOP_VIEW = "(SELECT learning_item_id,pitstop FROM cxpp_db.cxpp_learning_pitstop_temp\n" +
			"UNION \n" +
			"SELECT learning_item_id, pitstop FROM cxpp_db.cxpp_learning_successtrack st,cxpp_db.cxpp_learning_usecase uc,cxpp_db.cxpp_learning_pitstop pt where \n" + 
			"st.successtrack_id=uc.successtrack_id and uc.usecase_id=pt.usecase_id \n" +
			"UNION \n" +
			"SELECT learning_map_id as learning_item_id,pitstop from cxpp_db.cxpp_learning_pitstop_temp pt left join cxpp_db.cxpp_learning_item item on pt.learning_item_id=item.learning_item_id AND learning_map_id IS NOT NULL) \n" +
			"as ptview ";

	public static final String GET_CARD_IDs_PITSTOP_TAGGED = "select distinct ptview.learning_item_id from\n" + GET_CARD_IDs_PITSTOP_VIEW + "where ptview.pitstop is not null ";

	public static final String GET_CARD_IDs_PITSTOP_TAGGED_FILTER = "select distinct ptview.learning_item_id from \n" + GET_CARD_IDs_PITSTOP_VIEW
	        + " where ptview.pitstop is not null and ptview.pitstop in (:lfcFilters)";

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
	
	public static final String GET_CARD_IDs_ROLE_VIEW =  "( select learning_item_id,roles from cxpp_db.cxpp_learning_roles \n"
			+ " UNION \n"
			+ " select learning_map_id as learning_item_id, roles from cxpp_db.cxpp_learning_roles roles, cxpp_db.cxpp_learning_item item where roles.learning_item_id=item.learning_item_id and learning_map_id is not null ) as rolesView\n";
	
	public static final String GET_CARD_IDs_TECH_VIEW =  "( select learning_item_id,technology from cxpp_db.cxpp_learning_technology where technology!='null'\n"
			+ " UNION \n"
			+ " select learning_map_id as learning_item_id, technology from cxpp_db.cxpp_learning_technology tech, cxpp_db.cxpp_learning_item item where tech.learning_item_id=item.learning_item_id and learning_map_id is not null and technology!='null') as techView\n";
	
	public static final String GET_ROLE_WITH_COUNT_BY_CARD = "select roles as label, count(*) as count from "  + GET_CARD_IDs_ROLE_VIEW + " where learning_item_id in (:learningItemIds) \n"
			+ " group by roles order by roles";

	public static final String GET_TECH_WITH_COUNT_BY_CARD = "select technology as label, count(*) as count from " + GET_CARD_IDs_TECH_VIEW + " where learning_item_id in (:learningItemIds) \n"
			+ " group by technology order by technology";

	public static final String GET_CARD_IDs_ROLE = "select distinct learning_item_id from " + GET_CARD_IDs_ROLE_VIEW + "\n"
			+ " where rolesView.roles in (:values) and rolesView.learning_item_id in (:learningItemIdsList)";

	public static final String GET_CARD_IDs_TECH = "select distinct learning_item_id from " + GET_CARD_IDs_TECH_VIEW + "\n"
			+ " where techView.technology in (:values) and techView.learning_item_id in (:learningItemIdsList) ";

	public static final String GET_CARD_IDs_ROLE_FILT = "select distinct learning_item_id from " + GET_CARD_IDs_ROLE_VIEW + "\n"
			+ " where rolesView.roles in (:values) ";

	public static final String GET_CARD_IDs_TECH_FILT = "select distinct learning_item_id from " + GET_CARD_IDs_TECH_VIEW + "\n"
			+ " where techView.technology in (:values) ";

	public static final String GET_LFC_WITH_COUNT_BY_CARD =  "select ptview.pitstop as label, count(*) as count from " + GET_CARD_IDs_PITSTOP_VIEW + " where ptview.learning_item_id in (:learningItemIds) \n"
			+ " group by ptview.pitstop order by  ptview.pitstop";

	public static final String GET_CARD_IDs_LFC = "select distinct learning_item_id from " + GET_CARD_IDs_PITSTOP_VIEW 
			+ " where ptview.pitstop in (:values) and ptview.learning_item_id in (:learningItemIds)";

	public static final String GET_PD_ST_UC_WITH_COUNT = "select count(*) as dbvalue , usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id where st.learning_item_id in (:learningItemIds) "
			+ " group by usecase,successtrack "
			+ " order by successtrack,usecase ";

	public static final String GET_PD_CARD_IDS_BY_STUC = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc"
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " where  uc.usecase in (:usecaseInp) and st.successtrack = :successtrackInp";

	public static final String GET_PD_CARD_IDS_BY_STUC_FILTER = GET_PD_CARD_IDS_BY_STUC + " and st.learning_item_id in (:learningItemIds)";

	public static final String GET_SUCCESSTRACKS_COUNT = "select count(*) FROM cxpp_db.cxpp_learning_successtrack";
	
	public static final String GET_LIFECYCLE_COUNT = "select count(*) FROM cxpp_db.cxpp_learning_pitstop_temp";
	
	public static final String GET_ROLES_COUNT = "select count(*) FROM cxpp_db.cxpp_learning_roles";
	
	public static final String GET_TECHNOLOGY_COUNT= "select count(*) FROM cxpp_db.cxpp_learning_technology";

	public static final String GET_POPULAR_ACCROSS_PARTNERS = "select * from\n" +
			"((select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='learningmodule' or learning_type=\"learningmap\" and popularity_weight > 0 order by popularity_weight desc limit :limit)\n" +
			"UNION\n" +
			"(select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='piw' or learning_type=\"st\" and popularity_weight > 0 order by popularity_weight desc limit :limit)\n" +
			"UNION\n" +
			"(select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='product_documentation' and popularity_weight > 0 order by popularity_weight desc limit :limit)) as idView\n" +
			"left join\n" +
			"cxpp_db.cxpp_learning_content\n" +
			"on idView.learning_item_id = id order by sort_by_date desc";

	public static final String GET_POPULAR_ACCROSS_PARTNERS_FILTERED = "select * from\n" +
			"((select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='learningmodule' or learning_type=\"learningmap\" and popularity_weight > 0 order by popularity_weight desc limit :limit)\n" +
			"UNION\n" +
			"(select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='piw' or learning_type=\"st\" and popularity_weight > 0 order by popularity_weight desc limit :limit)\n" +
			"UNION\n" +
			"(select learning_item_id from cxpp_db.cxpp_learning_popularity where learning_type='product_documentation' and popularity_weight > 0 order by popularity_weight desc limit :limit)) as idView \n" +
			" join\n" +
			"cxpp_db.cxpp_learning_content \n" +
			"on idView.learning_item_id = id and idView.learning_item_id in (:learningItemIds) order by sort_by_date desc";

	public static final String GET_POPULAR_AT_PARTNER = "select * from cxpp_db.cxpp_learning_bookmark_count bkcount, cxpp_db.cxpp_learning_content learning \n" +
			"where bkcount.puid = :puid and bkcount.learning_item_id=learning.id and bkcount.count>0 order by count desc limit :limit";


	public static final String GET_POPULAR_AT_PARTNER_FILTERED = "select * from cxpp_db.cxpp_learning_bookmark_count bkcount, cxpp_db.cxpp_learning_content learning \n" +
			"where bkcount.puid = :puid and bkcount.learning_item_id=learning.id and bkcount.count>0 and bkcount.learning_item_id in (:learningItemIds) order by count desc limit :limit";
}
