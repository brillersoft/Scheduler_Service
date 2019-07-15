package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.SchedulerJobInfo;

/**
 * @author Abhishek
 */

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, Long> {

	List<SchedulerJobInfo> findByIsActive(Boolean isActive);

	SchedulerJobInfo findByJobNameAndJobGroup(String jobName, String jobGroup);

}
