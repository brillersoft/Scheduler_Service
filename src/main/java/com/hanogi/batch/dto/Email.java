package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "EMAILID")
public class Email extends AuditFields<String> {

	@Id
	@Column(name = "email_id")
	private String emailId;

	@Column(name = "EMAILID_DOMAIN_TYPE")
	private String emailidDomainType;

	@Column(name = "EMAILID_TYPE_ID")
	private String emailidType;

	@Column(name = "STATUS")
	private String status;

	@Version
	private Integer versionNum;

	@OneToOne
	@JoinColumn(name = "email_preference_id")
	private EmailPreferenceMap objEmailPreferences;

	@OneToOne
	@JoinColumn(name = "EMAIL_DOMAIN_ID")
	private EmailDomainDetails objEmailDomainDetails;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmailidDomainType() {
		return emailidDomainType;
	}

	public void setEmailidDomainType(String emailidDomainType) {
		this.emailidDomainType = emailidDomainType;
	}

	public String getEmailidType() {
		return emailidType;
	}

	public void setEmailidType(String emailidType) {
		this.emailidType = emailidType;
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

	public EmailDomainDetails getObjEmailDomainDetails() {
		return objEmailDomainDetails;
	}

	public void setObjEmailDomainDetails(EmailDomainDetails objEmailDomainDetails) {
		this.objEmailDomainDetails = objEmailDomainDetails;
	}

	public EmailPreferenceMap getObjEmailPreferences() {
		return objEmailPreferences;
	}

	public void setObjEmailPreferences(EmailPreferenceMap objEmailPreferences) {
		this.objEmailPreferences = objEmailPreferences;
	}

}
