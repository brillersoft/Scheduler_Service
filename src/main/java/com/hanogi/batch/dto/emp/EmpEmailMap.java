package com.hanogi.batch.dto.emp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * @author abhishek.gupta02
 *
 */

@Embeddable
public class EmpEmailMap implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private String emailId;

	@NotNull
	@Column(name = "EMPLOYEE_ID")
	private String employeeId;

	public EmpEmailMap() {
	}

	public EmpEmailMap(String emailId, String employeeId) {
		this.setEmailId(emailId);
		this.setEmployeeId(employeeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		EmpEmailMap that = (EmpEmailMap) obj;

		if (!emailId.equals(that.emailId))
			return false;
		return employeeId.equals(that.employeeId);
	}

	@Override
	public int hashCode() {
		int result = emailId.hashCode();
		result = 31 * result + employeeId.hashCode();
		return result;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
}
