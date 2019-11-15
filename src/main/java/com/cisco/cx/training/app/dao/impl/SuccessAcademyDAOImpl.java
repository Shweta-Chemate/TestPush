package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessAcademyModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyFilterMap;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SuccessAcademyDAOImpl implements SuccessAcademyDAO {
	private static final Logger LOG = LoggerFactory.getLogger(SuccessAcademyDAOImpl.class);

	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	private final String INDEX = "cxpp_success_academy_alias";
	private final String FILTER_INDEX = "cxpp_success_academy_filters_alias";

	@Override
	public List<SuccessAcademyModel> getLearnings() {

		List<SuccessAcademyModel> learningModelES = new ArrayList<SuccessAcademyModel>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		TreeMap<String, String> orderMap = new TreeMap<>();
		HashMap<String, List<SuccessAcademyLearning>> modelMap = new HashMap<>();

		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {

			ElasticSearchResults<SuccessAcademyLearning> results = elasticSearchDAO.query(INDEX, sourceBuilder,
					SuccessAcademyLearning.class);
		//	SuccessAcademyFilter successAcademyFilter  = new SuccessAcademyFilter();

			SuccessAcademyFilter successAcademyFilter = getSuccessAcademyFilter();
			results.getDocuments().forEach(learn -> {

				for (SuccessAcademyFilterMap successAcademyFilterMap : successAcademyFilter.getFilters()) {
					orderMap.put(successAcademyFilterMap.getTabLocationOnUI(), successAcademyFilterMap.getKey());
					if (successAcademyFilterMap.getValues().contains(learn.getParentFilter())
							|| successAcademyFilterMap.getKey().contains(learn.getParentFilter())) {

						if (modelMap.containsKey(successAcademyFilterMap.getKey())) {
							modelMap.get(successAcademyFilterMap.getKey()).add(learn);

						} else {
							List<SuccessAcademyLearning> learningES = new ArrayList<SuccessAcademyLearning>();

							learningES.add(learn);
							modelMap.put(successAcademyFilterMap.getKey(), learningES);

						}
						break;
					}

				}

			});

			for (String name : orderMap.values()) {
				SuccessAcademyModel eLearnings = new SuccessAcademyModel();
				eLearnings.setName(name);
				for (SuccessAcademyFilterMap successAcademyFilterMap : successAcademyFilter.getFilters()) {

					if (successAcademyFilterMap.getKey().equals(name)) {
						eLearnings.setDisplayType(successAcademyFilterMap.getDisplayType());
						eLearnings.setShowFilters(successAcademyFilterMap.getShowFilters());
						eLearnings.setTabLocationOnUI(successAcademyFilterMap.getTabLocationOnUI());
						eLearnings.setSolutionTypes(successAcademyFilterMap.getValues());
						break;
					}

				}
				eLearnings.setLearningDetails(modelMap.get(name));
				learningModelES.add(eLearnings);
			}

		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		return learningModelES;

	}

	private SuccessAcademyFilter getSuccessAcademyFilter() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SuccessAcademyFilter successAcademyFilter = new SuccessAcademyFilter();
		ElasticSearchResults<SuccessAcademyFilter> results1;
		try {
			results1 = elasticSearchDAO.query(FILTER_INDEX, sourceBuilder, SuccessAcademyFilter.class);
            if (results1 != null) {
			ObjectMapper objectMapper = new ObjectMapper();

			String filterjson = objectMapper.writeValueAsString(results1.getDocuments().get(0));
			successAcademyFilter = objectMapper.readValue(filterjson, SuccessAcademyFilter.class);
			LOG.info("Filter JSON filter:: {}", successAcademyFilter.getFilters());
			
            }

		} catch (IOException e) {
			LOG.error(ERROR_MESSAGE, e);
			throw new GenericException(ERROR_MESSAGE);
		}
		return successAcademyFilter;

	}

}
