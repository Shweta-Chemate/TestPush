package com.cisco.cx.training.app.builders;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;


public class SpecificationBuilderSuccessTalk {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationBuilderSuccessTalk.class);

	public <T> Specification<T> filter(Map<String, String> queryFilters, String search) {
		Specification<T> specification = Specification.where(null);

		//filter successtalk learning type
		specification= specification.and(CustomSpecifications.hasValue(Constants.LEARNING_TYPE, Constants.SUCCESSTALK));

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
			specification = specification.and(CustomSpecifications.hasSuccesstalks(key, value));
		}

		return specification;
	}

	public <T> Specification<T> buildSearchSpecification(String search) {
		Specification<T> searchSpecification = Specification.where(null);
		if (search != null) {
			for (String searchField : getSearchFields()) {
				LOGGER.info("searchField : {}  searchValue : {}",searchField,search);
				searchSpecification = searchSpecification
						.or(CustomSpecifications.searchSuccesstalksWithCriteria(searchField, search));
			}
			LOGGER.info("stspecifications: {} ", searchSpecification);
		}
		return searchSpecification;
	}

	public Set<String> getSearchFields() {
		Set<String> searchFields = new HashSet<>();
		searchFields.add(Constants.TITLE);
		return searchFields;
	}

}
