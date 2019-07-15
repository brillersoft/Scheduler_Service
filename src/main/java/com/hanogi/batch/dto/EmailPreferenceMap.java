package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "Email_preferences")
public class EmailPreferenceMap extends AuditFields<String> {

	@Id
	private Integer emailPreferenceId;

	@Column(columnDefinition = "TEXT", name = "Email_preferences")
	private String emailPreferencesJson;

	@Version
	private Integer versionNum;

	private String status;

	public Integer getEmailPreferenceId() {
		return emailPreferenceId;
	}

	public void setEmailPreferenceId(Integer emailPreferenceId) {
		this.emailPreferenceId = emailPreferenceId;
	}

	public String getEmailPreferencesJson() {
		return emailPreferencesJson;
	}

	public void setEmailPreferencesJson(String emailPreferencesJson) {
		this.emailPreferencesJson = emailPreferencesJson;
	}

	public Integer getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(Integer versionNum) {
		this.versionNum = versionNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
