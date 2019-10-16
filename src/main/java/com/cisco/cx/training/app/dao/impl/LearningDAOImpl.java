package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.*;

import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;
import com.cisco.cx.training.models.ElasticSearchResults;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LearningDAOImpl implements LearningDAO {
	private static final Logger LOG = LoggerFactory.getLogger(LearningDAOImpl.class);

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	private final String INDEX = "cxpp_training_enablement_success_academy_dev";

	@Override
	public Learning insertLearning(Learning learning) {

		try {
			learning = elasticSearchDAO.saveEntry(INDEX, learning, Learning.class);
		} catch (IOException e) {
			LOG.error("Error while insertLearning ES API", e);
		}

		return learning;
	}

	@Override
	public List<LearningModel> getLearnings() {

		List<LearningModel> learningModelES = new ArrayList<LearningModel>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		HashMap<String, List<Learning>> modelMap = new HashMap<>();
		HashMap<String, Set<String>> categoryTypes = new HashMap<String, Set<String>>();

		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<Learning> results = elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class);

			results.getDocuments().forEach(learn -> {

				if (modelMap.containsKey(learn.getUsecase())) {
					modelMap.get(learn.getUsecase()).add(learn);
					categoryTypes.get(learn.getUsecase()).add(learn.getSolution());
				} else {
					List<Learning> learningES = new ArrayList<Learning>();
					Set<String> category = new TreeSet<>();
					learningES.add(learn);
					category.add(learn.getSolution());
					modelMap.put(learn.getUsecase(), learningES);
					categoryTypes.put(learn.getUsecase(), category);
				}

			});

			for (String name : modelMap.keySet()) {
				LearningModel eLearnings = new LearningModel();
				eLearnings.setName(name);
				eLearnings.setCategoryTypes(categoryTypes.get(name));
				eLearnings.setLearning(modelMap.get(name));
				learningModelES.add(eLearnings);
			}

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		}

		return learningModelES;

	}

	@Override
	public List<LearningModel> getFilteredLearnings(String solution, String usecase) {

		HashMap<String, List<Learning>> modelMap = new HashMap<>();
		List<LearningModel> learningModelES = new ArrayList<LearningModel>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();

		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", solution);
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", usecase);

		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<Learning> results = elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class);

			results.getDocuments().forEach(learn -> {
				if (modelMap.containsKey(learn.getCategory())) {
					modelMap.get(learn.getCategory()).add(learn);
				} else {
					List<Learning> learningES = new ArrayList<Learning>();
					learningES.add(learn);
					modelMap.put(learn.getUsecase(), learningES);
				}

			});

			for (String name : modelMap.keySet()) {
				LearningModel eLearnings = new LearningModel();
				eLearnings.setName(name);
				eLearnings.setLearning(modelMap.get(name));
				learningModelES.add(eLearnings);
			}

		} catch (IOException ioe) {
			LOG.error("Error while invoking ES API", ioe);
			throw new GenericException("Error while invoking ES API");
		}

		return learningModelES;

	}

}
