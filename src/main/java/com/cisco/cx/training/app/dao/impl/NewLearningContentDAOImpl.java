package com.cisco.cx.training.app.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.builders.SpecificationBuilder;
import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.builders.SpecificationBuilderSuccessTalk;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String,List<String>> filterParams) {
		Specification<NewLearningContentEntity> specification = addTimeRangeSpecification();
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(Constants.DESC),Constants.SORTDATE));
	}

	private Specification<NewLearningContentEntity> addTimeRangeSpecification() {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		LocalDateTime localDateTimeStart = LocalDateTime.now().minusMonths(3);
		ZonedDateTime zdtStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());
		LocalDateTime localDateTimeEnd = LocalDateTime.now();
		ZonedDateTime zdtEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());
		specification= specification.and(CustomSpecifications.hasDateBetweenCriteria(Constants.SORTDATE,new Timestamp(zdtStart.toInstant().toEpochMilli()),new Timestamp(zdtEnd.toInstant().toEpochMilli())));
		return specification;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilderSuccessTalk().filter(filterParams, search));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(sortType),sortField));
	}

	@Override
	public List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType,
			Map<String, String> filterParams, String search) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilderPIW().filter(filterParams, search, region));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(sortType),sortField));
	}

	@Override
	public Integer getSuccessTalkCount() {
		return learningContentRepo.countByLearningTypeAndStatusNot(Constants.SUCCESSTALK, SuccessTalk.SuccessTalkStatusEnum.CANCELLED.toString());
	}
}
