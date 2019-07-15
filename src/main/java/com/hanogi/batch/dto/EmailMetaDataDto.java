package com.hanogi.batch.dto;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "email_metadata")
public class EmailMetaDataDto extends AuditFields<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "email_metadata_id")
	private Integer emailMetadataId;

	
	@Column(name = "aggregated_tone_id")
	private Integer aggregatedToneId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "calculated_tone_id", nullable = true)
	private CalculatedTone calculatedTone;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "individual_tone_id", nullable = true)
	private IndividualTone indvidualToneId;

	private String emailDirection;

	@Column(name = "from_mail_id")
	private String fromEmailId;

	@Column(name = "batch_run_id")
	private Integer batchRunDetails;

	@Column(name = "email_processing_status_id")
	private String emailProcessingExecutionStatus;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "data_classification_id")
	private DataClassification dataClassification;

	@OneToOne
	@JoinColumn(name = "EMAIL_METADATA_ID")
	private EmailHeader emailHeader;

	private String status;

	@Version
	private Integer versionNum;

	public Integer getEmailMetadataId() {
		return emailMetadataId;
	}

	public void setEmailMetadataId(Integer emailMetadataId) {
		this.emailMetadataId = emailMetadataId;
	}


	public CalculatedTone getCalculatedTone() {
		return calculatedTone;
	}

	public void setCalculatedTone(CalculatedTone calculatedTone) {
		this.calculatedTone = calculatedTone;
	}

	

	public String getEmailDirection() {
		return emailDirection;
	}

	public void setEmailDirection(String emailDirection) {
		this.emailDirection = emailDirection;
	}

	public String getFromEmailId() {
		return fromEmailId;
	}

	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}

	public Integer getBatchRunDetails() {
		return batchRunDetails;
	}

	public void setBatchRunDetails(Integer batchRunDetails) {
		this.batchRunDetails = batchRunDetails;
	}

	public String getEmailProcessingExecutionStatus() {
		return emailProcessingExecutionStatus;
	}

	public void setEmailProcessingExecutionStatus(String emailProcessingExecutionStatus) {
		this.emailProcessingExecutionStatus = emailProcessingExecutionStatus;
	}

	public DataClassification getDataClassification() {
		return dataClassification;
	}

	public void setDataClassification(DataClassification dataClassification) {
		this.dataClassification = dataClassification;
	}

	public EmailHeader getEmailHeader() {
		return emailHeader;
	}

	public void setEmailHeader(EmailHeader emailHeader) {
		this.emailHeader = emailHeader;
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

	public Integer getAggregatedToneId() {
		return aggregatedToneId;
	}

	public void setAggregatedToneId(Integer aggregatedToneId) {
		this.aggregatedToneId = aggregatedToneId;
	}

	public IndividualTone getIndvidualToneId() {
		return indvidualToneId;
	}

	public void setIndvidualToneId(IndividualTone indvidualToneId) {
		this.indvidualToneId = indvidualToneId;
	}

}
