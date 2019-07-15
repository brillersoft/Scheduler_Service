package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.emp.EmpEmailMap;
import com.hanogi.batch.dto.emp.EmployeeEmailMapping;

@Repository
public interface EmployeeEmailMappingRepositry extends CrudRepository<EmployeeEmailMapping, EmpEmailMap> {
	
	public List<EmployeeEmailMapping> findByAnalyseTone(String analyseTone);
}
