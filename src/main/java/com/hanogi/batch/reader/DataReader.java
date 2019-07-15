package com.hanogi.batch.reader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.BaseTones;
import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.dto.AggregatedTone;
import com.hanogi.batch.dto.CalculatedTone;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.dto.batch.BatchRunDetails;
import com.hanogi.batch.services.IBatchService;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.services.ISummaryCalculationService;
import com.hanogi.batch.services.IToneCalcultionService;
import com.hanogi.batch.utils.bo.EmailMessageData;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@Component
public class DataReader implements IDataReader {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("mailReadersMaps")
	private Map<String, Map<String, IEmailReader>> mailReaders;

	@Autowired
	@Qualifier("emailDataQueue")
	private BlockingQueue<EmailMessageData> emailDataProcessingQueue;

	@Autowired
	private ToneReader<EmailMessageData> emailToneReader;

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private IBatchService batchService;
	
	@Autowired
	private IToneCalcultionService toneCalculationService;
	
	@Autowired
	private ISummaryCalculationService summaryCalculationService;
	
	@Autowired
	@Qualifier("cacheErrorMap")
	private Map<String,String> cacheErrorMap;


	Long startTimeInMillSec = 0l;

	@Override
	public void readData(List<Email> emailList, BatchRunDetails batchRunDetails, CacheType cacheType) throws Exception {
		Map<String, ExecutionStatusEnum> emailProcessingStatusMap = new ConcurrentHashMap<>();

		Cache cacheInstance = cacheService.getCacheInstance(cacheType);

		loadCacheData(cacheInstance, batchRunDetails);

		Calendar calendar = Calendar.getInstance();

		startTimeInMillSec = calendar.getTimeInMillis();

		logger.warn("Batch processing start time:" + calendar.getTimeInMillis());

		for (Email email : emailList) {
			logger.info("setting execution status as Inprogress");
			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.Inprogress);
		}

		for (Email email : emailList) {
			IEmailReader emailReader = mailReaders.get(email.getObjEmailDomainDetails().getEmailServiceProvider())
					.get(email.getObjEmailDomainDetails().getServerDeploymentType());

			logger.info("Start processing mailId:" + email.getEmailId());

			emailReader.readMail(email, batchRunDetails, emailProcessingStatusMap, cacheInstance);

		}

		Map<String, Set<EmailHeader>> mailThreadsMap = beginToneProcessing(emailProcessingStatusMap, cacheInstance);
		
		boolean successulfulyCompleted = checkCacheForBatchCompletion(cacheInstance);
		
		ExecutionStatusEnum batchExecutionStatus = ExecutionStatusEnum.failure;
		
		logger.info("Checking for successful completion of batch : "+ successulfulyCompleted);
		
		if(successulfulyCompleted) {
		 			
			 batchExecutionStatus = hasEmailReadersContainsErrors(emailProcessingStatusMap)
					? ExecutionStatusEnum.failure
					: ExecutionStatusEnum.Complete;
			
			
			toneCalculationService.executeToneCalculation(mailThreadsMap);
		}
		
		perfomSummaryCalculation(batchRunDetails,emailProcessingStatusMap);
		logger.info("Start updating the batch Execution status");
		batchService.updateBatchExecutionStatus(batchRunDetails, batchExecutionStatus);
		
