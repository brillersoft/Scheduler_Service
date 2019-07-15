package com.hanogi.batch.services;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.dto.batch.BatchRunDetails;

/**
 * @author abhishek.gupta02
 *
 */

public interface IBatchService {

	void createNewBatch(BatchRunDetails batchRunDetails);

	Boolean runBatch(BatchRunDetails batchRunDetails,CacheType cacheType);

	BatchRunDetails getBatchRunDetails(Integer batchId);
	
	void updateBatchExecutionStatus(BatchRunDetails batchRunDetails,ExecutionStatusEnum executionStatus);

}
