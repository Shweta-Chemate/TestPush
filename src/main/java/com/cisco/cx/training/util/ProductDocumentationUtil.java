package com.cisco.cx.training.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.CollectionUtils;

import com.cisco.cx.training.app.entities.LearningItemEntity;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.UserLearningPreference;

public class ProductDocumentationUtil {
	private static final Logger logger = LoggerFactory.getLogger(ProductDocumentationUtil.class);
	private static final int THREE = 3;
	private static String[] FIXED_TIMEZONES = {"MIT (UTC-11)","HST (UTC-10)","AST (UTC-9)","PST (UTC-8)","PNT (UTC-7)","MST (UTC-7)","CST (UTC-6)","EST (UTC-5)","IET (UTC-5)","PRT (UTC-4)","CNT (UTC-3:30)","AGT (UTC-3)","BET (UTC-3)","CAT (UTC-1)","GMT (UTC)","ECT (UTC+1)","EET (UTC+2)","ART (UTC+2)","EAT (UTC+3)","MET (UTC+3:30)","NET (UTC+4)","PLT (UTC+5)","IST (UTC+5:30)","BST (UTC+6)","VST (UTC+7)","CTT (UTC+8)","JST (UTC+9)","ACT (UTC+9:30)","AET (UTC+10)","SST (UTC+11)","NST (UTC+12)"};

	/** nulls **/
	private static final String NULL_TEXT = "null";		

	private static final int TWENTY_FOUR = 24;
	private static final int TWELVE = 12;	
	private static final int SIXTY= 60;
	private static final String PM = "PM";
	private static final String UTC_MINUS = "UTC-";

	public static String getNowDateUTCStr()
	{
		try
		{
			Date nowDate = new Date();  //NOSONAR
			SimpleDateFormat sdf1 = new SimpleDateFormat();
			sdf1.applyPattern("yyyy-MM-dd HH:mm:ss");
			sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
			String nowStr = sdf1.format(nowDate);
			logger.info("sdf1 str:{} ",nowStr);
			//Date nowDateUTC = sdf1.parse(nowStr);return nowDateUTC;			
			return nowStr;
		}
		catch(Exception e)
		{
			logger.info("Err in nowUTC str",e);
		}
		return null;
	}
	public static Integer[] getHrsMins(String timeZone)
	{
		int utcStartInd = timeZone.indexOf("UTC");
		Integer hrMin []= new Integer[] {0,0};
		if(utcStartInd>-1)
		{
			int utcTimeHrsStart = utcStartInd + THREE;
			int utcTimeHrsEnd = timeZone.indexOf(":",utcTimeHrsStart);
			int utcTimeMinuteStart=-1,utcTimeMinuteEnd=-1;
			if(utcTimeHrsEnd == -1) {utcTimeHrsEnd = timeZone.length()-1;}
			else { utcTimeMinuteStart = utcTimeHrsEnd+1; utcTimeMinuteEnd = timeZone.length()-1;}			
			int hrs3 = Integer.parseInt(timeZone.substring(utcTimeHrsStart, utcTimeHrsEnd));			
			int min3 = utcTimeMinuteStart==-1?0:Integer.parseInt(timeZone.substring(utcTimeMinuteStart, utcTimeMinuteEnd));		
			hrMin[0]=hrs3; hrMin[1]=min3;			
		}
		return hrMin;
	}
	public static Map<String,String> listToMap(List<Map<String,Object>> dbList)
	{
		Map<String,String> countMap = new HashMap<String,String>();
		for(Map<String,Object> dbMap : dbList)
		{
			String dbKey = String.valueOf(dbMap.get("dbkey"));
			String dbValue = String.valueOf(dbMap.get("dbvalue"));			
			countMap.put(dbKey,dbValue);		
		}
		return countMap;
	}

	public static Map<String,Object> listToSTMap(List<Map<String,Object>> dbList, final Map<String,Object> stFilter)
	{
		Map<String,Object> stAllKeysMap = new HashMap<String,Object>();
		Map<String,Object> stCountMap = new HashMap<String,Object>();

		Map<String,Object> stMap = new HashMap<String,Object>();//new HashMap<String,Map<String,Map<String,String>>>();

		Set<String> distinctST = new HashSet<String>();
		Map<String,List<String>> distinctUCForST = new HashMap<String,List<String>>();
		Map<String,List<String>> distinctPSForUC = new HashMap<String,List<String>>();


		for(Map<String,Object> dbMap : dbList)
		{
			String st = String.valueOf(dbMap.get("successtrack"));
			String uc = String.valueOf(dbMap.get("usecase"));
			String ps = String.valueOf(dbMap.get("pitstop"));

			String dbValue = String.valueOf(dbMap.get("dbvalue"));	

			distinctST.add(st);
			if(!distinctUCForST.keySet().contains(st)) {distinctUCForST.put(st, new ArrayList<String>());}
			distinctUCForST.get(st).add(uc);
			if(!distinctPSForUC.keySet().contains(uc)) {distinctPSForUC.put(uc, new ArrayList<String>());}
			distinctPSForUC.get(uc).add(ps);

			if(!stMap.keySet().contains(st)) {stMap.put(st, new HashMap<String,Map<String,String>>()) ;}
			if(!((Map)stMap.get(st)).keySet().contains(uc)) {((Map)stMap.get(st)).put(uc, dbValue);}

			if(stFilter!=null)
			{
				if(!stAllKeysMap.keySet().contains(st)) { stAllKeysMap.put(st, new HashMap<String,Map<String,String>>()) ;}
				if(!((Map)stAllKeysMap.get(st)).keySet().contains(uc)) { ((Map)stAllKeysMap.get(st)).put(uc, "0");}				
			}					
		}		
		stCountMap.putAll(stMap);if(stFilter!=null) {stFilter.putAll(stAllKeysMap);}		

		logger.info("stCountMap {} , stFilter={}",stCountMap, stFilter);

		return stCountMap;
	}

