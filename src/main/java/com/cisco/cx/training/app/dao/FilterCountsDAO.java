package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilterCountsDAO {

	Set<String> andFilters(Map<String, Set<String>> filteredCards);

	void setFilterCounts(Set<String> cardIdsInp, HashMap<String, HashMap<String, String>> filterCountsMap,
			Map<String, Set<String>> filteredCardsMap);

	void setFilterCounts(Set<String> cardIds, HashMap<String, HashMap<String, String>> filterCountsMap);

	Map<String, Set<String>> filterCards(Map<String, String> filter, Set<String> learningItemIdsList);

	void initializeFiltersWithCounts(List<String> filterGroups, HashMap<String, HashMap<String, String>> countFilters, Set<String> learningItemIdsList);

}
