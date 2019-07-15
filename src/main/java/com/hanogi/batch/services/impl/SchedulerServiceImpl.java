package com.hanogi.batch.services.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.transaction.Transactional;

import org.quartz.CronExpression;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.hanogi.batch.dto.SchedulerJobInfo;
import com.hanogi.batch.quartz.components.JobScheduleCreator;
import com.hanogi.batch.repositories.SchedulerRepository;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.services.ISchedulerService;
import com.hanogi.batch.utils.bo.EmailMessageData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class SchedulerServiceImpl implements ISchedulerService {

	private final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	private final String CRON_CLASS_PATH = "com.hanogi.batch.jobs.CronJob";

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private SchedulerRepository schedulerRepository;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JobScheduleCreator scheduleCreator;

	@Autowired
	private ICacheService cacheService;

	@Autowired
	@Qualifier("emailDataQueue")
	private BlockingQueue<EmailMessageData> emailDataProcessingQueue;

	@Override
	public void startAllActiveSchedulers() {
		List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();
		if (jobInfoList != null) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			jobInfoList.forEach(jobInfo -> {
				try {
					JobDetail jobDetail = JobBuilder
							.newJob((Class<? extends QuartzJobBean>) Class.forName(CRON_CLASS_PATH))
							.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
					if (!scheduler.checkExists(jobDetail.getKey())) {
						Trigger trigger;
						jobDetail = scheduleCreator.createJob(
								(Class<? extends QuartzJobBean>) Class.forName(CRON_CLASS_PATH), false, context,
								jobInfo.getJobName(), jobInfo.getJobGroup());

						if (jobInfo.getCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
							trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
									jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
							scheduler.scheduleJob(jobDetail, trigger);
						} else {
							trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
									jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
						}

					}
				} catch (ClassNotFoundException e) {
					LOGGER.error("Cron Job class missing.Exception is:" + e.getMessage());
				} catch (SchedulerException e) {
					LOGGER.error("Error while scheduling job:" + e.getMessage());
				}
			});
		}
	}

	@Override
	public boolean scheduleNewJob(SchedulerJobInfo jobInfo) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(CRON_CLASS_PATH))
					.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
			if (!scheduler.checkExists(jobDetail.getKey())) {

				jobDetail = scheduleCreator.createJob((Class<? extends QuartzJobBean>) Class.forName(CRON_CLASS_PATH),
						false, context, jobInfo.getJobName(), jobInfo.getJobGroup());

				Trigger trigger;
				if (jobInfo.getCronJob()) {
					trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
							jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
				} else {
					trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
							jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
				}

				scheduler.scheduleJob(jobDetail, trigger);
				schedulerRepository.save(jobInfo);
				return Boolean.TRUE;
			} else {
				LOGGER.warn(
						"A Job with same name exists,So Cant't create with same name.Please try with different name.");
				return Boolean.FALSE;
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("Cron Job class missing.Exception is:" + e.getMessage());
			return Boolean.FALSE;
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return Boolean.FALSE;
		}
	}

	@Override
	public void updateScheduleJob(SchedulerJobInfo jobInfo) {
		Trigger newTrigger;
		if (jobInfo.getCronJob()) {
			newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
					jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		} else {
			newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime(),
					SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		}
		try {
			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
			schedulerRepository.save(jobInfo);
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
		}
	}

	@Override
	public boolean unScheduleJob(String jobName) {
		try {
			return schedulerFactoryBean.getScheduler().unscheduleJob(new TriggerKey(jobName));
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean deleteJob(SchedulerJobInfo jobInfo) {
		try {
			return schedulerFactoryBean.getScheduler()
					.deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean pauseJob(SchedulerJobInfo jobInfo) {
		try {
			schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean resumeJob(SchedulerJobInfo jobInfo) {
		try {
			schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean startJobNow(SchedulerJobInfo jobInfo) {
		try {
			schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job:" + e.getMessage());
			return false;
		}
	}

	// Creating new scheduler info for recurring or historical jobs.
	@Override
	public boolean createNewScheduler(SchedulerJobInfo schedulerJobInfo) {
		return (schedulerRepository.save(schedulerJobInfo) != null);
	}

	@Override
	public void batchCompletionSuccess() {
		try {
			LOGGER.info("batch processing completed now clearing related resources");

			cacheService.cleanConcurrentCache();
			emailDataProcessingQueue.clear();

		} catch (Exception e) {
			LOGGER.error("Error while clearing resources for the batch dew to:" + e.getMessage());
		}
	}
}
