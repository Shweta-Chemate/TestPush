package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.impl.SuccessAcademyDAOImpl;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessAcademyLearningTopics;

@RunWith(SpringRunner.class)
public class SuccessAcademyDAOTest {
	@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private SuccessAcademyDAO learningDAO = new SuccessAcademyDAOImpl();

	private final String INDEX = "cxpp_success_academy_alias";
	
	@Test
	public void getLearnings() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		ElasticSearchResults<SuccessAcademyLearning> results = new ElasticSearchResults<SuccessAcademyLearning>();
		results.addDocument(successAcademy());
		when(elasticSearchDAO.query(INDEX, sourceBuilder, SuccessAcademyLearning.class)).thenReturn(results);
		learningDAO.getSuccessAcademy();
	}

	@Test(expected = GenericException.class)
	public void getLearningsESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		when(elasticSearchDAO.query(INDEX, sourceBuilder, SuccessAcademyLearning.class)).thenThrow(IOException.class);
		learningDAO.getSuccessAcademy();
	}


	private SuccessAcademyLearning successAcademy() {
		SuccessAcademyLearning learning = new SuccessAcademyLearning();
		learning.setName("Customer Success Manager");
		learning.setDescription("The Cisco Customer Success Manager Learning Map is a foundational training curriculum that allows an aspiring Customer Success Manager to understand the role as part of a high performing team");
		learning.setParentFilter("Role Based Training");
		learning.setTrainingColour("");
		learning.setUrl("");
		SuccessAcademyLearningTopics learningTopics = new SuccessAcademyLearningTopics();
		learningTopics.setName("Foundational Learning Map");
		learningTopics.setLink("https://salesconnect.cisco.com/#/mylearningmap/SC_LMS_479");
		learning.setLearning(Arrays.asList(learningTopics));
		return learning;
	}


}
