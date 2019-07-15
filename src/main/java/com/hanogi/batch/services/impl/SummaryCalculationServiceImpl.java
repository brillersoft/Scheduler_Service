package com.hanogi.batch.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.BaseTones;
import com.hanogi.batch.constants.EmailDirection;
import com.hanogi.batch.dto.AggregatedTone;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.dto.RetailAccountCsatSummary;
import com.hanogi.batch.dto.RetailClient;
import com.hanogi.batch.dto.RetailEmployeeCsatSummary;
import com.hanogi.batch.repositories.AggregatedToneRepositry;
import com.hanogi.batch.repositories.EmailHeaderRepositry;
import com.hanogi.batch.repositories.RetailAccountCsatSummaryRepository;
import com.hanogi.batch.repositories.RetailClientRepository;
import com.hanogi.batch.repositories.RetailEmployeeCsatSummaryRepository;
import com.hanogi.batch.services.ISummaryCalculationService;

@Component
public class SummaryCalculationServiceImpl implements ISummaryCalculationService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmailHeaderRepositry emailHeaderRepo;
	
	@Autowired
	private RetailClientRepository retailClientRepo;
	
	@Autowired
	private AggregatedToneRepositry aggToneRepo;
	
	@Autowired
	private RetailEmployeeCsatSummaryRepository employeeCsatRepo;
	
	@Autowired
	private RetailAccountCsatSummaryRepository accountCsatRepo;
	
	
	public List<EmailHeader> fetchMailDetailsByDate(Date fromDate){
		
		 List<EmailHeader> lstEmailDetails = emailHeaderRepo.fetchMailDetails(fromDate);
		 
		 return lstEmailDetails;
		
	}

	@Override
	public void executeSummaryCalculation(String emailId,  List<EmailHeader> lstEmailDetails, 
								Map<String,String> mailThreadToneMap,Map<String,Integer> retailClientMap,Date fromDate) {
		
		Map<Integer,Map<Date,RetailEmployeeCsatSummary>> employeeSummaryMap = new HashMap<>();
		
		
		if(null!=lstEmailDetails && lstEmailDetails.size()>0) {
			
			for (EmailHeader emailHeader : lstEmailDetails) {
				
				String emailDirection = emailHeader.getEmailDirection();
				
				if(emailDirection.equalsIgnoreCase(EmailDirection.Sent.name())) {
					
					String fromEmailId = emailHeader.getFromEmailId();
					
					if(fromEmailId.equalsIgnoreCase(emailId)) {
						
						String[] toEmailId = emailHeader.getToEmailId().split(",");
						
						for (String email : toEmailId) {
							
							performSummaryCalculation(email, retailClientMap, employeeSummaryMap, 
																mailThreadToneMap, emailHeader,emailId);
							
						}
						
					}
					
					
				}else {
					
					String toEmailId = emailHeader.getToEmailId();
					
					if(toEmailId.contains(emailId)) {
						
						String fromEmailId = emailHeader.getFromEmailId();
						
						performSummaryCalculation(fromEmailId, retailClientMap, 
											employeeSummaryMap, mailThreadToneMap, emailHeader,emailId);
					}
					
				}
				
			}
			
			saveEmployeeCsatSummary(employeeSummaryMap,fromDate);
			
			performAccountCsatSummaryCalculation(fromDate);
			
		}
	}
	
	public void performAccountCsatSummaryCalculation(Date fromDate) {
		
		Iterable<RetailEmployeeCsatSummary> allEmployeeCsatSummary = employeeCsatRepo.findAll();
		
		Map<Integer,Map<Date,RetailAccountCsatSummary>> mapAccountSummary = new HashMap<>();
		
		for (RetailEmployeeCsatSummary retailEmployeeCsatSummary : allEmployeeCsatSummary) {
			
			Integer accountId = retailEmployeeCsatSummary.getAccountId();
			
			Date mailDate = retailEmployeeCsatSummary.getMailDate();
			
			if(null!=mapAccountSummary.get(accountId)) {
				
				Map<Date,RetailAccountCsatSummary> dateAccountSummaryMap = mapAccountSummary.get(accountId);
				
				if(null!=dateAccountSummaryMap.get(mailDate)) {
					
					RetailAccountCsatSummary accountCsatSummary = dateAccountSummaryMap.get(mailDate);
					
					Integer negativeInteractionsInObj = retailEmployeeCsatSummary.getNegativeInteractions();
					Integer totalInteractionsInObj = retailEmployeeCsatSummary.getTotalInteractions();
					
					Integer negativeInteractionsInMap = accountCsatSummary.getNegativeInteractions();
					Integer totalInteractionsInMap = accountCsatSummary.getTotalInteractions();
					
					Integer negativeInteractions = negativeInteractionsInMap+negativeInteractionsInObj;
					Integer totalInteractions =  totalInteractionsInObj+ totalInteractionsInMap;
					
					double csat = ((double)(totalInteractions-negativeInteractions)/totalInteractions)*100;
					
					accountCsatSummary.setNegativeInteractions(negativeInteractions);
					accountCsatSummary.setTotalInteractions(totalInteractions);
					accountCsatSummary.setCsat(csat);
					accountCsatSummary.setStatus("Y");
					accountCsatSummary.setMailDate(mailDate);
					
					dateAccountSummaryMap.put(mailDate, accountCsatSummary);
					mapAccountSummary.put(accountId, dateAccountSummaryMap);
					 
					
				}else {
					
					createAccountCsatAccountSummary(retailEmployeeCsatSummary, dateAccountSummaryMap, mapAccountSummary);
					
				}
				
				
			}else {
				
				Map<Date,RetailAccountCsatSummary> dateAccountSummaryMap = new HashMap<>();
				
				createAccountCsatAccountSummary(retailEmployeeCsatSummary, dateAccountSummaryMap, mapAccountSummary);
				
			}
			
		}		
		
		
		saveAccountCsatSummary(fromDate,mapAccountSummary);
		
	}
	
	public void saveAccountCsatSummary(Date fromDate, Map<Integer, Map<Date, RetailAccountCsatSummary>> mapAccountSummary) {

		accountCsatRepo.deleteAccountCsatSummaryForDate(fromDate);

		for (Integer accountId : mapAccountSummary.keySet()) {

			Map<Date, RetailAccountCsatSummary> accountCsatMap = mapAccountSummary.get(accountId);

			for (Date mailDate : accountCsatMap.keySet()) {

				RetailAccountCsatSummary empCsatSummary = accountCsatMap.get(mailDate);

				accountCsatRepo.save(empCsatSummary);

			}

		}

	}

	public void createAccountCsatAccountSummary(RetailEmployeeCsatSummary retailEmployeeCsatSummary , 
												Map<Date,RetailAccountCsatSummary> dateAccountSummaryMap,
												Map<Integer,  Map<Date,RetailAccountCsatSummary>> mapAccountSummary) {
		
		RetailAccountCsatSummary accountCsatSummary = new RetailAccountCsatSummary();
		
		Date mailDate = retailEmployeeCsatSummary.getMailDate();
		Integer accountId = retailEmployeeCsatSummary.getAccountId();
		
		accountCsatSummary.setMailDate(mailDate);
		accountCsatSummary.setAccountId(accountId);
		
		Integer negativeInteractions = retailEmployeeCsatSummary.getNegativeInteractions();
		Integer totalInteractions = retailEmployeeCsatSummary.getTotalInteractions();
		
		accountCsatSummary.setNegativeInteractions(negativeInteractions);
		accountCsatSummary.setTotalInteractions(totalInteractions);
		
		double csat = ((double)(totalInteractions-negativeInteractions)/totalInteractions)*100;
		
		accountCsatSummary.setCsat(csat);
		
		if(csat<50.00) {
			accountCsatSummary.setEscalations(1);
		}else {
			accountCsatSummary.setEscalations(0);
		}
		accountCsatSummary.setStatus("Y");
		
		accountCsatSummary.setMailDate(mailDate);
		
		dateAccountSummaryMap.put(mailDate, accountCsatSummary);
		
		mapAccountSummary.put(accountId, dateAccountSummaryMap);
	}

	public  void saveEmployeeCsatSummary(Map<Integer, Map<Date, RetailEmployeeCsatSummary>> employeeSummaryMap, Date fromDate) {
		
		employeeCsatRepo.deleteEmpCsatSummaryForDate(fromDate);
		
		for (Integer clientId : employeeSummaryMap.keySet()) {
			
			Map<Date, RetailEmployeeCsatSummary> empCsatMap = employeeSummaryMap.get(clientId);
			
			for (Date mailDate : empCsatMap.keySet()) {
				
				RetailEmployeeCsatSummary empCsatSummary = empCsatMap.get(mailDate);
				
				employeeCsatRepo.save(empCsatSummary);
				
			}
			
		}
	
		
	}

	public Date convertToDateWithoutTime(Date mailDate) {
		
		Date dateWithNoTime = null;

		DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

		parser.setTimeZone(TimeZone.getTimeZone("UTC"));

		try {
			String stDate = parser.format(mailDate);
			dateWithNoTime = parser.parse(stDate);

		} catch (ParseException e) {
			e.printStackTrace();

		}

		return dateWithNoTime;
	}

	public List<AggregatedTone> getAggregatedTones(List<EmailHeader> lstEmailDetails) {
		
		List<String> lstMailThreadNames =null;
		 
		 if(lstEmailDetails!=null && lstEmailDetails.size()>0) {
			 
			 lstMailThreadNames  =  lstEmailDetails.stream().map(e-> e.mailThreadName).collect(Collectors.toList());
			 
		 }
		 
		 List<AggregatedTone> lstAggregatedTone = aggToneRepo.findByMailThreadNameIn(lstMailThreadNames);
		 
		 return lstAggregatedTone;
	}
	
	
	public Map<String,Integer> getAllRetailClients() {
		
		Iterable<RetailClient> lstRetailClient = retailClientRepo.findAll();
		
		Map<String,Integer> retailClientMap = new HashMap<>();
		
		for (RetailClient retailClient : lstRetailClient) {
			
			retailClientMap.put(retailClient.getDomainName(), retailClient.getAccountId());
			
		}
		
		return retailClientMap;
		
	}
	
	public RetailEmployeeCsatSummary createEmployeeCsatSummary
					(Integer accountId, String employeeEmail,EmailHeader emailHeader,Map<String,String> mailThreadToneMap) {
		
		RetailEmployeeCsatSummary employeeCsatSumm = new RetailEmployeeCsatSummary();
		
		employeeCsatSumm.setAccountId(accountId);
		employeeCsatSumm.setEmployeeEmail(employeeEmail);
		employeeCsatSumm.setStatus("Y");
		employeeCsatSumm.setTotalEmails(1);
		employeeCsatSumm.setTotalInteractions(1);
		
		String baseTone = mailThreadToneMap.get(emailHeader.getMailThreadName());
		
		if(BaseTones.NEGATIVE.name().equalsIgnoreCase(baseTone)) {
			employeeCsatSumm.setNegativeInteractions(1);
		}else {
			employeeCsatSumm.setNegativeInteractions(0);
		}
		
		int totalInteractions = employeeCsatSumm.getTotalInteractions();
		
		int negativeInteractions = employeeCsatSumm.getNegativeInteractions();
		
		double csat = ((double)(totalInteractions-negativeInteractions)/totalInteractions)*100;
		
		employeeCsatSumm.getThreadName().add(emailHeader.getMailThreadName());
		
		employeeCsatSumm.setCsat(csat);
		
		return employeeCsatSumm;
		
	}
	
	public void performSummaryCalculation(String email, Map<String,Integer>retailClientMap,
									      Map<Integer,Map<Date,RetailEmployeeCsatSummary>> employeeSummaryMap,
									      Map<String,String> mailThreadToneMap,EmailHeader emailHeader,String employeeEmail) {
		
		String domainName = email.split("@")[1];
		
		Integer clientId = retailClientMap.get(domainName);
		
		
		logger.info("domainName : "+domainName);
		
		logger.info("clientId : "+clientId);
		
		logger.info("employeeEmail : "+employeeEmail);
		
		logger.info("mailDate : "+ convertToDateWithoutTime(emailHeader.getEmailDate()));
		
		if(null!=clientId) {
			
			Map<Date,RetailEmployeeCsatSummary> mapEmpCsatSummary =  employeeSummaryMap.get(clientId);
			
			Date mailDateWithTime = emailHeader.getEmailDate();
			
			Date mailDate = convertToDateWithoutTime(mailDateWithTime);
			
			if(null==mapEmpCsatSummary) {
				
				Map<Date,RetailEmployeeCsatSummary> mapEmpCsatSummaryNew = new HashMap<>();
				
				RetailEmployeeCsatSummary employeeCsatSumm = 
								createEmployeeCsatSummary(clientId, employeeEmail, emailHeader, mailThreadToneMap);
				employeeCsatSumm.setMailDate(mailDate);
				
				mapEmpCsatSummaryNew.put(mailDate,employeeCsatSumm);
				
				employeeSummaryMap.put(clientId, mapEmpCsatSummaryNew);
				
			}else {
				
				
				if(null!=mapEmpCsatSummary.get(mailDate)) {
					
					RetailEmployeeCsatSummary employeeCsatSumm = mapEmpCsatSummary.get(mailDate);
					
					int totalMails = employeeCsatSumm.getTotalEmails();
					int totalInteractions = employeeCsatSumm.getTotalInteractions();
					
					String threadName = emailHeader.getMailThreadName();
					
					Set<String> threadNamesSet = employeeCsatSumm.getThreadName();
					
					employeeCsatSumm.setTotalEmails(totalMails+1);
					
					if(!threadNamesSet.contains(threadName)) {
						
						employeeCsatSumm.setTotalInteractions(totalInteractions+1);
						
						String baseTone = mailThreadToneMap.get(emailHeader.getMailThreadName());
						
						if(BaseTones.NEGATIVE.name().equalsIgnoreCase(baseTone)) {
							
							int negativeInteractions = employeeCsatSumm.getNegativeInteractions();
							employeeCsatSumm.setNegativeInteractions(negativeInteractions+1);
						}
					}
					
					int totalInteractionsNew = employeeCsatSumm.getTotalInteractions();
					
					int negativeInteractionsNew = employeeCsatSumm.getNegativeInteractions();
					
					double csat = ((double)(totalInteractionsNew-negativeInteractionsNew)/totalInteractionsNew)*100;
					
					employeeCsatSumm.setCsat(csat);
						
					employeeCsatSumm.getThreadName().add(threadName);
					
					mapEmpCsatSummary.put(mailDate, employeeCsatSumm);
					
					employeeSummaryMap.put(clientId, mapEmpCsatSummary);
					
				}else {
					
					RetailEmployeeCsatSummary employeeCsatSumm = 
							createEmployeeCsatSummary(clientId, email, emailHeader, mailThreadToneMap);
					
					employeeCsatSumm.setMailDate(mailDate);
					
					mapEmpCsatSummary.put(mailDate, employeeCsatSumm);
					
					employeeSummaryMap.put(clientId, mapEmpCsatSummary);
					
					
				}
			}

			
		}
		
	}
	
}
