package com.cisco.cx.training.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cisco.cx.training.constants.Constants;

public class LearningContentUtil {

	public static HashMap<String, String> getMappings() {
		HashMap<String, String> filterGroupMappings=new HashMap<String, String>();
		filterGroupMappings.put(Constants.LANGUAGE, Constants.LANGUAGE_PRM);
		filterGroupMappings.put(Constants.LIVE_EVENTS, Constants.REGION);
		filterGroupMappings.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_PRM);
		filterGroupMappings.put(Constants.ROLE, Constants.ROLE);
		filterGroupMappings.put(Constants.MODEL, Constants.MODEL);
		filterGroupMappings.put(Constants.TECHNOLOGY, Constants.TECHNOLOGY);
		filterGroupMappings.put(Constants.SUCCESS_TRACK, Constants.SUCCESS_TRACK);
		filterGroupMappings.put(Constants.LIFECYCLE, Constants.LIFECYCLE);
		filterGroupMappings.put(Constants.FOR_YOU_FILTER, Constants.FOR_YOU_FILTER);
		return filterGroupMappings;
	}

	public static List<String> getDefaultFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add(Constants.ROLE);
		order.add(Constants.SUCCESS_TRACK);
		order.add(Constants.LIFECYCLE);
		order.add(Constants.TECHNOLOGY);
		order.add(Constants.LIVE_EVENTS);
		order.add(Constants.CONTENT_TYPE);
		order.add(Constants.LANGUAGE);
		return order;
	}

	public static List<String> getCXInsightsFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add(Constants.LIFECYCLE);
		order.add(Constants.ROLE);
		order.add(Constants.TECHNOLOGY);
		order.add(Constants.SUCCESS_TRACK);
		order.add(Constants.LIVE_EVENTS);
		order.add(Constants.FOR_YOU_FILTER);
		order.add(Constants.CONTENT_TYPE);
		order.add(Constants.LANGUAGE);
		return order;
	}

	public static List<String> getLFCFilterOrder() {
		List<String> order = new ArrayList<>();
		order.add("Accelerate"); order.add("Need");
		order.add("Evaluate"); order.add("Select");
		order.add("Align"); order.add("Purchase");
		order.add("Onboard"); order.add("Implement");
		order.add("Use");  order.add("Engage");
		order.add("Adopt"); order.add("Optmize");
		order.add("Renew"); order.add("Recommend");
		order.add("Advocate");
		return order;
	}
	

	public static Map<String, String>  filterStringtoMap(String filter){
		Map<String, String> query_map = new LinkedHashMap<String, String>();
		if (!StringUtils.isBlank(filter)) {
			filter = filter.replaceAll("%3B", ";");
			filter = filter.replaceAll("%3A", ":");
			String[] columnFilter = filter.split(";");
			for (int colFilterIndex = 0; colFilterIndex < columnFilter.length; colFilterIndex++) {
				String[] valueFilter = columnFilter[colFilterIndex].split(":");
				String fieldName = valueFilter[0];
				String fieldValue = valueFilter[1];
				query_map.put(fieldName, fieldValue);
			}
		}
		return query_map;
	}
}
