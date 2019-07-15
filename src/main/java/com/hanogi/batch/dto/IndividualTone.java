package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "Individual_Tone")
public class IndividualTone extends AuditFields<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "individual_tone_id")
	private Integer individualToneId;

	@Column(name = "individual_tone", columnDefinition = "TEXT")
	private String individualTone;

	private String status;

	private Integer versionNum;
	
	@Column(name = "mail_index")
	private Double  mailIndex;
	
	@Column(name = "mail_direction")
	private String mailDirection;
	
	@Column(name = "message_id")
	private String messageId;
	
	@Column(name = "mail_thread_name")
	private String mailThreadName;

	public Integer getIndividualToneId() {
		return individualToneId;
	}

	public void setIndividualToneId(Integer individualToneId) {
		this.individualToneId = individualToneId;
	}

	public String getIndividualTone() {
		return individualTone;
	}

	public void setIndividualTone(String individualTone) {
		this.individualTone = individualTone;
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
		result = prime * result + ((individualTone == null) ? 0 : individualTone.hashCode());
		result = prime * result + ((individualToneId == null) ? 0 : individualToneId.hashCode());
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
		IndividualTone other = (IndividualTone) obj;
		if (individualTone == null) {
			if (other.individualTone != null)
				return false;
		} else if (!individualTone.equals(other.individualTone))
			return false;
		if (individualToneId == null) {
			if (other.individualToneId != null)
				return false;
		} else if (!individualToneId.equals(other.individualToneId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndividualTone [individualToneId=" + individualToneId + ", individualTone=" + individualTone
				+ ", status=" + status + ", versionNum=" + versionNum + "]";
	}

	public Double getMailIndex() {
		return mailIndex;
	}

	public void setMailIndex(Double mailIndex) {
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

}
