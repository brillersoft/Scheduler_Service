package com.hanogi.batch.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "EMAIL_HEADER")
public class EmailHeader implements Comparable<EmailHeader>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMAIL_HEADER_ID")
	private Integer emailHeaderId;

	@Column(name = "EMAIL_METADATA_ID")
	private Integer emailMetaDataId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EMAIL_DATE")
	private Date emailDate;

	@Column(name = "SUBJECT")
	private String subject;

	private String importance;

	@Column(name = "MESSAGE_ID", nullable = true)
	public String messageId;

	@Column(name = "IN_REPLY_TO")
	private String inReplyTo;

	@Column(name = "SENDER_IP", nullable = true)
	private String senderIp;

	@Column(name = "content_language", nullable = true)
	private String contentLanguage;

	private String ccEmailId;

	private String toEmailId;
	
	@Column(name = "mail_thread_name")
	public String mailThreadName;
	
	@Column(name = "mail_thread_index")
	private Double mailThreadIndex;

	@Column(name = "reference_message_id", nullable = true)
	private String referenceMessageId;

	private String status;

	private Integer versionNum;
	
	@Column(name = "from_mail_id")
	private String fromEmailId;
	
	@Column(name = "email_direction")
	private String emailDirection;

	@Column(name = "email_text")
	private String emailText;


	public Integer getEmailMetadataId() {
		return emailHeaderId;
	}

	public void setEmailMetadataId(Integer emailMetadataId) {
		this.emailHeaderId = emailMetadataId;
	}

	public Date getEmailDate() {
		return emailDate;
	}

	public void setEmailDate(Date emailDate) {
		this.emailDate = emailDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getImportance() {
		return importance;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public String getContentLanguage() {
		return contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	public String getCcEmailId() {
		return ccEmailId;
	}

	public void setCcEmailId(String ccEmailId) {
		this.ccEmailId = ccEmailId;
	}

	public String getToEmailId() {
		return toEmailId;
	}

	public void setToEmailId(String toEmailId) {
		this.toEmailId = toEmailId;
	}

	public String getReferenceMessageId() {
		return referenceMessageId;
	}

	public void setReferenceMessageId(String referenceMessageId) {
		this.referenceMessageId = referenceMessageId;
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
	
	public Integer getEmailMetaDataId() {
		return emailMetaDataId;
	}

	public void setEmailMetaDataId(Integer emailMetaDataId) {
		this.emailMetaDataId = emailMetaDataId;
	}
	
	public Double getMailThreadIndex() {
		return mailThreadIndex;
	}

	public void setMailThreadIndex(Double mailThreadIndex) {
		this.mailThreadIndex = mailThreadIndex;
	}

	public String getMailThreadName() {
		return mailThreadName;
	}

	public void setMailThreadName(String mailThreadName) {
		this.mailThreadName = mailThreadName;
	}

	public String getFromEmailId() {
		return fromEmailId;
	}

	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}

	
	@Override
	public int compareTo(EmailHeader m) {
		
		return this.getEmailDate().compareTo(m.getEmailDate());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailHeaderId == null) ? 0 : emailHeaderId.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EmailHeader other = (EmailHeader) obj;
		if (null == emailHeaderId) {
			if (other.emailHeaderId != null) {
				return false;
			}
		} else if (!messageId.equals(other.messageId)) {
			return false;
		} else if (!subject.equals(other.subject)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "EmailHeader [emailMetadataId=" + emailHeaderId + ", emailDate=" + emailDate + ", subject=" + subject
				+ ", importance=" + importance + ", messageId=" + messageId + ", inReplyTo=" + inReplyTo + ", senderIp="
				+ senderIp + ", contentLanguage=" + contentLanguage + ", ccEmailId=" + ccEmailId + ", toEmailId="
				+ toEmailId + ", referenceMessageId=" + referenceMessageId + ", status=" + status + ", versionNum="
				+ versionNum + "]";
	}

	public String getEmailDirection() {
		return emailDirection;
	}

	public void setEmailDirection(String emailDirection) {
		this.emailDirection = emailDirection;
	}

	public String getEmailText() {
		return emailText;
	}

	public void setEmailText(String emailText) {
		this.emailText = emailText;
	}
	
}
