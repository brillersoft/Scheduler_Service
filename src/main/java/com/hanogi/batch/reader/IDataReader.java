package com.hanogi.batch.reader;

import java.util.List;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.batch.BatchRunDetails;

public interface IDataReader {

	public void readData(List<Email> EmailList, BatchRunDetails batchRunDetails, CacheType cacheType) throws Exception;
}
