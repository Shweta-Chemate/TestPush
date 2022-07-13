package com.cisco.cx.training.app.repo;

import com.cisco.cx.training.app.entities.PeerViewedEntity;
import com.cisco.cx.training.app.entities.PeerViewedEntityPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerViewedRepo extends JpaRepository<PeerViewedEntity, PeerViewedEntityPK> {
  List<PeerViewedEntity> findByRoleName(String userRole);
}
