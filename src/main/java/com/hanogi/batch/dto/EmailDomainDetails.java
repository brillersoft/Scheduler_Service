package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

/**
 * This class will contain the details for an email domain.This will help in
 * identifying the type of connector required to connect to mail server
 * 
 * @author mayank.agarwal
 *
 */
@Entity
@Table(name = "EMAIL_DOMAIN_DETAILS")
public class EmailDomainDetails extends AuditFields<String> {

	/** The id of the email domain. */
	@Id
	@Column(name = "EMAIL_DOMAIN_ID")
	private Integer emailDomainId;

	/** The domain name of the email */
	@Column(name = "EMAIL_DOMAIN_NAME")
	private String emailDomainName;

	/** Type of server deployment */
	@Column(name = "SERVER_DEPLOYMENT_TYPE")
	private String serverDeploymentType;

	/** Mail service provider. */
	@Column(name = "EMAIL_SERVICE_PROVIDER")
	private String emailServiceProvider;

	/** JSON for mail server configuration. */
	@Column(name = "EMAIL_SERVER_CONFIG")
	private String emailServerConfig;

	@Version
	private Integer versionNum;

	private String status;

	public Integer getEmailDomainId() {
		return emailDomainId;
	}

	public void setEmailDomainId(Integer emailDomainId) {
		this.emailDomainId = emailDomainId;
	}

	public String getEmailDomainName() {
		return emailDomainName;
	}

	public void setEmailDomainName(String emailDomainName) {
		this.emailDomainName = emailDomainName;
	}

	public String getServerDeploymentType() {
		return serverDeploymentType;
	}

	public void setServerDeploymentType(String serverDeploymentType) {
		this.serverDeploymentType = serverDeploymentType;
	}

	public String getEmailServiceProvider() {
		return emailServiceProvider;
	}

	public void setEmailServiceProvider(String emailServiceProvider) {
		this.emailServiceProvider = emailServiceProvider;
	}

	public String getEmailServerConfig() {
		return emailServerConfig;
	}

	public void setEmailServerConfig(String emailServerConfig) {
		this.emailServerConfig = emailServerConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailDomainId == null) ? 0 : emailDomainId.hashCode());
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
		EmailDomainDetails other = (EmailDomainDetails) obj;
		if (emailDomainId == null) {
			if (other.emailDomainId != null) {
				return false;
			}
		} else if (!emailDomainId.equals(other.emailDomainId)) {
			return false;
		}
		return true;
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