		performCleanUp(cacheInstance,emailProcessingStatusMap);

	}

	public  void perfomSummaryCalculation(BatchRunDetails batchRunDetails, Map<String, ExecutionStatusEnum> emailProcessingStatusMap) {
		
		//Date batchDate = batchRunDetails.getFromDate();
		logger.info("Start performing summary calculations");

		Date fromDate = getDate("2018-01-01 00:00:00");
		
		List<EmailHeader> lstEmailDetails = summaryCalculationService.fetchMailDetailsByDate(fromDate);
		
		List<AggregatedTone> lstAggregatedTones = summaryCalculationService.getAggregatedTones(lstEmailDetails);
		
		Map<String, Integer> retailClientMap = summaryCalculationService.getAllRetailClients();
		
		Map<String, String> mailThreadToneMap = new HashMap<>();
		
		for (AggregatedTone aggTone : lstAggregatedTones) {
			
			String baseTone = aggTone.getAggregatedToneScore()<0.0? BaseTones.NEGATIVE.name():BaseTones.POSITIVE.name();
			
			mailThreadToneMap.put(aggTone.getMailThreadName(), baseTone);
		}
		
		for (String emailId : emailProcessingStatusMap.keySet()) {
			
			summaryCalculationService.executeSummaryCalculation(emailId,lstEmailDetails,
													mailThreadToneMap,retailClientMap,fromDate);
			
		}
		
	}
	
	public Date getDate(String batchDate) {
		
		DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
		
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Date parsed=null;
		try {
			parsed = parser.parse(batchDate);
		} catch (ParseException e) {
			e.printStackTrace();

		}
		
		return parsed;
	}

	private void performCleanUp(Cache cacheInstance, Map<String, ExecutionStatusEnum> emailProcessingStatusMap) {
		
		logger.info("Start performing the clean up activity after batch completion");
		
		cacheInstance.removeAll();
		
		cacheErrorMap.clear();
		
		emailProcessingStatusMap.clear();
		
		if(!emailDataProcessingQueue.isEmpty()) {
			
			emailDataProcessingQueue.clear();
		}
		
		logger.info("Finshed performing the clean up activity after batch completion");
	}

	public boolean hasEmailReadersFinishedProcessing(Map<String, ExecutionStatusEnum> emailProcessingStatusMap) {

		boolean isComplete = emailProcessingStatusMap.containsValue(ExecutionStatusEnum.Inprogress) ? false : true;

		return isComplete;
	}

	/**
	 * This method checks that whether there is any email that whose processing
	 * status is Failure
	 * 
	 * @param emailProcessingStatusMap
	 * @return
	 */

	public boolean hasEmailReadersContainsErrors(Map<String, ExecutionStatusEnum> emailProcessingStatusMap) {

		boolean hasErrors = emailProcessingStatusMap.containsValue(ExecutionStatusEnum.failure) ? true : false;

		return hasErrors;
	}

	private void loadCacheData(Cache cacheInstance, BatchRunDetails batchRunDetails) {
		if (batchRunDetails.getBatchExecutionStatus().getStatusName().equals(ExecutionStatusEnum.failure)) {
			logger.info("Loading data from last failed batch already succeed records.");

		}
	}
	
	public Map<String, Set<EmailHeader>> beginToneProcessing
						(Map<String, ExecutionStatusEnum> emailProcessingStatusMap, Cache cacheInstance) {
		
		List<EmailMessageData> listEmailMessage = new ArrayList<>();

		Map<String, Set<EmailHeader>> mailThreadsMap = new HashMap<>();

		while (!(emailDataProcessingQueue.isEmpty() && hasEmailReadersFinishedProcessing(emailProcessingStatusMap))) {

			if (listEmailMessage.size() <= 100) {

				EmailMessageData emailMessage = emailDataProcessingQueue.poll();

				if (null != emailMessage) {

					listEmailMessage.add(emailMessage);

					String threadName = emailMessage.getEmailMetaData().getEmailHeader().getMailThreadName();

					Set<EmailHeader> mailThread = null;

					if (null != mailThreadsMap.get(threadName)) {

						mailThread = mailThreadsMap.get(threadName);

					} else {

						mailThread = new TreeSet<>();
					}

					EmailHeader objMailThread = new EmailHeader();

					objMailThread.setEmailDate(emailMessage.getEmailMetaData().getEmailHeader().getEmailDate());

					objMailThread.setMessageId(emailMessage.getEmailMetaData().getEmailHeader().getMessageId());

					mailThread.add(objMailThread);

					mailThreadsMap.put(threadName, mailThread);

				}

			}

			if (listEmailMessage.size() == 100) {

				emailToneReader.readTone(listEmailMessage, cacheInstance);

				listEmailMessage = new ArrayList<>();
			}

		}

		if (listEmailMessage.size() > 0) {

			emailToneReader.readTone(listEmailMessage, cacheInstance);

		}
		
		return mailThreadsMap;

	}
	
	
	@SuppressWarnings("static-access")
	public boolean checkCacheForBatchCompletion(Cache cacheInstance) {

		boolean hasSuccessfullyProcessed = false;

		boolean isProcessing = true;
		
		int exceptionCount=0;
		
		boolean containsOnlyErrorRecords = false;
		
		int size=0;

		while (isProcessing) {

			try {
				size = cacheInstance.getSize();
				
				Thread.currentThread().sleep(30000);

				isProcessing = cacheInstance.isValueInCache(ExecutionStatusEnum.Inprogress);
				
				if (size == cacheInstance.getSize() && containsOnlyErrorRecords) {

					isProcessing = false;
					hasSuccessfullyProcessed = true;

				}

				if (isProcessing) {

					List keysList = cacheInstance.getKeys();

					for (Object key : keysList) {

						Element element = cacheInstance.get(key);

						ExecutionStatusEnum messageStatus = (ExecutionStatusEnum) element.getObjectValue();

						if (ExecutionStatusEnum.Inprogress.equals(messageStatus)) {
							
							
							if (!cacheErrorMap.containsKey(key)) {
								containsOnlyErrorRecords = false;
								break;

							}else {
								containsOnlyErrorRecords=true;
							}

						}

					} // for Loop Ends

				} // if loop Ends
				else {
					
					Thread.currentThread().sleep(30000);

					isProcessing = cacheInstance.isValueInCache(ExecutionStatusEnum.Inprogress);

					if(!isProcessing) {
						hasSuccessfullyProcessed = true;
					}
				}

			} catch (Exception e) {
				
				logger.error("Error while checkin the cache for batch completion ",e);
				
				exceptionCount++;
				
				if(exceptionCount>5) {
					isProcessing=false;
					hasSuccessfullyProcessed = false;
				}

			}

		} // while loop ends

		return hasSuccessfullyProcessed;

	}

}
