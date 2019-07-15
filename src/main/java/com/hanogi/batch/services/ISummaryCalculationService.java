package com.hanogi.batch.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hanogi.batch.dto.AggregatedTone;
import com.hanogi.batch.dto.CalculatedTone;
import com.hanogi.batch.dto.EmailHeader;

public interface ISummaryCalculationService {


	public void executeSummaryCalculation(String emailId, List<EmailHeader> lstEmailHeader,
								Map<String, String> messageIdToneMap, Map<String, Integer> retailClientMap, Date fromDate);
	
	public List<EmailHeader> fetchMailDetailsByDate(Date fromDate);
	
	public List<AggregatedTone> getAggregatedTones(List<EmailHeader> lstEmailDetails);
	
	public Map<String,Integer> getAllRetailClients();

}
