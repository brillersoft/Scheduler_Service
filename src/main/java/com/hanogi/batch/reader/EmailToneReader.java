package com.hanogi.batch.reader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hanogi.batch.constants.EmailDirection;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.constants.MailTones;
import com.hanogi.batch.dto.AggregatedTone;
import com.hanogi.batch.dto.CalculatedTone;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.dto.EmailMetaDataDto;
import com.hanogi.batch.dto.EmailMetadata;
import com.hanogi.batch.dto.IndividualTone;
import com.hanogi.batch.dto.RetailClient;
import com.hanogi.batch.exceptions.BrillerBatchDBException;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.exceptions.BrillerBatchIOException;
import com.hanogi.batch.repositories.AggregatedToneRepositry;
import com.hanogi.batch.repositories.EmailHeaderRepositry;
import com.hanogi.batch.repositories.EmailMetadataRepositry;
import com.hanogi.batch.repositories.RetailClientRepository;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.services.IToneAnalyser;
import com.hanogi.batch.utils.bo.EmailMessageData;

import net.sf.ehcache.Cache;

@Service
public class EmailToneReader implements ToneReader<EmailMessageData> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private IToneAnalyser toneAnalyser;
	
	@Autowired
	@Qualifier("cacheErrorMap")
	private Map<String,String> cacheErrorMap;

	@Autowired
	private EmailMetadataRepositry emailMetadataRepo;


	@Autowired
	private EmailHeaderRepositry emailHeaderRepo;
	
	@Autowired
	private AggregatedToneRepositry aggregatedToneRepo;
	
	@Autowired
	private RetailClientRepository retailClientRepo;


	@Transactional
	private boolean saveMetaData
			(String finalBaseTone, String individualTone, double individualToneScore, String allToneStr, EmailMessageData emailMessageData)
			 throws BrillerBatchDBException {
		try {
			
			String mailDirection = emailMessageData.getEmailMetaData().getEmailDirection();
			
			String messageId = emailMessageData.getEmailMetaData().getEmailHeader().getMessageId();
			
			String mailThreadName = emailMessageData.getEmailMetaData().getEmailHeader().getMailThreadName();
					
			EmailMetadata emailMetadata = emailMessageData.getEmailMetaData();
			
			AggregatedTone aggregatedTone = new AggregatedTone();

			aggregatedTone.setAggregatedTone("");
			aggregatedTone.setMailThreadName(mailThreadName);
			aggregatedTone.setStatus("1");

			CalculatedTone calculatedTone = new CalculatedTone();

			calculatedTone.setCalculatedTone(finalBaseTone);
			
			calculatedTone.setIndividualTone(individualTone);
			calculatedTone.setIndividualToneScore(individualToneScore);
			calculatedTone.setMailDirection(mailDirection);
			calculatedTone.setMessageId(messageId);
			calculatedTone.setMailThreadName(mailThreadName);
			calculatedTone.setStatus("1");

			IndividualTone indTone = new IndividualTone();
			indTone.setIndividualTone(allToneStr);
			indTone.setMailDirection(mailDirection);
			indTone.setMessageId(messageId);
			indTone.setMailThreadName(mailThreadName);
			indTone.setStatus("1");

			EmailHeader emailHeader = setMailHeader(emailMetadata.getEmailHeader());

			EmailMetaDataDto metaDataDto = new EmailMetaDataDto();

			metaDataDto.setCalculatedTone(calculatedTone);

			metaDataDto.setIndvidualToneId(indTone);

			metaDataDto.setStatus("1");
			
			metaDataDto.setEmailDirection(mailDirection);
			
			metaDataDto.setBatchRunDetails(emailMessageData.getEmailMetaData().getBatchRunDetails());
			
			metaDataDto.setEmailProcessingExecutionStatus(ExecutionStatusEnum.Complete.name());
			
			metaDataDto.setFromEmailId(emailMessageData.getEmailMetaData().getFromEmailId());
			
			metaDataDto.setEmailHeader(emailHeader);
		
			
			AggregatedTone objectAggTone = aggregatedToneRepo.findByMailThreadName(mailThreadName);
			
			if(null==objectAggTone) {
				AggregatedTone savedAggregatedTone =  aggregatedToneRepo.save(aggregatedTone);
				
				if(null!=savedAggregatedTone) {
					
					int aggregatedToneId = savedAggregatedTone.getAggregatedToneId();
					
					metaDataDto.setAggregatedToneId(aggregatedToneId);
					
				}
			}else {
				metaDataDto.setAggregatedToneId(objectAggTone.getAggregatedToneId());
			}
			
			EmailMetaDataDto savedMetaData = emailMetadataRepo.save(metaDataDto);
			
			String emailDomainName = emailMessageData.getEmailDomainName();
			
			if(EmailDirection.Received.name().equalsIgnoreCase(mailDirection)) {
				
				String fromEmailId = emailMessageData.getEmailMetaData().getFromEmailId();
				
				saveRetailClientDetails(fromEmailId, emailDomainName);
				
				
			}else {
				
				String strToEmailId = savedMetaData.getEmailHeader().getToEmailId();
				
				if(null!=strToEmailId) {
					String[] toEmails = strToEmailId.split(",");
					
					for (String email : toEmails) {
						
						saveRetailClientDetails(email, emailDomainName);
						
					}
				}
				
					
			}
	
			return true;
		} catch (Exception e) {
			throw new BrillerBatchDBException("Error while persisiting the mail details in the DB ",e);
		}

	}

	public EmailHeader setMailHeader(EmailHeader emailHeader) {

		emailHeader.setSenderIp("");
		emailHeader.setContentLanguage("ENG");

		EmailHeader emailHeaderSaved = emailHeaderRepo.save(emailHeader);
		
		return emailHeaderSaved;

	}
	
	
	//@Async("toneProcessorThreadPool")
	public void readTone(List<EmailMessageData> listEmailMessageData, Cache cacheInstance) {
		
		try {
			
			// Send the data to the Tone Analyzer and wait for the response

			List<List<String[]>> toneMsgList = toneAnalyser.analyseTone(listEmailMessageData);

			
			for (int i=0;i<toneMsgList.size();i++) {
				
				String allToneStr = null;
				
				String finalTone = null;
				
				double finalScore=0;

				for (String[] tones : toneMsgList.get(i)) {

					if (null != allToneStr) {

						allToneStr = String.join(",", allToneStr, Arrays.toString(tones));
						
						double score = Double.parseDouble(tones[1]);
						
						if(score>finalScore) {
							
							finalTone= tones[0];
							
							finalScore = score;
						}
						

					} else {
						
						allToneStr = Arrays.toString(tones);
						
						finalTone = tones[0];
						
						finalScore = Double.parseDouble(tones[1]);
					}

				}
				
				String finalBaseTone = MailTones.valueOf(finalTone.toUpperCase()).getValue();
				
				
				
				boolean isMessageInsertedToDB = 
							saveMetaData(finalBaseTone,finalTone,finalScore,allToneStr, listEmailMessageData.get(i));

				String MessageId = listEmailMessageData.get(i).getEmailMetaData().getEmailHeader().getMessageId();


				// Updating the cache for MessageId
				if (isMessageInsertedToDB) {
					
					logger.info(MessageId +"execution COMPLETED and data inserted to DB successfully now updating status in cache with COMPLETE....");
					
					cacheService.checkAddUpdateCache(MessageId, ExecutionStatusEnum.Complete,cacheInstance);
					
				} else {
					
					logger.warn(MessageId+ "execution COMPLETED and data inserted to DB failed now updating status in cache with FAILURE....");
					
					cacheService.checkAddUpdateCache(MessageId, ExecutionStatusEnum.failure,cacheInstance);
				}
				
			}
			
		}catch(BrillerBatchIOException | BrillerBatchDataException e) {
			
			logger.error("Error while getting the response from tone analyser",e);
		
			
			cacheService.updateCacheStatus(listEmailMessageData,ExecutionStatusEnum.failure,cacheInstance);
			
		}
		
		catch(BrillerBatchDBException e) {
			
			logger.error("Error while persisting the data in the DB",e);
			
			cacheService.updateCacheStatus(listEmailMessageData,ExecutionStatusEnum.failure,cacheInstance);	
			
			
		}

				
	}
	
	public void saveRetailClientDetails(String email,String emailDomainName ) {
		
		String[] arrEmail = email.split("@");
		
		String domainName = arrEmail[1];
		
		if(!domainName.contains(emailDomainName)) {
			
			List<RetailClient> lstRetailClient = retailClientRepo.findByDomainName(domainName);
			
			if(null==lstRetailClient || lstRetailClient.size()==0) {
				
				RetailClient retailClient = new RetailClient();
				
				retailClient.setDomainName(domainName);
				
				retailClientRepo.save(retailClient);
				
			}
			
		}
	}

}
