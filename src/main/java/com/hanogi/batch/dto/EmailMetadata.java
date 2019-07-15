package com.hanogi.batch.dto;

import com.hanogi.batch.configs.audit.AuditFields;

public class EmailMetadata extends AuditFields<String> {

	private Integer emailMetadataId;

	private AggregatedTone aggregatedTone;

	private CalculatedTone calculatedTone;

	private IndividualTone indvidualToneIdindividualTone;

	private String emailDirection;

	private String fromEmailId;

	private Integer batchRunDetails;

	private String emailProcessingExecutionStatus;

	private DataClassification dataClassification;

	private EmailHeader emailHeader;

	private String status;

	private Integer versionNum;

	public Integer getEmailMetadataId() {
		return emailMetadataId;
	}

	public void setEmailMetadataId(Integer emailMetadataId) {
		this.emailMetadataId = emailMetadataId;
	}

	public AggregatedTone getAggregatedTone() {
		return aggregatedTone;
	}

	public void setAggregatedTone(AggregatedTone aggregatedTone) {
		this.aggregatedTone = aggregatedTone;
	}

	public CalculatedTone getCalculatedTone() {
		return calculatedTone;
	}

	public void setCalculatedTone(CalculatedTone calculatedTone) {
		this.calculatedTone = calculatedTone;
	}

	public IndividualTone getIndvidualToneIdindividualTone() {
		return indvidualToneIdindividualTone;
	}

	public void setIndvidualToneIdindividualTone(IndividualTone indvidualToneIdindividualTone) {
		this.indvidualToneIdindividualTone = indvidualToneIdindividualTone;
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

}
