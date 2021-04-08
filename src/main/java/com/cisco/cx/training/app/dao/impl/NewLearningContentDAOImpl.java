package com.cisco.cx.training.app.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{
	
	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent() {
		LocalDateTime localDateTimeStart = LocalDateTime.now().minusMonths(3);
		ZonedDateTime zdtStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());

		LocalDateTime localDateTimeEnd = LocalDateTime.now();
		ZonedDateTime zdtEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());

		return learningContentRepo.findAllByDateBetweenOrderByDateDesc(new Timestamp(zdtStart.toInstant().toEpochMilli()), new Timestamp(zdtEnd.toInstant().toEpochMilli()));
	}

}
