package com.hanogi.batch.dto.emp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.hanogi.batch.configs.audit.AuditFields;

/**
 * @author abhishek.gupta02
 *
 */

@Entity
@Table(name = "EMPLOYEE_EMAILID_MAPPING")
public class EmployeeEmailMapping extends AuditFields<String> {

	@EmbeddedId
	private EmpEmailMap empEmailMapId;

	@Version
	private Integer versionNum;

	@Column(name = "ANALYSE_TONE")
	private String analyseTone;

	private String status;

	public EmpEmailMap getEmpEmailMapId() {
		return empEmailMapId;
	}

	public void setEmpEmailMapId(EmpEmailMap empEmailMapId) {
		this.empEmailMapId = empEmailMapId;
	}

	public String getAnalyseTone() {
		return analyseTone;
	}

	public void setAnalyseTone(String analyseTone) {
		this.analyseTone = analyseTone;
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
