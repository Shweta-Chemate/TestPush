package com.cisco.cx.training.app.builders;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;


public class SpecificationBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationBuilder.class);

	public <T> Specification<T> filter(Map<String, List<String>> queryFilters) {
		Specification<T> specification = Specification
				.where(null);
		if (queryFilters != null) {
			Specification<T> filterSpecification = buildFilterSpecification(queryFilters);
			specification = specification.and(filterSpecification);
		}
		return specification;
	}
	
	public <T> Specification<T> buildFilterSpecification(Map<String, List<String>> filterParams) {
		Specification<T> specification = Specification.where(null);
		for (Entry<String, List<String>> filterParam : filterParams.entrySet()) {
			String key = filterParam.getKey();
			List<String> values = filterParam.getValue();
			if(key.equals(Constants.CONTENT_TYPE_FIELD)) {
				Specification<T> contentTypeSpecification = Specification.where(null);
				for(String contentType:values) {
					contentTypeSpecification=contentTypeSpecification.or(CustomSpecifications.findWithCriteria(key, contentType));
				}
				specification=specification.and(contentTypeSpecification);
			}
			else {
				specification = specification.and(CustomSpecifications.hasValueIn(key, values));
			}
		}
		return specification;
	}

	public <T> Specification<T> buildSearchSpecification(String search) {
		Specification<T> searchSpecification = Specification.where(null);
		if (search != null) {
			for (String searchField : getSearchFields()) {
				searchSpecification = searchSpecification
						.or(CustomSpecifications.searchItemsWithCriteria(searchField, search));
			}
			LOGGER.info("sspecifications: {} ", searchSpecification);
		}
		return searchSpecification;
	}

	public <T> Specification<T> filterById(List<String> learningItemIdsListForYou) {
		Specification<T> specification = Specification.where(null);
		if (!learningItemIdsListForYou.isEmpty()) {

			specification = specification.and(CustomSpecifications.hasValueIn(Constants.ID, learningItemIdsListForYou));
		}
		return specification;
	}

	public Set<String> getSearchFields() {
		Set<String> searchFields = new HashSet<>();
		searchFields.add(Constants.TITLE);
		searchFields.add(Constants.SPEAKERS);
		searchFields.add(Constants.DESCRIPTION);
		return searchFields;
	}

}
