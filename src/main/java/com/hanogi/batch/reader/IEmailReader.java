package com.hanogi.batch.reader;

import java.util.Map;

import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.batch.BatchRunDetails;

import net.sf.ehcache.Cache;

public interface IEmailReader {
	

	/**
	 * 
	 * @param email
	 * @param batchRunDetails
	 * @param emailProcessingStatusMap
	 * @param cache
	 * @throws Exception
	 */
	public void  readMail(Email email,BatchRunDetails batchRunDetails, Map<String, ExecutionStatusEnum> emailProcessingStatusMap,Cache cache ) throws Exception;

}
