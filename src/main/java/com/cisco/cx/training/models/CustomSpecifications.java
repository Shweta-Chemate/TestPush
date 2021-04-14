package com.cisco.cx.training.models;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.cisco.cx.training.constants.Constants;

public class CustomSpecifications {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomSpecifications.class);

	public static <T> Specification<T> hasValue(String columnName, String withValue) {
		return (entity, cq, cb) -> cb.equal(entity.get(columnName), withValue);
	}
	
	public static <T> Specification<T> hasValueIn(String columnName, List<String> withValue) {
        return (entity, cq, cb) -> cb.in(entity.get(columnName)).value(withValue);
    }

	public static <T> Specification<T> hasDateBetweenCriteria(String columnName, Timestamp startRange, Timestamp endRange) {
		return (entity, cq, cb) -> cb.between(entity.get(columnName), startRange, endRange);
	}

	public static <T> Specification<T> hasPIWs(String columnName, String withValue) {
		return (piw, cq, cb) -> cb.equal(piw.get(columnName), withValue);
	}

	public static <T> Specification<T> hasGreaterThanMinimumScore(String columnName) {
		return (piw, cq, cb) -> cb.greaterThan(piw.get(columnName), Constants.MINIMUM_SCORE);
	}

	public static <T> Specification<T> searchPIWsWithCriteria(String columnName, String withValue) {
		return (piw, cq, cb) -> cb.like(cb.lower(piw.get(columnName)).as(String.class), "%" + withValue + "%");
	}

	public static <T> Specification<T> filterPIWsWithStatus(String columnName, String withValue) {
		return (piw, cq, cb) -> cb.notEqual(piw.get(columnName), withValue);
	}
	
	public static <T> Specification<T> hasSuccesstalks(String columnName, String withValue) {
		return (successtalk, cq, cb) -> cb.equal(successtalk.get(columnName), withValue);
	}
	
	public static <T> Specification<T> searchSuccesstalksWithCriteria(String columnName, String withValue) {
		return (successtalk, cq, cb) -> cb.like(cb.lower(successtalk.get(columnName)).as(String.class), "%" + withValue + "%");
	}

}
