package com.hanogi.batch.dto.batch;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;
import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.dto.ExecutionStatus;

@Entity
@Table(name = "BATCH_RUN_DETAILS")
public class BatchRunDetails extends AuditFields<String> {

	@Version
	private Integer versionNum;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BATCH_RUN_ID")
	private Integer batchRunId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FROM_DATE")
	private Date fromDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TO_DATE")
	private Date toDate;

	@Column(name = "BATCH_RUN_DATE", nullable = false)
	private String batchRunDate;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "BATCH_STATUS_ID", nullable = false)
	private ExecutionStatus batchExecutionStatus;

	@OneToOne
	@JoinColumn(name = "BATCH_RUN_TYPE_ID")
	private BatchRunType batchRunType;

	@Column(name = "BATCH_STATUS_DETAILS", columnDefinition = "TEXT")
	private String batchStatusDetails;

	private String batchJobId;

	private String status;

	public Integer getBatchRunId() {
		return batchRunId;
	}

	public void setBatchRunId(Integer batchRunId) {
		this.batchRunId = batchRunId;
	}

	public String getBatchRunDate() {
		return batchRunDate;
	}

	public void setBatchRunDate(String batchRunDate) {
		this.batchRunDate = batchRunDate;
	}

	public ExecutionStatus getBatchExecutionStatus() {
		return batchExecutionStatus;
	}

	public void setBatchExecutionStatus(ExecutionStatus batchExecutionStatus) {
		this.batchExecutionStatus = batchExecutionStatus;
	}

	public BatchRunType getBatchRunType() {
		return batchRunType;
	}

	public void setBatchRunType(BatchRunType batchRunType) {
		this.batchRunType = batchRunType;
	}

	public String getBatchStatusDetails() {
		return batchStatusDetails;
	}

	public void setBatchStatusDetails(String batchStatusDetails) {
		this.batchStatusDetails = batchStatusDetails;
	}

	public String getBatchJobId() {
		return batchJobId;
	}

	public void setBatchJobId(String batchJobId) {
		this.batchJobId = batchJobId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(Integer versionNum) {
		this.versionNum = versionNum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
		result = prime * result + ((batchRunDate == null) ? 0 : batchRunDate.hashCode());
		result = prime * result + ((batchRunId == null) ? 0 : batchRunId.hashCode());
		result = prime * result + ((batchRunType == null) ? 0 : batchRunType.hashCode());
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
		BatchRunDetails other = (BatchRunDetails) obj;
		if (batchJobId == null) {
			if (other.batchJobId != null)
				return false;
		} else if (!batchJobId.equals(other.batchJobId))
			return false;
		if (batchRunDate == null) {
			if (other.batchRunDate != null)
				return false;
		} else if (!batchRunDate.equals(other.batchRunDate))
			return false;
		if (batchRunId == null) {
			if (other.batchRunId != null)
				return false;
		} else if (!batchRunId.equals(other.batchRunId))
			return false;
		if (batchRunType == null) {
			if (other.batchRunType != null)
				return false;
		} else if (!batchRunType.equals(other.batchRunType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchRunDetails [batchRunId=" + batchRunId + ", fromDate=" + getFromDate() + ", toDate=" + getToDate()
				+ ", batchRunDate=" + batchRunDate + ", batchExecutionStatus=" + batchExecutionStatus
				+ ", batchRunType=" + batchRunType + ", batchStatusDetails=" + batchStatusDetails + ", batchJobId="
				+ batchJobId + ", status=" + status + ", versionNum=" + versionNum + "]";
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

}
