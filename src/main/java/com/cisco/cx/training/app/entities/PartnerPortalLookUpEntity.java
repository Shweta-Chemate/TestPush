package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cxpp_lookup")
public class PartnerPortalLookUpEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7610906142767752144L;

	@Id
	@Column(name="id")
	private String rowId;
	
	@Column(name="cxpp_key")
	private String partnerPortalKey;
	
	@Column(name="cxpp_value")
	private String partnerPortalKeyValue;
	
	@Column(name="description")
	private String description;
	
	@Column(name="created_dt_time")
	private Date createdDtTime;
	
	@Column(name="updated_dt_time")
	private Date updatedDtTime;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_by")
	private String updatedBy;

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getPartnerPortalKey() {
		return partnerPortalKey;
	}

	public void setPartnerPortalKey(String partnerPortalKey) {
		this.partnerPortalKey = partnerPortalKey;
	}

	public String getPartnerPortalKeyValue() {
		return partnerPortalKeyValue;
	}

	public void setPartnerPortalKeyValue(String partnerPortalKeyValue) {
		this.partnerPortalKeyValue = partnerPortalKeyValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDtTime() {
		return createdDtTime; //NOSONAR
	}

	public void setCreatedDtTime(Date createdDtTime) {
		this.createdDtTime = createdDtTime; //NOSONAR
	}

	public Date getUpdatedDtTime() {
		return updatedDtTime; //NOSONAR
	}

	public void setUpdatedDtTime(Date updatedDtTime) {
		this.updatedDtTime = updatedDtTime; //NOSONAR
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	
}
