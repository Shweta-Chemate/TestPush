package com.cisco.cx.training.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;

@Repository
public interface NewLearningContentRepo extends JpaRepository<NewLearningContentEntity, String>,JpaSpecificationExecutor<NewLearningContentEntity>{

}
