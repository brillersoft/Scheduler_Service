package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "Calculated_Tone")
public class CalculatedTone extends AuditFields<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calculated_Tone_Id")
	private Integer calculatedToneId;

	@Column(name = "calculated_tone", columnDefinition = "TEXT")
	private String calculatedTone;

	private String status;
	
	@Column(name = "individual_tone")
	private String individualTone;
	
	@Column(name = "individual_tone_score")
	private Double individualToneScore;
	
	@Column(name = "mail_index")
	private Double  mailIndex;
	
	@Column(name = "mail_direction")
	private String mailDirection;
	
	@Column(name = "message_id")
	private String messageId;
	
	@Column(name = "mail_thread_name")
	private String mailThreadName;


	@Version
	private Integer versionNum;

	public Integer getCalculatedToneId() {
		return calculatedToneId;
	}

	public void setCalculatedToneId(Integer calculatedToneId) {
		this.calculatedToneId = calculatedToneId;
	}

	public String getCalculatedTone() {
		return calculatedTone;
	}

	public void setCalculatedTone(String calculatedTone) {
		this.calculatedTone = calculatedTone;
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
		result = prime * result + ((calculatedTone == null) ? 0 : calculatedTone.hashCode());
		result = prime * result + ((calculatedToneId == null) ? 0 : calculatedToneId.hashCode());
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
		CalculatedTone other = (CalculatedTone) obj;
		if (calculatedTone == null) {
			if (other.calculatedTone != null)
				return false;
		} else if (!calculatedTone.equals(other.calculatedTone))
			return false;
		if (calculatedToneId == null) {
			if (other.calculatedToneId != null)
				return false;
		} else if (!calculatedToneId.equals(other.calculatedToneId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CalculatedTone [calculatedToneId=" + calculatedToneId + ", calculatedTone=" + calculatedTone
				+ ", status=" + status + ", versionNum=" + versionNum + "]";
	}

	public String getIndividualTone() {
		return individualTone;
	}

	public void setIndividualTone(String individualTone) {
		this.individualTone = individualTone;
	}

	public Double getMailIndex() {
		return mailIndex;
	}

	public void setMailIndex(Double  mailIndex) {
		this.mailIndex = mailIndex;
	}

	public String getMailDirection() {
		return mailDirection;
	}

	public void setMailDirection(String mailDirection) {
		this.mailDirection = mailDirection;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMailThreadName() {
		return mailThreadName;
	}

	public void setMailThreadName(String mailThreadName) {
		this.mailThreadName = mailThreadName;
	}

	public Double getIndividualToneScore() {
		return individualToneScore;
	}

	public void setIndividualToneScore(Double individualToneScore) {
		this.individualToneScore = individualToneScore;
	}

}
