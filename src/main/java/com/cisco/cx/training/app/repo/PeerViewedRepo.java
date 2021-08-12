package com.cisco.cx.training.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.entities.PeerViewedEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntityPK;

@Repository
public interface PeerViewedRepo extends JpaRepository<PeerViewedEntity, PeerViewedEntityPK> {
	List<PeerViewedEntity> findByRoleName(String userRole);
	
}