	public static Set<String> orPreferences(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<String>();

		/** OR **/
		if(!filteredCards.isEmpty())
		{
			filteredCards.forEach((k,v)->{
				cardIds.addAll(v);
			});			
		}
		logger.info("OR mapped = {} {}",cardIds.size(), cardIds);	

		return cardIds;
	}

	public static Set<String> andFilters(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<String>();

		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			for(int i=0; i<keys.length; i++)
			{
				if(i==0) { cardIds.addAll(filteredCards.get(keys[i]));}
				else {cardIds.retainAll(filteredCards.get(keys[i]));}
			}
		}
		logger.info("mapped = {} ",cardIds);	

		return cardIds;
	}

	public static Set<String> andFiltersWithExcludeKey(Map<String, Set<String>> filteredCards, String excludeKey, 
			Set<String> cardIdsInp, boolean search)
	{
		Set<String> cardIds =  new HashSet<String>();

		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			int first=-1;
			for(int i=0; i<keys.length; i++)
			{
				if(keys[i].equalsIgnoreCase(excludeKey)) { continue;}
				if(first==-1) {first=i;}
				if(i==first) {cardIds.addAll(filteredCards.get(keys[i]));}
				else {cardIds.retainAll(filteredCards.get(keys[i]));}
			}
			logger.info("mapped with exclude key {} = {} cardIdsInp={}", excludeKey, cardIds, cardIdsInp);	
			if(search) {cardIds.retainAll(cardIdsInp);}
		}

		logger.info("count for exclude key {} = {}", excludeKey, cardIds);	
		return cardIds;
	}
	
	//"timeinterval":[{"timeMap":{"startTime":"12:30 AM","endTime":"2:00 AM","timeZone":"IST (UTC+5:30)"}}]}
	public static boolean isValidPrefTime(Map<String, List<UserLearningPreference>> userPreferences)
	{
		boolean isValidPrefTime = true;

		if(userPreferences!=null && userPreferences.containsKey("timeinterval"))
		{
			List<UserLearningPreference> upl = userPreferences.get("timeinterval");
			if(!CollectionUtils.isEmpty(upl)) 
			{
				UserLearningPreference up = upl.get(0);
				Map<String, String> tm = up.getTimeMap();
				if(!CollectionUtils.isEmpty(tm)) 
				{
					String st = tm.get("startTime");
					String et = tm.get("endTime");
					String tz = tm.get("timeZone");		
					if(StringUtils.isBlank(st)|| StringUtils.isBlank(et)||StringUtils.isBlank(tz))
					{
						logger.error("Missing time params: st={} et={} tz={}" ,st  , et, tz);
						return false;
					}
					SimpleDateFormat target = new SimpleDateFormat("h:mm a");  //12 hr format
					try {
						Date stDate = target.parse(st);
						Date etDate = target.parse(et);
						logger.info("dates:{}={}  {}={}", st,stDate , et,etDate);					
					} catch (ParseException e) {
						logger.error("Validation Failed for parsing time:" , e);
						isValidPrefTime = false;
					}
					if(!Arrays.asList(FIXED_TIMEZONES).contains(tz)) {
						logger.error("Invalid timezone:{}" , tz);
						isValidPrefTime = false;
					}
				}
				else
				{
					isValidPrefTime = false;
				}					
			}
			else
			{
				isValidPrefTime = false;
			}
		}

		return isValidPrefTime;		
	}	
	
	public static Map<String, Object> orderFilters(final HashMap<String, Object> filters, String contentTab)
	{
		Map<String, Object> orderedFilters = new LinkedHashMap<>();
		String [] orders = Constants.FILTER_CATEGORIES;
		if(contentTab.equals(Constants.ROLE_DB_TABLE)) {orders = Constants.FILTER_CATEGORIES_ROLE;}
		if(contentTab.equals(Constants.TOPPICKS)) {orders = Constants.FILTER_CATEGORIES_TOPPICKS;}
		
		for(int i=0;i<orders.length;i++)
		{
			String key = orders[i];
			if(filters.containsKey(key)) {orderedFilters.put(key, filters.get(key));}
		}
		return orderedFilters;
	}

	public static void cleanFilters(final HashMap<String, Object> filters)
	{	
		logger.info("All {}",filters);
		if(filters.keySet().contains(Constants.SUCCESS_TRACKS_FILTER))//do 2 more times
		{
			HashMap<String, Object> stFilters = (HashMap<String, Object>)filters.get(Constants.SUCCESS_TRACKS_FILTER);//ST
			/*
			 * stFilters.forEach((k,v) -> { HashMap<String, Object> ucFilters =
			 * (HashMap<String, Object>)v;//UC removeNulls(ucFilters); //this will remove
			 * null pts and parent uc if has only one null pt });
			 */			
			removeNulls(stFilters);  //this will remove null ucs and parent st if has only one null uc
		}
		Set<String> removeThese = removeNulls(filters);  //all top level
		logger.info("Removed {} final {}",removeThese, filters);
	}

	/* e.g. Tech null 498 */
	private static Set<String> removeNulls(final HashMap<String, Object> filters)
	{
		Set<String> removeThese = new HashSet<String>();
		filters.forEach((k,v)-> {
			Map<String, Object> subFilters  = (Map<String, Object>)v;
			if(subFilters==null) {removeThese.add(k);}//remove filter itself
			else {
			Set<String> nulls = new HashSet<>();
			Set<String>  all = subFilters.keySet();
			all.forEach(ak -> {
				if(ak==null || ak.trim().isEmpty() || ak.trim().equalsIgnoreCase(NULL_TEXT)) {
					nulls.add(ak);}
			});
			nulls.forEach(n-> subFilters.remove(n));
			if(subFilters.size()==0) {removeThese.add(k);}//remove filter itself
			}
		});

		removeThese.forEach(filter->filters.remove(filter));
		return removeThese;
	}
	

	public static List<String> getRangeLW(List<LearningItemEntity> onlyFutureLWIds, Map<String, String> ddbTI)
	{
		long requestStartTime = System.currentTimeMillis();	
		List<String> rangeCardsIds = new ArrayList<>();
		String startTime = ddbTI.get(Constants.TI_START_TIME).trim();
		String endTime = ddbTI.get(Constants.TI_END_TIME).trim();
		String timeZone = ddbTI.get(Constants.TI_TIME_ZONE).trim();
		//LOG.info("TI:{},{},{}",startTime,endTime, timeZone);

		int hrs1 = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
		int min1 = Integer.parseInt(startTime.substring(startTime.indexOf(":")+1, startTime.indexOf(" ")));
		if(startTime.contains(PM)) {hrs1=hrs1+TWELVE;}
		else if (hrs1 == TWELVE) {hrs1=0;}

		int hrs2 = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
		int min2 = Integer.parseInt(endTime.substring(endTime.indexOf(":")+1, endTime.indexOf(" ")));
		if(endTime.contains(PM)) {hrs2=hrs2+TWELVE;}
		else if (hrs2 == TWELVE) {hrs2=0;}
		//LOG.info("{} {} {} {}",hrs1,min1,hrs2,min2);

		Integer hrMin[] = ProductDocumentationUtil.getHrsMins(timeZone);
		int hrs3 = hrMin[0];
		int min3 = hrMin[1];
		if(timeZone.contains(UTC_MINUS)) {min3 = min3 * -1;}		

		for(LearningItemEntity futureCard : onlyFutureLWIds)
		{			
			Date date4 = Timestamp.valueOf(futureCard.getSortByDate());	
			int finalHrs = date4.getHours() + hrs3; 
			if(finalHrs<0) {finalHrs = finalHrs*-1 -1;} 
			if(finalHrs>=TWENTY_FOUR) {finalHrs-=TWENTY_FOUR;}
			int finalMin = date4.getMinutes() + min3; 
			if(finalMin<0) {finalMin += SIXTY; finalHrs-=1; } 
			if(finalMin>=SIXTY) { finalMin-=SIXTY;finalHrs+=1;}
			logger.info("finalHrs {} {} {} {} {} {} {} {} {} {}",futureCard.getLearning_item_id(),hrs1,min1,hrs2,min2, hrs3, min3, date4 , finalHrs, finalMin);

			boolean hrsCondition = (finalHrs>hrs1 && finalHrs<hrs2);
			boolean hrMinCondition1 = (finalHrs==hrs1 && finalMin>=min1 );
			boolean hrMinCondition2 = (finalHrs==hrs2 && finalMin <= min2 ) ;
			if( hrsCondition ||	hrMinCondition1 ||	hrMinCondition2	)
			{ rangeCardsIds.add(futureCard.getLearning_item_id());} 
		}		
		logger.info("PD-range processed in {} ", (System.currentTimeMillis() - requestStartTime));
		return rangeCardsIds; //rangeCards
	}
	
}
