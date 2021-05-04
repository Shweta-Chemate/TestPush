package com.cisco.cx.training.app.builders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;


public class SpecificationBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationBuilder.class);


	public <T> Specification<T> filter(Map<String, String> queryFilters) {
		Specification<T> specification = Specification
				.where(null);
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
			// for differentating between OR and AND condition for successacademy filters
			if(key.contains("assetFacet"))
				key=StringUtils.chop(key);
			if(key.equals(Constants.CONTENT_TYPE_FIELD))
				specification=specification.and(CustomSpecifications.notEqual(Constants.LEARNING_TYPE, Constants.SUCCESS_ACADEMY));
			List<String> values = Arrays.asList(value.split(","));
			specification = specification.and(CustomSpecifications.hasValueIn(key, values));
		}
		return specification;
	}


}
