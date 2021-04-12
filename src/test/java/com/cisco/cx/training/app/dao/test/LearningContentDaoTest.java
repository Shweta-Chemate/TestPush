package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.impl.NewLearningContentDAOImpl;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;


@RunWith(SpringRunner.class)
public class LearningContentDaoTest {

	@Mock
	private NewLearningContentRepo learningContentRepo;
	
	@InjectMocks
	private NewLearningContentDAO learningContentDAO=new NewLearningContentDAOImpl();
	
	@Test
	public void testListPIWs() {
		List<NewLearningContentEntity> piwList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,"testSearch","testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField"))).thenReturn(piwList);
		learningContentDAO.listPIWs("testRegion", "testField", "asc", filter, "testSearch");
	}
	
	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> successtalkList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,"testSearch","testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField"))).thenReturn(successtalkList);
		learningContentDAO.fetchSuccesstalks("testField", "asc", filter, "testSearch");
	}
	
}

