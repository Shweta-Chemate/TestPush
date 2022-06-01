package com.cisco.cx.training.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLearningPreference{

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("selected")
	private boolean selected = false;
	
	@JsonProperty("timeMap")
	private Map<String,String> timeMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Map<String, String> getTimeMap() {
		return timeMap;
	}

	public void setTimeMap(Map<String, String> timeMap) {
		this.timeMap = timeMap;
	}
	
	@Override
	public String toString() {
		String toString = "Preference [name=" + name + ", selected =" + selected ;
		if(null != timeMap) {
			for(String key : timeMap.keySet()) {
				toString += " key="+key+" value="+timeMap.get(key)+",";
			}
		}
		toString += "]";
		return toString;
	}

}


