package com.hanogi.batch.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.dto.SchedulerJobInfo;
import com.hanogi.batch.dto.batch.BatchRunDetails;
import com.hanogi.batch.repositories.SchedulerRepository;
import com.hanogi.batch.services.impl.BatchService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author abhishek.gupta02
 *
 */

@Slf4j
@DisallowConcurrentExecution
public class CronJob extends QuartzJobBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchService batchServices;

	@Autowired
	private SchedulerRepository schedulerRepo;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		logger.info("Cron Job triggered");

		try {
			SchedulerJobInfo jobInfo = schedulerRepo.findByJobNameAndJobGroup(context.getJobDetail().getKey().getName(),
					context.getJobDetail().getKey().getGroup());

			if (jobInfo != null) {
				Integer batchId = jobInfo.getBatchId();
				if (batchId != null) {
					BatchRunDetails batchRunDetails = batchServices.getBatchRunDetails(batchId);

					CacheType cacheType = (jobInfo.getCronJob() ? CacheType.ConcurrentCache
							: CacheType.HistroicalCache);
					
					if (batchRunDetails != null) {
						batchServices.runBatch(batchRunDetails,cacheType);
					} else {
						logger.error("Batch Details are missing for batchId:" + batchId);
					}

				} else {
					logger.error("Batch Id is missing in scheduler Job Info.");
				}
			} else {
				logger.error("No such scheduler found with group and name.");
			}

		} catch (Exception e) {
			logger.error("Unable to run the job dew to Error:" + e.getMessage());
		}

	}
}
