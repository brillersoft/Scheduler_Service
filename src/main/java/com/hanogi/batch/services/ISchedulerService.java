package com.hanogi.batch.services;

import com.hanogi.batch.dto.SchedulerJobInfo;

/**
 * @author Abhishek
 */

public interface ISchedulerService {

	void startAllActiveSchedulers();

	boolean scheduleNewJob(SchedulerJobInfo jobInfo);

	void updateScheduleJob(SchedulerJobInfo jobInfo);

	boolean unScheduleJob(String jobName);

	boolean deleteJob(SchedulerJobInfo jobInfo);

	boolean pauseJob(SchedulerJobInfo jobInfo);

	boolean resumeJob(SchedulerJobInfo jobInfo);

	boolean startJobNow(SchedulerJobInfo jobInfo);

	boolean createNewScheduler(SchedulerJobInfo schedulerJobInfo);
	
	void batchCompletionSuccess();

}
