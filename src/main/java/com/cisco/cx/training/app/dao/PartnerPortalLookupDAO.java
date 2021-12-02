package com.cisco.cx.training.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.cx.training.app.entities.PartnerPortalLookUpEntity;

@SuppressWarnings({"squid:S1214"})
public interface PartnerPortalLookupDAO extends JpaRepository<PartnerPortalLookUpEntity, String>{
	
	public static final String GET_TAB_LOCATIONS = "select * from cxpp_lookup where cxpp_key like 'CXPP_UI_TAB_%'"; //NOSONAR
	
	@Query(value=GET_TAB_LOCATIONS , nativeQuery=true)
	List<PartnerPortalLookUpEntity> getTabLocations();
		
}