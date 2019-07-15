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
@Table(name = "retail_employee_csat_summary")
public class RetailEmployeeCsatSummary extends AuditFields<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_num")
	private Integer idNum;
	
	@Column(name = "total_emails")
	private Integer totalEmails;

	@Column(name = "total_interactions")
	private Integer totalInteractions;
	
	@Column(name = "negative_interactions")
	private Integer negativeInteractions;
	
	@Column(name = "csat")
	private Double csat;
	
	@Column(name = "mail_date")
	private Date mailDate;
	
	@Column(name = "employee_email")
	private String employeeEmail;
	
	@Column(name = "account_id")
	private Integer accountId;
	

	private String status;
	
	@Transient
	private Set<String> threadName = new HashSet<>();


	@Version
	private Integer versionNum;



	public Integer getTotalEmails() {
		return totalEmails;
	}


	public void setTotalEmails(Integer totalEmails) {
		this.totalEmails = totalEmails;
	}


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


	public String getEmployeeEmail() {
		return employeeEmail;
	}


	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
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


	public Set<String> getThreadName() {
		return threadName;
	}

	
	public void setThreadName(Set<String> threadName) {
		this.threadName = threadName;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}


	@Override
	public String toString() {
		return "RetailEmployeeCsatSummary [Id=" + idNum + ", totalEmails=" + totalEmails + ", totalInteractions="
				+ totalInteractions + ", negativeInteractions=" + negativeInteractions + ", csat=" + csat
				+ ", mailDate=" + mailDate + ", employeeEmail=" + employeeEmail + ", accountId=" + accountId
				+ ", status=" + status + ", threadName=" + threadName + ", versionNum=" + versionNum + "]";
	}


}
