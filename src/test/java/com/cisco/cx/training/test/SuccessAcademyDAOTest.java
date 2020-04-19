package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyFilterMap;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessAcademyLearningTopics;


@RunWith(SpringRunner.class)
public class SuccessAcademyDAOTest {
	/*@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private SuccessAcademyDAO learningDAO = new SuccessAcademyDAOImpl();
	
	@Test
	public void getLearnings() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.size(10000);
		ElasticSearchResults<SuccessAcademyLearning> results = new ElasticSearchResults<SuccessAcademyLearning>();
		results.addDocument(successAcademy());
		results.addDocument(successAcademy());
		ElasticSearchResults<SuccessAcademyFilter> filterResults = new ElasticSearchResults<SuccessAcademyFilter>();
		filterResults.addDocument(successAcademyFilter());
		when(elasticSearchDAO.query(config.getSuccessAcademyFilterIndex(), sourceBuilder, SuccessAcademyFilter.class)).thenReturn(filterResults);	
		when(elasticSearchDAO.query(config.getSuccessAcademyIndex(), sourceBuilder, SuccessAcademyLearning.class)).thenReturn(results);	
		learningDAO.getSuccessAcademy();
	}

	@Test(expected = GenericException.class)
	public void getLearningsESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();		
		sourceBuilder.size(10000);
		when(elasticSearchDAO.query(config.getSuccessAcademyIndex(), sourceBuilder, SuccessAcademyLearning.class)).thenThrow(IOException.class);
		learningDAO.getSuccessAcademy();
	}
	
	@Test
	public void getSuccessAcademyFilter() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.size(10000);
		ElasticSearchResults<SuccessAcademyFilter> results = new ElasticSearchResults<SuccessAcademyFilter>();
		
		SuccessAcademyFilter academyFilter = new SuccessAcademyFilter();
		academyFilter.setDocId("id");
		List<SuccessAcademyFilterMap> filters = new ArrayList<SuccessAcademyFilterMap>();
		SuccessAcademyFilterMap academyFilterMap = new SuccessAcademyFilterMap();
		filters.add(academyFilterMap);
		academyFilter.setFilters(filters);
		results.addDocument(academyFilter);
		
		when(elasticSearchDAO.query(config.getSuccessAcademyFilterIndex(), sourceBuilder, SuccessAcademyFilter.class)).thenReturn(results);
		learningDAO.getSuccessAcademyFilter();
	}
	
	@Test(expected = GenericException.class)
	public void getSuccessAcademyFilterError() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.size(10000);
		ElasticSearchResults<SuccessAcademyFilter> results = new ElasticSearchResults<SuccessAcademyFilter>();
		
		SuccessAcademyFilter academyFilter = new SuccessAcademyFilter();
		academyFilter.setDocId("id");
		List<SuccessAcademyFilterMap> filters = new ArrayList<SuccessAcademyFilterMap>();
		SuccessAcademyFilterMap academyFilterMap = new SuccessAcademyFilterMap();
		filters.add(academyFilterMap);
		academyFilter.setFilters(filters);
		results.addDocument(academyFilter);
		
		when(elasticSearchDAO.query(config.getSuccessAcademyFilterIndex(), sourceBuilder, SuccessAcademyFilter.class)).thenThrow(IOException.class);
		learningDAO.getSuccessAcademyFilter();
	}


	private SuccessAcademyLearning successAcademy() {
		SuccessAcademyLearning learning = new SuccessAcademyLearning();
		learning.setName("Customer Success Manager");
		learning.setDescription("The Cisco Customer Success Manager Learning Map is a foundational training curriculum that allows an aspiring Customer Success Manager to understand the role as part of a high performing team");
		learning.setParentFilter("Role Based Training");
		learning.setTrainingColour("");
		learning.setUrl("");
		learning.setDocId("");
		SuccessAcademyLearningTopics learningTopics = new SuccessAcademyLearningTopics();
		learningTopics.setName("Foundational Learning Map");
		learningTopics.setLink("https://salesconnect.cisco.com/#/mylearningmap/SC_LMS_479");
		learningTopics.setDescription("Foundational Learning Map");
		learning.setLearning(Arrays.asList(learningTopics));
		return learning;
	}
	
	private SuccessAcademyFilter successAcademyFilter() {
		SuccessAcademyFilter academyFilter = new SuccessAcademyFilter();
		academyFilter.setDocId("id");
		List<SuccessAcademyFilterMap> filters = new ArrayList<SuccessAcademyFilterMap>();
		SuccessAcademyFilterMap academyFilterMap = new SuccessAcademyFilterMap();
		academyFilterMap.setKey("Role Based Training");
		academyFilterMap.setShowFilters("false");
		academyFilterMap.setTabLocationOnUI("1.0");
		academyFilterMap.setDisplayType("card");
		academyFilterMap.setValues(new HashSet<>());
		filters.add(academyFilterMap);
		SuccessAcademyFilterMap academyFilterMap1 = new SuccessAcademyFilterMap();
		academyFilterMap1.setKey("Lifecycle Partner Model");
		academyFilterMap1.setShowFilters("false");
		academyFilterMap1.setTabLocationOnUI("2.0");
		academyFilterMap1.setDisplayType("accordion");	
		academyFilterMap1.setValues(new HashSet<>(Arrays.asList("Monetize", "Organize","Operate")));
		filters.add(academyFilterMap1);
		academyFilter.setFilters(filters);
		return academyFilter;
		
	}
*/

}
