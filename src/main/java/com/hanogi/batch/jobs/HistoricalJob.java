package com.hanogi.batch.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author abhishek.gupta02
 *
 */

public class HistoricalJob extends QuartzJobBean {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Historical batch Job triggered");
	}
}
