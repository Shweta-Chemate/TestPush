package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;

@Repository
public class LearningDAOImpl implements LearningDAO {
	private static final Logger LOG = LoggerFactory.getLogger(LearningDAOImpl.class);
	
	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	private final String INDEX = "cxpp_success_academy_alias";

	@Override
	public Learning insertLearning(Learning learning) {

		try {
			learning = elasticSearchDAO.saveEntry(INDEX, learning, Learning.class);
		} catch (IOException e) {
			LOG.error(ERROR_MESSAGE, e);
			throw new GenericException(ERROR_MESSAGE);
		}

		return learning;
	}

	@Override
	public List<LearningModel> getLearnings() {

		List<LearningModel> learningModelES = new ArrayList<LearningModel>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		HashMap<String, List<Learning>> modelMap = new HashMap<>();
		HashMap<String, Set<String>> solutionTypes = new HashMap<String, Set<String>>();

		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<Learning> results = elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class);

			results.getDocuments().forEach(learn -> {

				if (modelMap.containsKey(learn.getUsecase())) {
					modelMap.get(learn.getUsecase()).add(learn);
					solutionTypes.get(learn.getUsecase()).add(learn.getSolution());
				} else {
					List<Learning> learningES = new ArrayList<Learning>();
					Set<String> solution = new TreeSet<>();
					learningES.add(learn);
					solution.add(learn.getSolution());
					modelMap.put(learn.getUsecase(), learningES);
					solutionTypes.put(learn.getUsecase(), solution);
				}
				String category = learn.getCategory() != null ? learn.getCategory() : "LEARNING MAP";
				learn.setCategory(category);
				String img = learn.getImg() != null ? learn.getImg()
						: "https://www.cisco.com/web/fw/tools/ssue/cp/lifecycle/acc/images/acc_access-overview-demo.png";
				learn.setImg(img);

			});

			for (String name : modelMap.keySet()) {
				LearningModel eLearnings = new LearningModel();
				eLearnings.setName(name);
				eLearnings.setSolutionTypes(solutionTypes.get(name));
				eLearnings.setLearning(modelMap.get(name));
				learningModelES.add(eLearnings);
			}

		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
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
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		return learningModelES;

	}

}
