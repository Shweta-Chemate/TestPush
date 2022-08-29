package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilterCountsDAO {

  Set<String> andFilters(Map<String, Set<String>> filteredCards);

  void setFilterCounts(
      Set<String> cardIdsInp,
      HashMap<String, Object> filterCountsMap,
      Map<String, Set<String>> filteredCardsMap,
      String userId,
      String hcaasStatus);

  void setFilterCounts(
      Set<String> cardIds,
      HashMap<String, Object> filterCountsMap,
      String filterGroup,
      String userId,
      String hcaasStatus);

  Map<String, Set<String>> filterCards(
      Map<String, Object> filtersSelected,
      Set<String> learningItemIdsList,
      String userId,
      String hcaasStatus);

  void initializeFiltersWithCounts(
      List<String> filterGroups,
      HashMap<String, Object> filters,
      HashMap<String, Object> countFilters,
      Set<String> learningItemIdsList,
      String userId,
      String hcaasStatus);
}
