package com.hanogi.batch.services.impl;

import java.io.FileWriter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.exceptions.BrillerBatchIOException;
import com.hanogi.batch.repositories.AggregatedToneRepositry;
import com.hanogi.batch.services.IToneAnalyser;
import com.hanogi.batch.utils.bo.EmailMessageData;
import com.hanogi.batch.utils.bo.ToneMapper;
import com.hanogi.batch.utils.bo.ToneMessage;

@Service
public class ToneAnalyser implements IToneAnalyser {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${analyser.type}")
	private String analyserType;

	@Value("${analyser.name}")
	private String analyserName;

	@Value("${analyser.url}")
	private String analyserUrl;

	@Value("${authToken}")
	private String authToken;
	
	@Value("${analyser.toneUser}")
	private String username;
	
	@Value("${analyser.tonePassword}")
	private String password;

	@Autowired
	private AggregatedToneRepositry AggregatedToneRepositry;

	@PostConstruct
	public void createToneAnalyserInstance() {
		logger.info("Tone analyser properties analyserType:" + analyserType);
		logger.info("Tone analyser properties analyserName:" + analyserName);
	}

	@Override
	public String analyseTone(String messageBody) {
		
		String tone= getToneFromCustom(messageBody);
			
		return tone;
	}
	
	@Override
	public List<List<String[]>> analyseTone(List<EmailMessageData> messageBody)
													throws BrillerBatchIOException,BrillerBatchDataException  {
		
		List<String> emailBodyList = messageBody.stream().map(EmailMessageData::getUniqueEmailBody).collect(Collectors.toList());
		
		String [] arrMailBody = emailBodyList.stream().toArray(String[]::new);
				
		List<List<String[]>> toneList = getToneFromCustom(arrMailBody);
		
		return toneList;
	}

	private String getToneFromCustom(String messageBody) {

		
		ToneMessage toneMessage = new ToneMessage(new String[] {messageBody});

		HttpHeaders headers = new HttpHeaders();

		String notEncoded = username + ":" + password;
		
		String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
		
		headers.add("Authorization", "Basic " + encodedAuth);

		// creating headers and setting authentication token
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		//headers.add("Authorization", "Bearer " + authToken);

		HttpEntity<?> httpEntity = new HttpEntity<>(toneMessage, headers);

		RestTemplate restTemplate = new RestTemplate();

		// Sending post request to rest end point of tone analyzer service
		ResponseEntity<String> res = restTemplate.exchange(analyserUrl, HttpMethod.POST, httpEntity, String.class);

		String responseBody = res.getBody();
		//boolean saveToneToDb = saveToneToDb(responseBody);

	/*	if (saveToneToDb) {
			logger.info("SAved");
		} else {
			logger.error("Failed");
		}*/
		return responseBody;
	}
	
	
	private List<List<String[]>> getToneFromCustom(String[] messageBody) 
							throws BrillerBatchIOException,BrillerBatchDataException {
		
		ToneMessage toneMessage = new ToneMessage(messageBody);

		HttpHeaders headers = new HttpHeaders();
		
		ToneMapper toneData = null;
		
		try {
			
			String notEncoded = username + ":" + password;
			
			String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
			
			headers.add("Authorization", "Basic " + encodedAuth);

			// creating headers and setting authentication token
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			HttpEntity<?> httpEntity = new HttpEntity<>(toneMessage, headers);
			logger.info("the message  body being sent here is =="+messageBody.toString());
			RestTemplate restTemplate = new RestTemplate();
			logger.info("the Header body being sent here is =="+httpEntity.toString());
			try{    
		           FileWriter fw=new FileWriter("D:\\testout.txt");  
		           FileWriter fw1=new FileWriter("D:\\testmailbody.txt");  
		           fw.write(httpEntity.toString());    
		           fw.close();
		           fw1.write(messageBody.toString());    
		           fw1.close();
		          }catch(Exception e){System.out.println(e);}    
		          System.out.println("Success...");    
		         

			// Sending post request to rest end point of tone analyzer service
			ResponseEntity<String> res = restTemplate.exchange(analyserUrl, HttpMethod.POST, httpEntity, String.class);
			String responseBody = res.getBody();
			
			String updatedResponse = responseBody.replace("\\", "");
			
			String correctJSON = updatedResponse.replace("\"{","{").replace("}\"","}");
			
			Gson gson = new Gson();

			toneData = gson.fromJson(correctJSON, ToneMapper.class);
			
			if(null==toneData.getTones() || toneData.getTones().size()!=messageBody.length) {
				
				throw new BrillerBatchDataException("Errorneous result returned from the tone analyser");
				
			}
			
			
		}catch(RestClientException e ) {
			
			throw new BrillerBatchIOException("Error while fetching the tone response from the tone analyzer",e);
			
		}
		catch(Exception e ) {
			
			throw new BrillerBatchIOException("Error while fetching the tone response from the tone analyzer",e);
			
		}
		
		
		return toneData.getTones();
	}

}
