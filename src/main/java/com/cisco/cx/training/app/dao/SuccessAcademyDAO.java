package com.cisco.cx.training.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;

@SuppressWarnings({"squid:S1214"})
public interface SuccessAcademyDAO extends JpaRepository<SuccessAcademyLearningEntity, String>{
	
	public static final String GET_LEARNING_FILTERS = "select distinct asset_model , asset_facet from cxpp_success_academy_learnings"; //NOSONAR
	
	@Query(value=GET_LEARNING_FILTERS , nativeQuery=true)
	List<Object[]> getLearningFilters();
		
}