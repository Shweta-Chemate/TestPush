package com.cisco.cx.training.constants;

public class ProductDocumentationConstants{	
	
	public static final String FIELDS_CL = " cl.learning_item_id, cl.learning_type, cl.title , cl.description, cl.status, "
			+ " cl.registrationurl, cl.presentername, cl.recordingurl, cl.duration, cl.piw_region, "
			+ " cl.piw_score, cl.piw_language, cl.sort_by_date, cl.learning_map_id, cl.avg_rating_percentage, cl.total_completions, cl.votes_percentage, cl.specialization ";
	
	
	public static final String CASE_CLAUSE = " ( "
			+ " case when :joinTable='Technology' then learning_item_id in ( "
			+ " select ct.learning_item_id from cxpp_db.cxpp_learning_technology ct "
			+ " union "
			+ " select st.learning_item_id from cxpp_db.cxpp_learning_successtrack st "
			+ " ) "
			+ " when :joinTable='Skill' then learning_item_id in (select cr.learning_item_id from cxpp_db.cxpp_learning_roles cr) "
			+ " else 1=1 end "
			+ " ) ";
	
	public static final String CASE_CLAUSE_WHERE = " where " + CASE_CLAUSE;			
	public static final String CASE_CLAUSE_AND = " and " + CASE_CLAUSE;
	
	public static final String CASE_CLAUSE_CL = " ( "
			+ " case when :joinTable='Technology' then cl.learning_item_id in ( "
			+ " select ct.learning_item_id from cxpp_db.cxpp_learning_technology ct"
			+ " union "
			+ " select st.learning_item_id from cxpp_db.cxpp_learning_successtrack st "
			+ " ) "
			+ " when :joinTable='Skill' then cl.learning_item_id in (select cr.learning_item_id from cxpp_db.cxpp_learning_roles cr) "
			+ " else 1=1 end "
			+ " ) ";
	
	public static final String CASE_CLAUSE_WHERE_CL = " where " + CASE_CLAUSE_CL;			
	public static final String CASE_CLAUSE_AND_CL = " and " + CASE_CLAUSE_CL;
	
	public static final String DYNAMIC_FROM_SUBQUERY = " from ( select " + FIELDS_CL  //cl.* "
			+ " from cxpp_db.cxpp_learning_item cl "
			+ " where "
			+ CASE_CLAUSE_CL
			+ " ) as cl ";
	
	/** all cards **/

	public static final String GET_LM_LEARNING_CARDS =" union ( select learning_map_id as learning_item_id,  "
			+ " 'learningmap' as learning_type, "
			+ " title , description, "
			+ " null as status, null as registrationurl, null as presentername, "
			+ " null as recordingurl, null as duration, null as piw_region, "
			+ " null as piw_score, null as piw_language, null as sort_by_date, null as learning_map_id, "
			+ " avg_rating_percentage, total_completions, votes_percentage, specialization, "
			+ " null as asset_types, link as asset_links, null as learning_map"
			+ " from cxpp_db.cxpp_learning_map where learning_map_id in "
			+ " ( "
			+ " select distinct learning_map_id from cxpp_db.cxpp_learning_item cl "
			+ " where cl.learning_map_id is not null and  "
			+ CASE_CLAUSE_CL
			+ " )  " 
			+ " ) ";
			
	
	public static final String ALL_CARDS = "select cl.*, CT.asset_types,CT.asset_links, mp.title as learning_map  "
			+ DYNAMIC_FROM_SUBQUERY
			+ " left join "
			+ "	(select learning_item_id, "
			+ "	group_concat(ifnull(asset_type,'') separator ',') as asset_types, "
			+ "	group_concat(ifnull(link,'') separator ',') AS asset_links "
			+ "	from cxpp_db.cxpp_item_link  "
			+ "	group by learning_item_id) as CT "
			+ "	on cl.learning_item_id = CT.learning_item_id "
			+ " left join cxpp_db.cxpp_learning_map mp on cl.learning_map_id=mp.learning_map_id "
			+ GET_LM_LEARNING_CARDS ;
	
	public static final String GET_PD_LEARNING_CARDS = "select * from ( " + ALL_CARDS + " ) as cl "
			+ "  \n-- #sort\n";	

	
	/** search **/
	
	public static final String GET_PD_LEARNING_CARDS_SEARCH = "select * from ( " + ALL_CARDS + " ) as cl "
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken "
			+ "  \n-- #sort\n";	

