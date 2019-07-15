package com.hanogi.batch.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.BaseTones;
import com.hanogi.batch.dto.AggregatedTone;
import com.hanogi.batch.dto.CalculatedTone;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.repositories.AggregatedToneRepositry;
import com.hanogi.batch.repositories.CalculatedToneRepositry;
import com.hanogi.batch.repositories.EmailHeaderRepositry;
import com.hanogi.batch.repositories.IndividualToneRepositry;
import com.hanogi.batch.services.IToneCalcultionService;

@Component
public class ToneCalculationServiceImpl implements IToneCalcultionService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmailHeaderRepositry emailHeaderRepo;
	
	@Autowired
	private CalculatedToneRepositry calculatedToneRepo;
	
	@Autowired
	private IndividualToneRepositry individualToneRepo;
	
	@Autowired
	private AggregatedToneRepositry aggregatedToneRepo;
	

	@Override
	public void executeToneCalculation(Map<String, Set<EmailHeader>> mailThreadsMap) {
		
		
		List<EmailHeader> existingMailThreadsInDB =  fetchExistingDataFromDB(mailThreadsMap);
		
		
		Map<String, Set<EmailHeader>> completeMailThreadMap = new HashMap<>();
		
		if(null!=existingMailThreadsInDB && existingMailThreadsInDB.size()>0) {
			
			for (EmailHeader mailThread : existingMailThreadsInDB) {
				
				String threadName = mailThread.getMailThreadName();
				
				Set<EmailHeader> threadDetails = null;

				if (null != completeMailThreadMap.get(threadName)) {

					threadDetails = completeMailThreadMap.get(threadName);

				} else {

					threadDetails = new TreeSet<>();
				}

				threadDetails.add(mailThread);
				
				completeMailThreadMap.put(threadName, threadDetails);
				
			}
			
		}
		
		updateMailThreadIndex(completeMailThreadMap);
		
		calculateAndUpdateBaseTone(completeMailThreadMap);

	}



	public void updateMailThreadIndex(Map<String, Set<EmailHeader>> mailThreadsMap) {
		
		if (null != mailThreadsMap) {

			Set<String> keys = mailThreadsMap.keySet();

			for (String threadName : keys) {

				Set<EmailHeader> mailThreads = mailThreadsMap.get(threadName);
				
				if(null!=mailThreads) {
					
					int threadIndex = 0;
					
					int size = mailThreads.size();

					for (EmailHeader mailThread : mailThreads) { 

						threadIndex++;
						
						double calcThreadIndex = ((double)threadIndex)/size;
						
						emailHeaderRepo.updateMailThreadIndex(calcThreadIndex, mailThread.getMessageId());
						
						calculatedToneRepo.updateMailThreadIndex(calcThreadIndex, mailThread.getMessageId());
						
						individualToneRepo.updateMailThreadIndex(calcThreadIndex, mailThread.getMessageId());

					}
					
				}

			}

		}
	}
	
	
	public List<EmailHeader> fetchExistingDataFromDB(Map<String, Set<EmailHeader>> mailThreadsMap) {
		
		List<EmailHeader> mailThreadsFromDB = null;
		
		if (null != mailThreadsMap) {

			Set<String> keys = mailThreadsMap.keySet();
			
			List<String> emailThreadList = keys.stream().collect(Collectors.toList());
			
			mailThreadsFromDB = emailHeaderRepo.findByMailThreadNameIn(emailThreadList);
			
		}

		return mailThreadsFromDB;
	}
	
	public List<CalculatedTone> fetchcalculatedToneDataFromDB(Map<String, Set<EmailHeader>> mailThreadsMap) {

		List<CalculatedTone> calculatedTonesFromDB = null;

		if (null != mailThreadsMap) {

			Set<String> keys = mailThreadsMap.keySet();

			List<String> emailThreadList = keys.stream().collect(Collectors.toList());

			calculatedTonesFromDB = calculatedToneRepo.findByMailThreadNameIn(emailThreadList);

		}

		return calculatedTonesFromDB;
	}
	
	

	public void calculateAndUpdateBaseTone(Map<String, Set<EmailHeader>> mailThreadsMap) {
		
		List<CalculatedTone> calculatedTonesFromDB =  fetchcalculatedToneDataFromDB(mailThreadsMap);
		
		Map<String,AggregatedTone> aggregatedToneDetails = new HashMap<>();
		
		for (CalculatedTone calculatedTone : calculatedTonesFromDB) {
			
			double mailIndex = calculatedTone.getMailIndex();
			
			String calcTone = calculatedTone.getCalculatedTone();
			
			int score=BaseTones.valueOf(calcTone).getValue();
			
			double calculatedToneScore = mailIndex*score;
			
			double baseToneScore = mailIndex*4;
			
			double mailCSAT = ((double)calculatedToneScore*100)/baseToneScore;
			
			calculatedToneRepo.updateToneDetails(calculatedToneScore, baseToneScore,mailCSAT,calculatedTone.getMessageId());
			
			String threadName = calculatedTone.getMailThreadName();
			
			if(null!=aggregatedToneDetails.get(threadName)) {
				
				AggregatedTone at = aggregatedToneDetails.get(threadName);
				
				double calcToneScore = at.getAggregatedToneScore()+calculatedToneScore;
				double calcBaseScore = at.getBenchMarkToneScore()+baseToneScore;
				
				double mailCSATScore = ((double)calcToneScore*100)/calcBaseScore;
				
				at.setAggregatedToneScore(calcToneScore);
				at.setBenchMarkToneScore(calcBaseScore);
				at.setThreadCSATScore(mailCSATScore);
				aggregatedToneDetails.put(threadName, at);
				
			}else {
				
				AggregatedTone at = new AggregatedTone();
				
				at.setAggregatedToneScore(calculatedToneScore);
				at.setBenchMarkToneScore(baseToneScore);
				at.setThreadCSATScore(mailCSAT);
				aggregatedToneDetails.put(threadName, at);
				
			}
			
		}
		
		
		for (String threadName : aggregatedToneDetails.keySet()) {
			
			AggregatedTone aggTone = aggregatedToneDetails.get(threadName);
			
			double calcToneScore = aggTone.getAggregatedToneScore();
			double calcBaseScore = aggTone.getBenchMarkToneScore();
			
			double mailCSATScore = aggTone.getThreadCSATScore();
			
			aggregatedToneRepo.updateToneDetails(calcToneScore, calcBaseScore,mailCSATScore,threadName);
			
		}
	}
	
}
