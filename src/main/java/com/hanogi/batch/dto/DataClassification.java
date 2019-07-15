package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "Data_Classification")
public class DataClassification {

	@Id
	@Column(name = "classification_category_id")
	private Integer classificationCategoryId;

	@Column(name = "classification_category_name")
	private String classificationCategoryName;

	@Column(name = "classification_category_desc")
	private String classificationCategoryDesc;

	private String status;

	@Version
	private Integer versionNum;

	public Integer getClassificationCategoryId() {
		return classificationCategoryId;
	}

	public void setClassificationCategoryId(Integer classificationCategoryId) {
		this.classificationCategoryId = classificationCategoryId;
	}

	public String getClassificationCategoryName() {
		return classificationCategoryName;
	}

	public void setClassificationCategoryName(String classificationCategoryName) {
		this.classificationCategoryName = classificationCategoryName;
	}

	public String getClassificationCategoryDesc() {
		return classificationCategoryDesc;
	}

	public void setClassificationCategoryDesc(String classificationCategoryDesc) {
		this.classificationCategoryDesc = classificationCategoryDesc;
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
		result = prime * result + ((classificationCategoryId == null) ? 0 : classificationCategoryId.hashCode());
		return result;
	}

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
		DataClassification other = (DataClassification) obj;
		if (classificationCategoryId == null) {
			if (other.classificationCategoryId != null) {
				return false;
			}
		} else if (!classificationCategoryId.equals(other.classificationCategoryId)) {
			return false;
		} else if (!classificationCategoryName.equals(other.classificationCategoryName)) {
			return false;
		}

		return true;
	}

}
