package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hanogi.batch.configs.audit.AuditFields;
import com.hanogi.batch.constants.ExecutionStatusEnum;

@Entity
@Table(name = "EXECUTION_STATUS")
public class ExecutionStatus extends AuditFields<String> {
	@Id
	private Integer statusId;

	@Enumerated(EnumType.STRING)
	private ExecutionStatusEnum statusName;

	@Column(name = "STATUS_DESC", nullable = true)
	private String statusDesc;

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public ExecutionStatusEnum getStatusName() {
		return statusName;
	}

	public void setStatusName(ExecutionStatusEnum statusName) {
		this.statusName = statusName;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statusId == null) ? 0 : statusId.hashCode());
		result = prime * result + ((statusName == null) ? 0 : statusName.hashCode());
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
		ExecutionStatus other = (ExecutionStatus) obj;
		if (statusId == null) {
			if (other.statusId != null)
				return false;
		} else if (!statusId.equals(other.statusId))
			return false;
		if (statusName == null) {
			if (other.statusName != null)
				return false;
		} else if (!statusName.equals(other.statusName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExecutionStatus [statusId=" + statusId + ", statusName=" + statusName + ", statusDesc=" + statusDesc
				+ "]";
	}

}
