package com.cisco.cx.training.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;

@Repository
public interface NewLearningContentRepo extends JpaRepository<NewLearningContentEntity, String>,JpaSpecificationExecutor<NewLearningContentEntity>{

	 List<NewLearningContentEntity> findAllByLearningType(String learning_type);
	 
	 Integer countByLearningTypeAndStatusNot(String learning_type, String status);
}
