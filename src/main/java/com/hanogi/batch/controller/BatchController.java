package com.hanogi.batch.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanogi.batch.dto.SchedulerJobInfo;
import com.hanogi.batch.services.IToneAnalyser;
import com.hanogi.batch.services.ISchedulerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author abhishek.gupta02
 *
 */
@RestController
@Api(value = "Batch Mail Processing System")
public class BatchController {

	@Autowired
	private ISchedulerService schedulerService;

	@Autowired
	private IToneAnalyser toneAnalyser;

	/*
	 * @Autowired private CacheService CacheServiceImpl;
	 */

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * To up all the scheduled jobs and active jobs in case of our services start.
	 */
	@PostConstruct
	public void scheduleAllJobs() {
		schedulerService.startAllActiveSchedulers();
	}

	/**
	 * This method will be invoked by the batch job for one time historical load in
	 * to DB.Once ran successfully this method will never be invoked
	 */
	public void runHistoricalBatch(SchedulerJobInfo jobInfo) {

		log.info("Running Historical batch for jobId:" + jobInfo.getId());

		try {
			schedulerService.startJobNow(jobInfo);

		} catch (Exception e) {
			log.error("Historical job scheduler failed dew to" + e.getMessage());
		}

	}

	/**
	 * This method will be invoked by the batch job for recurrent data load in to
	 * DB. The frequency of this recurrent load will be pulled from DB and can be
	 * set from Admin UI. This will load the data from the last successfully ran
	 * recurrent batch
	 * 
	 * @return
	 */
	public String runRecurrentBatch() {

		log.info("Inside runRecurrentBatch");

		return null;

	}

	/**
	 * This method will be invoked from Admin UI when the admin wants to run the
	 * batch for a given list of email ids.The no of unique email ids will be
	 * limited to no. of user licenses configured
	 * 
	 * @param emailId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/runbatch/email")
	@ApiOperation(value = "Running Batch job for a particular Email-Id", response = String.class, consumes = "emailId on whuc batch should run")
	public String runBatchforEmail(@RequestParam List<String> emailId) throws Exception {

		log.info("Inside runBatchforEmail");

		return null;

	}

	@GetMapping("/test")
	public String test() {

		// CacheServiceImpl.checkAddUpdateCache("ABC", ExecutionStatus.Inprogress);
		return toneAnalyser.analyseTone("plain English text to analyze, e.g., the email message. mandatory");
	}

}
