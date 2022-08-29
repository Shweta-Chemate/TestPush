package com.cisco.cx.training.app.builders;

import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilderPIW {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationBuilderPIW.class);

  public <T> Specification<T> filter(
      Map<String, String> queryFilters, String search, String region) {
    Specification<T> specification = Specification.where(null);

    // filter piws learning type
    specification =
        specification.and(CustomSpecifications.hasValue(Constants.LEARNING_TYPE, Constants.PIW));

    if (region != null) {
      specification = specification.and(CustomSpecifications.hasPIWs(Constants.REGION, region));
    }
    if (search != null) {
      try {
        search = java.net.URLDecoder.decode(search, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        LOGGER.error("Issue while decoding string: ", e);
      }
      Specification<T> searchSpecification = buildSearchSpecification(search.toLowerCase());
      specification = specification.and(searchSpecification);
    }
    if (queryFilters != null) {
      Specification<T> filterSpecification = buildFilterSpecification(queryFilters);
      specification = specification.and(filterSpecification);
    }
    return specification;
  }

  public <T> Specification<T> buildFilterSpecification(Map<String, String> filterParams) {
    Specification<T> specification = Specification.where(null);
    for (Map.Entry<String, String> filterParam : filterParams.entrySet()) {
      String key = filterParam.getKey();
      String value = filterParam.getValue();
      if (key.equalsIgnoreCase(Constants.MOST_POPULAR)) {
        specification =
            specification.and(
                CustomSpecifications.hasGreaterThanMinimumScore(Constants.SCORE_COLUMN));
      }

      if (key.equalsIgnoreCase(Constants.BOOKMARKED)
          || key.equalsIgnoreCase(Constants.REGISTERED)) {
        // Dont do anything, this is handled in training enablement microservice
      } else {
        specification = specification.and(CustomSpecifications.hasPIWs(key, value));
      }
    }

    return specification;
  }

  public <T> Specification<T> buildSearchSpecification(String search) {
    Specification<T> searchSpecification = Specification.where(null);
    if (search != null) {
      for (String searchField : getSearchFields()) {
        searchSpecification =
            searchSpecification.or(
                CustomSpecifications.searchPIWsWithCriteria(searchField, search));
      }
      LOGGER.info("spspecifications: {} ", searchSpecification);
    }
    return searchSpecification;
  }

  public Set<String> getSearchFields() {
    Set<String> searchFields = new HashSet<>();
    searchFields.add(Constants.TITLE);
    searchFields.add(Constants.SPEAKERS);
    return searchFields;
  }
}
