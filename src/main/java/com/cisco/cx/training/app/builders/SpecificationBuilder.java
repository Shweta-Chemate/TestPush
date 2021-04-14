package com.cisco.cx.training.app.builders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
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

	private <T> Specification<T> buildFilterSpecification(Map<String, List<String>> filterParams) {
		Specification<T> specification = Specification.where(null);
		for (Map.Entry<String, List<String>> filterParam : filterParams.entrySet()) {
			String key = filterParam.getKey();
			List<String> values = filterParam.getValue();
			Specification<T> innerSpecification = Specification.where(null);
			for(String value:values)
			{
				innerSpecification = innerSpecification.or(CustomSpecifications.hasValue(key,value));
			}
			specification = specification.and(innerSpecification);
		}
		return specification;
	}
	
	public <T> Specification<T> buildFilterSpecificationForViewMoreFilters(Map<String, String> filterParams) {
		Specification<T> specification = Specification.where(null);
		for (Map.Entry<String, String> filterParam : filterParams.entrySet()) {
			String key = filterParam.getKey();
			String value = filterParam.getValue();
			List<String> values = Arrays.asList(value.split(","));
			specification = specification.and(CustomSpecifications.hasValueIn(key, values));
		}
		return specification;
	}


}
