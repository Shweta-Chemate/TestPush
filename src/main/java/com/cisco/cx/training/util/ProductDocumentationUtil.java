package com.cisco.cx.training.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDocumentationUtil {
	private static final Logger logger = LoggerFactory.getLogger(ProductDocumentationUtil.class);
	private static final int THREE = 3;

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

}
