package com.cisco.cx.training.models;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public class CustomSpecifications {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomSpecifications.class);

	public static <T> Specification<T> hasValue(String columnName, String withValue) {
		return (entity, cq, cb) -> cb.equal(entity.get(columnName), withValue);
	}
	
    public static <T> Specification<T> hasDateBetweenCriteria(String columnName, Timestamp startRange, Timestamp endRange) {
        return (entity, cq, cb) -> cb.between(entity.get(columnName), startRange, endRange);
    }

}
