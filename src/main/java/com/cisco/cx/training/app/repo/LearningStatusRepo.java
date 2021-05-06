package com.cisco.cx.training.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.LearningStatusEntityPK;

@Repository
public interface LearningStatusRepo extends JpaRepository<LearningStatusEntity, LearningStatusEntityPK> {

	LearningStatusEntity findByLearningItemIdAndUserIdAndPuid(String learningItemId, String userId, String puid);
	
	List<LearningStatusEntity> findByUserIdAndPuid(String userId, String puid);
	
}