	public static final String GET_PD_LEARNING_CARD_IDS_SEARCH = " select cl.learning_item_id "			
			+ " from ( " + ALL_CARDS + " ) as cl "
			+ " where lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken " ;	
	
	
	/** Filter **/
	
	public static final String GET_PD_LEARNING_BY_CONTENT_TYPE = "select distinct learning_item_id "
			+ " from cxpp_db.cxpp_item_link "
			+ " where asset_type in (:contentTypeFilter) "
			+ CASE_CLAUSE_AND ;
	
	public static final String GET_PD_LEARNING_CARDS_BY_FILTER = "select * from ( " + ALL_CARDS + " ) as cl "
			+ " where cl.learning_item_id in (:filterCards) "
			+ "  \n-- #sort\n";
	
	
	/** filter + search */
	
	public static final String GET_PD_LEARNING_CARDS_FILTERED_SEARCH = "select * from ( " + ALL_CARDS + " ) as cl "
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) "
			+ "  \n-- #sort\n";

	
	public static final String GET_PD_LEARNING_CARD_IDS_FILTERED_SEARCH = " select cl.learning_item_id "
			+ " from ( " + ALL_CARDS + " ) as cl "
			+ " where cl.learning_item_id in (:filteredCards) and "
			+ " ( lower(cl.title) like :likeToken or lower(cl.description) like :likeToken  "
			+ " or lower(cl.presentername) like :likeToken ) " ;

	
	/** other filters **/
	public static final String GET_PD_CARD_IDS_REGION = " select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ " where  piw_region in (:values)" 
			+ CASE_CLAUSE_AND ;					

	public static final String GET_PD_CARD_IDS_LG = " select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ " where  piw_language in (:values)" 
			+ CASE_CLAUSE_AND ;					

	public static final String GET_PD_CARD_IDS_AT = " select learning_item_id from ( "
			+ " ( select archetype, learning_item_id "
			+ " from cxpp_db.cxpp_learning_item  "
			+ CASE_CLAUSE_WHERE
			+ " ) "
			+ " union "
			+ " ( select distinct archetype, learning_map_id as learning_item_id "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_map_id is not null"
			+ CASE_CLAUSE_AND
			+ " ) "
			+ " ) as T"
			+ " where  archetype in (:values)" 
			 ;					

	public static final String GET_PD_CARD_IDS_TC = " select learning_item_id from ( "
			+ " ( select technology, learning_item_id from cxpp_db.cxpp_learning_technology  "
			+ CASE_CLAUSE_WHERE
			+ " ) "
			+ " union "
			+ " ( select technology, learning_map_id as learning_item_id from cxpp_db.cxpp_learning_item cl "
			+ " inner join cxpp_db.cxpp_learning_technology tc "
			+ " on cl.learning_item_id=tc.learning_item_id "
			+ " where  cl.learning_map_id is not null "
			+ CASE_CLAUSE_AND_CL
			+ " ) "
			+ " ) as T"
			+ " where technology in (:values) "
			;					

	public static final String GET_LM_STUCPT_WITH_COUNT =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " pitstop, usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ ")";
	
	public static final String GET_LM_STUCPT_WITH_COUNT_BY_CARDS =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " pitstop, usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ " and lmt.learning_map_id in (:cardIds) "
			+ ")";
	
	public static final String GET_PD_CARD_IDS_BY_STUCPS = " select learning_item_id from ( "
			+ " ( select learning_item_id , pitstop, usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc "
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " inner join cxpp_db.cxpp_learning_pitstop ps "
			+ " on ps.usecase_id = uc.usecase_id "
			+ CASE_CLAUSE_WHERE  + " ) "
			+ GET_LM_STUCPT_WITH_COUNT 
			+ " ) as T "
			+ " where pitstop in (:pitstopInp) and usecase = :usecaseInp and successtrack = :successtrackInp ";
			//+ " where ps.pitstop in (:pitstopInp) and uc.usecase = :usecaseInp and st.successtrack = :successtrackInp"					

	/** LM counts **/
	
	public static final String GET_LM_TECHNOLOGY_WITH_COUNT=" union (select distinct tc.technology, lmt.learning_map_id as learning_item_id "
			+ " from cxpp_db.cxpp_learning_technology tc left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on tc.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ ")";	
	
	public static final String GET_LM_TECHNOLOGY_WITH_COUNT_BY_CARDS=" union (select distinct tc.technology, lmt.learning_map_id as learning_item_id "
			+ " from cxpp_db.cxpp_learning_technology tc left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on tc.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ " and lmt.learning_map_id in (:cardIds) "
			+ ")";
			
	
	public static final String GET_LM_DOCUMENTATION_WITH_COUNT=" union (select  it.archetype, lm.learning_map_id as learning_item_id "
			+ "		from cxpp_db.cxpp_learning_item it "
			+ "		left join "
			+ "		cxpp_db.cxpp_learning_map lm "
			+ "		on lm.learning_map_id=it.learning_map_id "
			+ "		where it.learning_map_id is not null "
			+ CASE_CLAUSE_AND
			+ ")";	
	
	public static final String GET_LM_DOCUMENTATION_WITH_COUNT_BY_CARDS=" union (select  it.archetype, lm.learning_map_id as learning_item_id "
			+ "		from cxpp_db.cxpp_learning_item it "
			+ "		left join "
			+ "		cxpp_db.cxpp_learning_map lm "
			+ "		on lm.learning_map_id=it.learning_map_id "
			+ "		where it.learning_map_id is not null "
			+ CASE_CLAUSE_AND
			+ " and learning_item_id in (:cardIds) "
			+ ")";	

	
	/** count by cards **/	
	
	public static final String GET_PD_TECHNOLOGY_WITH_COUNT_BY_CARD = " select technology as dbkey, count(*) as dbvalue "
					+ " from ( select technology, learning_item_id "
					+ " from cxpp_db.cxpp_learning_technology "
					+ " where learning_item_id in (:cardIds) "
					+ CASE_CLAUSE_AND 
					+ GET_LM_TECHNOLOGY_WITH_COUNT_BY_CARDS + " ) as T "					
					+ " group by technology "
					+ " order by technology;";

	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT_BY_CARD = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by asset_type "
			+ " order by asset_type ";	

	public static final String GET_PD_LANGUAGE_WITH_COUNT_BY_CARD = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by piw_language "
			+ " order by piw_language ";	

