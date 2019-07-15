package com.hanogi.batch.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

@Entity
@Table(name = "retail_account_csat_summary")
public class RetailAccountCsatSummary extends AuditFields<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_num")
	private Integer idNum;
	
	@Column(name = "total_interactions")
	private Integer totalInteractions;
	
	@Column(name = "negative_interactions")
	private Integer negativeInteractions;
	
	@Column(name = "account_id")
	private Integer accountId;
	
	@Column(name = "csat")
	private Double csat;
	
	@Column(name = "mail_date")
	private Date mailDate;
	
	@Column(name = "escalations")
	private Integer escalations;
	
	private String status;

	@Version
	private Integer versionNum;

	public Integer getTotalInteractions() {
		return totalInteractions;
	}

	public void setTotalInteractions(Integer totalInteractions) {
		this.totalInteractions = totalInteractions;
	}

	public Integer getNegativeInteractions() {
		return negativeInteractions;
	}

	public void setNegativeInteractions(Integer negativeInteractions) {
		this.negativeInteractions = negativeInteractions;
	}

	public Double getCsat() {
		return csat;
	}

	public void setCsat(Double csat) {
		this.csat = csat;
	}

	public Date getMailDate() {
		return mailDate;
	}

	public void setMailDate(Date mailDate) {
		this.mailDate = mailDate;
	}

	public Integer getEscalations() {
		return escalations;
	}

	public void setEscalations(Integer escalations) {
		this.escalations = escalations;
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
	public String toString() {
		return "RetailAccountCsatSummary [totalInteractions=" + totalInteractions + ", negativeInteractions="
				+ negativeInteractions + ", csat=" + csat + ", mailDate=" + mailDate + ", escalations=" + escalations
				+ ", status=" + status + ", versionNum=" + versionNum + "]";
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

}
