package com.hanogi.batch.dto.batch;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "BATCH_RUN_TYPE")
public class BatchRunType extends AuditFields<String> {

	@Id
	private Integer batchRunTypeId;

	private String batchRunTypeName;

	private String batchRunTypeDesc;

	public Integer getBatchRunTypeId() {
		return batchRunTypeId;
	}

	public void setBatchRunTypeId(Integer batchRunTypeId) {
		this.batchRunTypeId = batchRunTypeId;
	}

	public String getBatchRunTypeName() {
		return batchRunTypeName;
	}

	public void setBatchRunTypeName(String batchRunTypeName) {
		this.batchRunTypeName = batchRunTypeName;
	}

	public String getBatchRunTypeDesc() {
		return batchRunTypeDesc;
	}

	public void setBatchRunTypeDesc(String batchRunTypeDesc) {
		this.batchRunTypeDesc = batchRunTypeDesc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchRunTypeId == null) ? 0 : batchRunTypeId.hashCode());
		result = prime * result + ((batchRunTypeName == null) ? 0 : batchRunTypeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchRunType other = (BatchRunType) obj;
		if (batchRunTypeId == null) {
			if (other.batchRunTypeId != null)
				return false;
		} else if (!batchRunTypeId.equals(other.batchRunTypeId))
			return false;
		if (batchRunTypeName == null) {
			if (other.batchRunTypeName != null)
				return false;
		} else if (!batchRunTypeName.equals(other.batchRunTypeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchRunType [batchRunTypeId=" + batchRunTypeId + ", batchRunTypeName=" + batchRunTypeName
				+ ", batchRunTypeDesc=" + batchRunTypeDesc + "]";
	}

}