public static final String GET_PD_DOCUMENTATION_WITH_COUNT_BY_CARD = "select archetype as dbkey, count(*) as dbvalue "
			+ " from ( select archetype, learning_item_id "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "	
			+ CASE_CLAUSE_AND
			+ GET_LM_DOCUMENTATION_WITH_COUNT_BY_CARDS 
			+ " ) as T "					 
			+ " group by archetype "
			+ " order by archetype ";

	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT_BY_CARD = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ " group by piw_region "
			+ " order by piw_region ";
	
	/** all counts **/
	
	public static final String GET_PD_CONTENT_TYPE_WITH_COUNT = "select asset_type as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_item_link "
			+ CASE_CLAUSE_WHERE 
			+ " group by asset_type order by asset_type ";	

	public static final String GET_PD_TECHNOLOGY_WITH_COUNT = "select technology as dbkey, count(*) as dbvalue "
			+ " from ( select technology, learning_item_id "
			+ " from cxpp_db.cxpp_learning_technology "
			+ CASE_CLAUSE_WHERE 
			+ GET_LM_TECHNOLOGY_WITH_COUNT + " ) as T "
			+ " group by technology order by technology;";	

	public static final String GET_PD_LANGUAGE_WITH_COUNT = "select piw_language as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "		
			+ CASE_CLAUSE_WHERE 
			+ " group by piw_language "
			+ " order by piw_language ";	

	public static final String GET_PD_DOCUMENTATION_WITH_COUNT = "select archetype as dbkey, count(*) as dbvalue "
			+ " from ( select archetype, learning_item_id "
			+ " from cxpp_db.cxpp_learning_item "
			+ CASE_CLAUSE_WHERE 
			+ GET_LM_DOCUMENTATION_WITH_COUNT 
			+ " ) as T "
			+ " group by archetype "
			+ " order by archetype ";

	
	public static final String GET_PD_LIVE_EVENTS_WITH_COUNT = "select piw_region as dbkey, count(*) as dbvalue "
			+ " from cxpp_db.cxpp_learning_item "
			+ CASE_CLAUSE_WHERE 
			+ " group by piw_region "
			+ " order by piw_region ";

	
	/** ST **/	
	public static final String GET_PD_ST_UC_PS_WITH_COUNT = "select count(*) as dbvalue ,  pitstop, usecase, successtrack "
			+ " from ( select learning_item_id, pitstop, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ CASE_CLAUSE_WHERE   // WHERE !
			+ GET_LM_STUCPT_WITH_COUNT + " ) as T "
			+ " group by pitstop,usecase,successtrack "
			+ " order by successtrack,usecase,pitstop ";	

	public static final String GET_PD_ST_UC_PS_WITH_COUNT_BY_CARDS = "select count(*) as dbvalue ,  pitstop, usecase, successtrack "
			+ " from ( select learning_item_id, pitstop, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " where learning_item_id in (:cardIds) "	
			+ CASE_CLAUSE_AND   // AND !
			+ GET_LM_STUCPT_WITH_COUNT_BY_CARDS + " ) as T "			
			+ " group by pitstop,usecase,successtrack "
			+ " order by successtrack,usecase,pitstop ";	

	
	/** skill  - for role no case clause required **/
	
	public static final String GET_LM_ROLE_WITH_COUNT = "union (  "
			+ "select distinct tc.roles, lmt.learning_map_id as learning_item_id "
			+ "from cxpp_db.cxpp_learning_roles tc left join "
			+ "( "
			+ "select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ "from cxpp_db.cxpp_learning_map lm "
			+ "inner join cxpp_db.cxpp_learning_item it "
			+ "on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ ") lmt "
			+ "on tc.learning_item_id=lmt.learning_item_id "
			+ "where lmt.learning_map_id is not null "
			+ ")";
	
	public static final String GET_LM_ROLE_WITH_COUNT_BY_CARDS = "union (  "
			+ "select distinct tc.roles, lmt.learning_map_id as learning_item_id "
			+ "from cxpp_db.cxpp_learning_roles tc left join "
			+ "( "
			+ "select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ "from cxpp_db.cxpp_learning_map lm "
			+ "inner join cxpp_db.cxpp_learning_item it "
			+ "on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ ") lmt "
			+ "on tc.learning_item_id=lmt.learning_item_id "
			+ "where lmt.learning_map_id is not null "
			+ " and lmt.learning_map_id in (:cardIds) "
			+ ")";
	
	
	
	public static final String GET_PD_ROLE_WITH_COUNT = "select roles as dbkey, count(*) as dbvalue "
			+ " from ( select roles, learning_item_id "
			+ "	from cxpp_db.cxpp_learning_roles 		"
			+ CASE_CLAUSE_WHERE 
			+ GET_LM_ROLE_WITH_COUNT + " ) as T "
			+ " group by roles order by roles ";	

	public static final String GET_PD_CARD_IDS_ROLE = " select learning_item_id from ( "
			+ " ( select roles, learning_item_id from cxpp_db.cxpp_learning_roles  "
			+ CASE_CLAUSE_WHERE
			+ " ) "
			+ " union "
			+ " ( select roles, learning_map_id as learning_item_id from cxpp_db.cxpp_learning_item cl "
			+ " inner join cxpp_db.cxpp_learning_roles tc "
			+ " on cl.learning_item_id=tc.learning_item_id "
			+ " where  cl.learning_map_id is not null "		
			+ CASE_CLAUSE_AND_CL
			+ " ) "
			+ " ) as T"
			+ " where roles in (:values) "
			;										

	public static final String GET_PD_ROLE_WITH_COUNT_BY_CARD = " select roles as dbkey, count(*) as dbvalue "
			+ " from ( select roles, learning_item_id "
			+ " from cxpp_db.cxpp_learning_roles "	
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND 
			+ GET_LM_ROLE_WITH_COUNT_BY_CARDS + " ) as T "			
			+ " group by roles "
			+ " order by roles;";

	/** For You - New **/	
	public static final String GET_PD_YOU_CARD_IDS_BY_CARD = "select learning_item_id "
			+ " from cxpp_db.cxpp_learning_item "
			+ " where learning_item_id in (:cardIds) "
			+ CASE_CLAUSE_AND ;			

	/** learning map **/
	
	public static final String GET_PD_LEARNING_MAP_COUNTS = "select mp.learning_map_id as dbkey, count(*) as dbvalue"
			+ " from cxpp_db.cxpp_learning_item cl "
			+ " inner join cxpp_db.cxpp_learning_map mp "
			+ " on cl.learning_map_id=mp.learning_map_id "
			+ " where cl.learning_map_id is not null "
			+ " group by mp.learning_map_id;";			

	
	/** lifecycle **/
	
	public static final String GET_LM_STUC_WITH_COUNT =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ ")";
	
	public static final String GET_LM_STUC_WITH_COUNT_BY_CARDS =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ " and lmt.learning_map_id in (:cardIds) "
			+ ")";
	
	public static final String GET_PD_ST_UC_WITH_COUNT = "select count(*) as dbvalue ,  usecase, successtrack "
			+ " from ( select learning_item_id, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "		
			+ CASE_CLAUSE_WHERE   // WHERE !
			+ GET_LM_STUC_WITH_COUNT + " ) as T "
			+ " group by usecase,successtrack "
			+ " order by successtrack,usecase ";	

	public static final String GET_LM_PT_WITH_COUNT =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " pitstop "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ ")";
	
	public static final String GET_LM_PT_WITH_COUNT_BY_CARDS =" union (select distinct lmt.learning_map_id as learning_item_id , "
			+ " pitstop "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " left join "
			+ " ( "
			+ " select lm.learning_map_id, lm.title as learning_map, it.learning_item_id  "
			+ " from cxpp_db.cxpp_learning_map lm "
			+ " inner join cxpp_db.cxpp_learning_item it "
			+ " on lm.learning_map_id=it.learning_map_id "
			+ CASE_CLAUSE_WHERE
			+ " ) lmt "
			+ " on st.learning_item_id=lmt.learning_item_id "
			+ " where lmt.learning_map_id is not null"
			+ " and lmt.learning_map_id in (:cardIds) "
			+ ")";

	public static final String GET_PD_PS_WITH_COUNT = "select count(*) as dbvalue ,  pitstop as dbkey "
			+ " from ( select learning_item_id, pitstop "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ CASE_CLAUSE_WHERE   // WHERE !
			+ GET_LM_PT_WITH_COUNT + " ) as T "
			+ " group by pitstop "
			+ " order by pitstop ";	
	
	public static final String GET_PD_CARD_IDS_BY_PITSTOP = " select learning_item_id from ( "
			+ " ( select learning_item_id , pitstop, usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc "
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " inner join cxpp_db.cxpp_learning_pitstop ps "
			+ " on ps.usecase_id = uc.usecase_id "
			+ CASE_CLAUSE_WHERE  + " ) "
			+ GET_LM_STUCPT_WITH_COUNT 
			+ " ) as T "
			+ " where pitstop in (:pitstopInp)  ";
			//+ " where ps.pitstop in (:pitstopInp) and uc.usecase = :usecaseInp and st.successtrack = :successtrackInp"					

	public static final String GET_PD_CARD_IDS_BY_STUC = " select learning_item_id from ( "
			+ " ( select learning_item_id , usecase, successtrack "
			+ " from cxpp_db.cxpp_learning_successtrack st "
			+ " inner join cxpp_db.cxpp_learning_usecase uc "
			+ " on uc.successtrack_id = st.successtrack_id "
			+ " inner join cxpp_db.cxpp_learning_pitstop ps "
			+ " on ps.usecase_id = uc.usecase_id "
			+ CASE_CLAUSE_WHERE  + " ) "
			+ GET_LM_STUC_WITH_COUNT 
			+ " ) as T "
			+ " where usecase in (:usecaseInp) and successtrack = :successtrackInp ";
			//+ " where ps.pitstop in (:pitstopInp) and uc.usecase = :usecaseInp and st.successtrack = :successtrackInp"					

	public static final String GET_PD_ST_UC_WITH_COUNT_BY_CARDS = "select count(*) as dbvalue , usecase, successtrack "
			+ " from ( select learning_item_id, usecase, successtrack "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ " where learning_item_id in (:cardIds) "	
			+ CASE_CLAUSE_AND   // AND !
			+ GET_LM_STUC_WITH_COUNT_BY_CARDS + " ) as T "			
			+ " group by usecase,successtrack "
			+ " order by successtrack,usecase ";	

	public static final String GET_PD_PS_WITH_COUNT_BY_CARDS = "select count(*) as dbvalue ,  pitstop as dbkey "
			+ " from ( select learning_item_id, pitstop "
			+ "	from cxpp_db.cxpp_learning_successtrack st  "
			+ "	inner join cxpp_db.cxpp_learning_usecase uc  "
			+ "	on uc.successtrack_id = st.successtrack_id  "
			+ "	inner join cxpp_db.cxpp_learning_pitstop ps  "
			+ "	on ps.usecase_id = uc.usecase_id  "
			+ " where learning_item_id in (:cardIds) "	
			+ CASE_CLAUSE_AND   // AND !
			+ GET_LM_PT_WITH_COUNT_BY_CARDS + " ) as T "			
			+ " group by pitstop "
			+ " order by pitstop ";	
	

	/** for preferences **/	
	public static final String GET_UPCOMING_WEBINARS = " select * from ( " + ALL_CARDS + " ) as UW "
			+ " where UW.asset_types like '%Live Webinar%' and  UW.sort_by_date > current_date() and UW.status!='cancelled' " ;


	public static final String GET_SPECIALIZED_CARDS = "select learning_item_id from cxpp_db.cxpp_learning_item where specialization in (:specializations) ";

}





