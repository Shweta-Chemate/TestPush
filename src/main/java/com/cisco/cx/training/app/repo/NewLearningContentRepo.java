package com.cisco.cx.training.app.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

@Repository
public interface NewLearningContentRepo extends JpaRepository<NewLearningContentEntity, String>{

	 List<NewLearningContentEntity> findAllBySortByDateBetweenOrderBySortByDateDesc(Timestamp timeStart,Timestamp timeEnd);

}
