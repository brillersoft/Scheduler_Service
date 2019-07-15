package com.hanogi.batch.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.ExecutionStatus;
import com.hanogi.batch.dto.batch.BatchRunDetails;
import com.hanogi.batch.reader.DataReader;
import com.hanogi.batch.repositories.BatchRunDetailsRepositry;
import com.hanogi.batch.services.EmployeeService;
import com.hanogi.batch.services.IBatchService;

@Component
public class BatchService implements IBatchService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataReader dataReader;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private BatchRunDetailsRepositry batchrunDetailsRepo;

	@Autowired
	@Qualifier("executionStatusMap")
	private List<ExecutionStatus> executionStatusMap;

	@Override
	public Boolean runBatch(BatchRunDetails batchRunDetails, CacheType cacheType) {

		try {
			List<Email> employeeMailListToToneAnalyse = employeeService.getEmployeeMailListToToneAnalyse();

			if (employeeMailListToToneAnalyse != null && employeeMailListToToneAnalyse.size() != 0) {
				dataReader.readData(employeeMailListToToneAnalyse, batchRunDetails, cacheType);
				return Boolean.TRUE;
			} else {
				logger.warn("No mail Id to found to process");
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failure while running the batch:" + e.getMessage());
			return Boolean.FALSE;
		}
	}

	@Override
	public BatchRunDetails getBatchRunDetails(Integer batchId) {
		Optional<BatchRunDetails> batchRun = batchrunDetailsRepo.findById(batchId);

		BatchRunDetails batchRunDetails = batchRun.get();

		if (batchRunDetails != null) {
			return batchRunDetails;
		} else {
			return null;
		}
	}

	@Override
	public void createNewBatch(BatchRunDetails batchRunDetails) {

	}

	@Override
	public void updateBatchExecutionStatus(BatchRunDetails batchRunDetails, ExecutionStatusEnum executionStatus) {
		logger.info("Updating BatchId:" + batchRunDetails.getBatchJobId() + " execution status with" + executionStatus);
		try {

			List<ExecutionStatus> executionStatusList = executionStatusMap.stream()
					.filter(status -> status.getStatusName().equals(executionStatus)).collect(Collectors.toList());

			ExecutionStatus status = new ExecutionStatus();

			status.setStatusName(executionStatusList.get(0).getStatusName());
			status.setStatusDesc(executionStatusList.get(0).getStatusDesc());

			batchRunDetails.setBatchExecutionStatus(status);

			batchrunDetailsRepo.save(batchRunDetails);
			
		} catch (Exception e) {
			logger.error("Error while updating batch status with message:" + e.getMessage());
		}

	}

}
