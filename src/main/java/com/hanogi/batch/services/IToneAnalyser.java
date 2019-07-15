package com.hanogi.batch.services;

import java.util.List;

import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.exceptions.BrillerBatchIOException;
import com.hanogi.batch.utils.bo.EmailMessageData;

public interface IToneAnalyser {
	
	public String analyseTone(String messageBody);
	
	public List<List<String[]>> analyseTone(List<EmailMessageData> messageBody)
									throws BrillerBatchIOException, BrillerBatchDataException;
	
}
