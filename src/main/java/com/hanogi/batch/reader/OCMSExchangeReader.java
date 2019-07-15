package com.hanogi.batch.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hanogi.batch.constants.ApplicationConstant;
import com.hanogi.batch.constants.EmailDirection;
import com.hanogi.batch.constants.EmailPreference;
import com.hanogi.batch.constants.ErrorCodes;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.constants.OCMSQueryParams;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.EmailDomainDetails;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.dto.EmailMetadata;
import com.hanogi.batch.dto.EmailPreferenceMap;
import com.hanogi.batch.dto.batch.BatchRunDetails;
import com.hanogi.batch.exceptions.BrillerBatchConnectionException;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.utils.bo.EmailMessageData;
import com.hanogi.batch.utils.bo.OCMSEmailMessage;
import com.hanogi.batch.utils.bo.OCMSExchangeConnectionParams;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import net.sf.ehcache.Cache;

/**
 * This class will be contain methods to read the mails from 
 * Microsoft online exchange over cloud
 * @author mayank.agarwal
 *
 */
@Component
public class OCMSExchangeReader implements IEmailReader {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("emailDataQueue")
	private BlockingQueue<EmailMessageData> emailDataProcessingQueue;

	@Autowired
	@Qualifier("cacheService")
	private ICacheService cacheService;

	@Value("${connectionRetryAttems}")
	private Integer maxRetryConnectionAttempt = 1;
	
	
	//@Value("${connectionTimeOutInSeconds}")
    private  long timeOutInSeconds = 60l;

	
	private Map<OCMSExchangeConnectionParams,AuthenticationResult> accessTokenCache = new ConcurrentHashMap<>();
	
	
	public String getAuthenticationTokens(OCMSExchangeConnectionParams connectionParams) 
														throws BrillerBatchConnectionException {

		String accessToken = null;

		// Fetch access tokens
		AuthenticationResult authTokenResult = authenticateAndAcquireTokens(connectionParams);

		if (null != authTokenResult) {

			accessToken = authTokenResult.getAccessToken();

			if (StringUtils.isBlank(accessToken)) {

				throw new BrillerBatchConnectionException("Failed to Authenticate the admin user"
									+ "and hence cannot acquire tokens.",ErrorCodes.AUTHENTICATION_ERROR);
			}
		}
		
		accessTokenCache.put(connectionParams, authTokenResult);

		return accessToken;

	}
	
	@Async("batchProcessorThreadPool")
	public void readMail(Email email, BatchRunDetails batchRunDetails,
									  Map<String, ExecutionStatusEnum> emailProcessingStatusMap, Cache cache) {
		try {
			
			// Load Connection Configurations
			OCMSExchangeConnectionParams connectionParams = populateConnectionParams(email.getObjEmailDomainDetails());
			
			AuthenticationResult authTokenResult = accessTokenCache.get(connectionParams);
			
			String accessToken = null;
			
			if(null==authTokenResult) {
				
				accessToken = getAuthenticationTokens(connectionParams);
				
			}else {
				
				accessToken = authTokenResult.getAccessToken();
				
				if(isAuthDataExpired(authTokenResult)) {
					
					accessToken = getAuthenticationTokens(connectionParams);
					
				}
			}
			
			processEmails(email, batchRunDetails, emailProcessingStatusMap,accessToken, cache);
			
		}catch (BrillerBatchConnectionException  e) {

			log.error("Connection Error - email address :" + email.getEmailId() + "marked email processing as failed due to" + e.getMessage());
			
			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.failure);

		}
		
	}
	
	private boolean isAuthDataExpired(AuthenticationResult authData) {
		
        return authData.getExpiresOnDate().before(new Date()) ? true : false;
        
    }

	public void processEmails(Email email, BatchRunDetails batchRunDetails,
									  Map<String, ExecutionStatusEnum> emailProcessingStatusMap, String accessToken, Cache cache) {

		try {

			if (null != email.getObjEmailPreferences()) {

				EmailPreference emailPreferences = getMailPreferences(email);

				// Reading the standard folders
				if (null != emailPreferences.getStandardFolders() & emailPreferences.getStandardFolders().size() > 0) {

					List<String> standardFolders = emailPreferences.getStandardFolders();

					for (String folder : standardFolders) {

						extractAndProcessMails(email, batchRunDetails,accessToken,folder,cache);
					}

				}

				// Reading the custom folders
				if (null != emailPreferences.getCustomFolders() & emailPreferences.getCustomFolders().size() > 0) {

					List<String> customFolders = emailPreferences.getCustomFolders();

					for (String folder : customFolders) {

						extractAndProcessMails(email, batchRunDetails,accessToken,folder,cache);
					}
				}
			}

			log.info(email.getEmailId() + ":mailId execution completed");

			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.Complete);

		} catch (BrillerBatchConnectionException  | BrillerBatchDataException e) {

			log.error("Connection Error - email address :" + email.getEmailId() + "marked email processing as failed due to" + e.getMessage());
			
			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.failure);

		}

	}
	
	/**
	 * Method to read the connection parameters for the Microsoft Exchange
	 * server from the Database 
	 * @param objEmailDomainDetails
	 * @return OCMSExchangeConnectionParams
	 * @throws BrillerBatchConnectionException
	 */
	
	private OCMSExchangeConnectionParams populateConnectionParams(EmailDomainDetails objEmailDomainDetails)
			throws BrillerBatchConnectionException {

		Gson g = new Gson();

		OCMSExchangeConnectionParams connectionParams = null;

		if (StringUtils.isNotBlank(objEmailDomainDetails.getEmailServerConfig())) {

			connectionParams = g.fromJson(objEmailDomainDetails.getEmailServerConfig(),OCMSExchangeConnectionParams.class);
			
		} else {
			throw new BrillerBatchConnectionException("Missing Mail Server Connection Parameters for email Domain : "
															+ objEmailDomainDetails,ErrorCodes.MISSING_CONNECTION_PARAMETERS);
		}

		return connectionParams;
	}


	
	
	/**
	 * Method to extract the details of the mail after reading from exchange server.
	 * This method will also convert the mail data in to standard format that will be
	 * understood by the batch process
	 * @param folderId
	 * @param email
	 * @param exchangeService
	 * @param batchRunDetails
	 * @param folder2 
	 * @param emailPreferences
	 * @throws Exception
	 */

	private void extractAndProcessMails(Email email,BatchRunDetails batchRunDetails,
										String accessToken, String folder, Cache cache) throws BrillerBatchConnectionException,
										BrillerBatchDataException{
		
		String messageURL = createMessageURL(email,batchRunDetails,folder);
		
		HttpClient client = HttpClientBuilder.create().build();
		
		HttpGet post = new HttpGet(messageURL);
		
		try {
			post.addHeader("Accept", "application/json");
			post.addHeader("Prefer", "outlook.body-content-type=text");
			post.addHeader("Authorization", "Bearer " + accessToken);

			HttpResponse response = client.execute(post);

			log.info("Response Code : " + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode()==(int)200) {
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			 
				StringBuffer result = new StringBuffer();
				String line = null;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				
				
				ObjectMapper objectMapper1 = new ObjectMapper();
				JSONObject  jsonObject1 = new JSONObject(result.toString());
				jsonObject1.getJSONArray("value");
				objectMapper1.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				
				TypeReference<List<OCMSEmailMessage>> mapType1 = new TypeReference<List<OCMSEmailMessage>>() {};
				
				List<OCMSEmailMessage> messegeList = objectMapper1.readValue(jsonObject1.getJSONArray("value").toString(), mapType1);
				
				
				for (OCMSEmailMessage emailMessage : messegeList) {
					
					EmailMessageData emailMessageData = new EmailMessageData();
					
					emailMessageData.setEmailBody(emailMessage.getBody().toString());
					emailMessageData.setUniqueEmailBody(emailMessage.getUniqueBody().getContent());
					emailMessageData.setEmailDomainName(email.getObjEmailDomainDetails().getEmailDomainName());

					EmailMetadata emailMetaData =
							extractAndSetEmailMetadata(emailMessage, folder,batchRunDetails);

					emailMessageData.setEmailMetaData(emailMetaData);
					
					// Update the Cache with the EmailMessage if not already present
					String MessageId = emailMessageData.getEmailMetaData().getEmailHeader().getMessageId();

					boolean doNotExists = 
						cacheService.checkAddUpdateCache(MessageId,ExecutionStatusEnum.Inprogress, cache);

					if (!doNotExists) {
						try {
							emailDataProcessingQueue.put(emailMessageData);
							
						} catch (InterruptedException e) {
							
							log.error("Error while add the mail message to the procesing Queue : "+ emailMessageData.getMessageDataId(), e);
							
						}
					}
				}
				
				System.out.println("Complete All the Emails for the mails****************************************************:");
				
			}
		} catch (IOException e) {
			
			log.error("Error while making connection with exchange server : ", e);

			throw new BrillerBatchConnectionException(
					"Error while making connection with exchange server ",ErrorCodes.SERVER_CONNECTION_ERROR);
			
			
		} catch (JSONException e) {
			
			log.error("Error while pasring the response to JSON object : ", e);

			throw new BrillerBatchDataException(
					"Error while pasring the response to JSON object ",ErrorCodes.INCORRECT_RESPONSE);
			
		}
		
	}
	

	private String createMessageURL(Email email, BatchRunDetails batchRunDetails,
										String folder) throws BrillerBatchDataException {
		
		StringBuffer sbURL = new StringBuffer();
		
		String folderName = WellKnownFolderName.valueOf(folder).name();
		
		if(StringUtils.isBlank(folderName)) {
			throw new BrillerBatchDataException("Incorrect Standard folder name. Could not"
							+ "Create Graph URL to fetch messages",ErrorCodes.INCORRECT_FOLDER_NAME);
		}
		
		sbURL.append(ApplicationConstant.MS_GRAPH_API_URL.getValue());
		sbURL.append(email.getEmailId());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_MAILFOLDERS.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(folderName);
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_MESSAGES.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_QUERY_SEPARATOR.getValue());
		//sbURL.append(ApplicationConstant.MS_GRAPH_API_FILTER.getValue());
		//sbURL.append(ApplicationConstant.MS_GRAPH_RECEIVED_TIME_GREATER_THAN.getValue());
		//sbURL.append("2018-09-30");
		//sbURL.append(ApplicationConstant.MS_GRAPH_AND_SEPARATOR.getValue());
		//sbURL.append(ApplicationConstant.MS_GRAPH_RECEIVED_TIME_LESSER_THAN.getValue());
		//sbURL.append(batchRunDetails.getToDate());
		//sbURL.append("2019-03-30");
		//$filter=ReceivedDateTime ge 2017-04-01 and receivedDateTime lt 2017-05-01
		//sbURL.append(ApplicationConstant.MS_GRAPH_QUERY_SEPARATOR.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_API_SELECT.getValue());
		
		
		List<String> queryParamList = new ArrayList<>();
		
		for (OCMSQueryParams queryParam : OCMSQueryParams.values()) {
			
			queryParamList.add(queryParam.name());
		}
		
		sbURL.append(String.join(",", queryParamList));
		
		
		return sbURL.toString();
	}


	/**
	 * Method to set the email meta data from the email data fetched from
	 * Microsoft exchange server
	 * @param emailMessage
	 * @param folderName
	 * @param batchRunDetails
	 * @return
	 */
	public EmailMetadata extractAndSetEmailMetadata(OCMSEmailMessage emailMessage ,
										String folderName, BatchRunDetails batchRunDetails)  {

		// Setting up Meta data
		EmailMetadata emailMetaData = new EmailMetadata();

		emailMetaData.setBatchRunDetails(batchRunDetails.getBatchRunId());
		emailMetaData.setFromEmailId(emailMessage.getSender().getEmailAddress().getAddress());
		emailMetaData.setEmailProcessingExecutionStatus(ExecutionStatusEnum.Inprogress.name());

		if (folderName.equalsIgnoreCase(WellKnownFolderName.SentItems.name())) {
			emailMetaData.setEmailDirection(EmailDirection.Sent.name());
		} else {
			emailMetaData.setEmailDirection(EmailDirection.Received.name());
		}

		EmailHeader emailHeader = extractAndSetEmailHeader(emailMessage, folderName);

		emailMetaData.setEmailHeader(emailHeader);

		return emailMetaData;
	}
	
	/**
	 * This method will extract the data from email message object & set the required
	 * data in the email header object
	 * @param emailMessage
	 * @param folderName
	 * @return
	 */
	public EmailHeader extractAndSetEmailHeader(OCMSEmailMessage emailMessage ,String folderName)  {

		EmailHeader emailHeader = new EmailHeader();

		if (null != emailMessage.getCcRecipients()) {

			String ccEmailAddresses = String.join(",", emailMessage.getCcRecipients().stream()
					.map(eAdd -> eAdd.getEmailAddress().getAddress()).collect(Collectors.toList()));

			emailHeader.setCcEmailId(ccEmailAddresses);
		}

		if (folderName.equalsIgnoreCase(WellKnownFolderName.SentItems.name())) {

			Date emailDate = convertStringToDate(emailMessage.getSentDateTime());

			emailHeader.setEmailDate(emailDate);
			
			emailHeader.setEmailDirection(EmailDirection.Sent.name());
		} else {
			Date emailDate = convertStringToDate(emailMessage.getReceivedDateTime());

			emailHeader.setEmailDate(emailDate);
			
			emailHeader.setEmailDirection(EmailDirection.Received.name());
		}

		emailHeader.setMessageId(emailMessage.getInternetMessageId());

		emailHeader.setMailThreadName(emailMessage.getConversationId());

		emailHeader.setSubject(emailMessage.getSubject());

		if (null != emailMessage.getToRecipients()) {

			String toEmailAddresses = String.join(",", emailMessage.getToRecipients().stream()
					.map(eAdd -> eAdd.getEmailAddress().getAddress()).collect(Collectors.toList()));

			emailHeader.setToEmailId(toEmailAddresses);
		}
		
		emailHeader.setFromEmailId(emailMessage.getSender().getEmailAddress().getAddress());
		
		emailHeader.setEmailText(emailMessage.getUniqueBody().getContent());

		return emailHeader;
	}

	private Date convertStringToDate(String dateTime) {
		
		DateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed=null;
		try {
			parsed = parser.parse(dateTime);
		} catch (ParseException e) {
			log.error("Error while parsing the date : "+e);

		}
        return parsed;
	}


	/**
	 * Method to get the date range filter criteria for filtering the emails
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public SearchFilter emailFilterCriteriaDateRange(Date fromDate, Date toDate) {

		SearchFilter searchFromFilter = new SearchFilter.IsGreaterThanOrEqualTo(ItemSchema.DateTimeReceived, fromDate);

		SearchFilter searchToFilter = new SearchFilter.IsLessThanOrEqualTo(ItemSchema.DateTimeReceived, toDate);

		SearchFilter unreadFilter = new SearchFilter.SearchFilterCollection(LogicalOperator.And, searchFromFilter,
				searchToFilter);

		return unreadFilter;
	}

	
	private EmailPreference getMailPreferences(Email email) {
		
		EmailPreference emailPreferences = null;
		
		Gson gson = new Gson();
		
		EmailPreferenceMap emailPreferencesMap = email.getObjEmailPreferences();

		try {
			log.info("Getting mail preferences");

			if (emailPreferencesMap != null) {

				emailPreferences = gson.fromJson(emailPreferencesMap.getEmailPreferencesJson(), EmailPreference.class);
			}

		} catch (Exception e) {
			
			log.error("Email preferences string is not as expected with error:" + e.getMessage());

		}
		return emailPreferences;
	}
	
	/**
	 * This method will be used to fetch the Authentication tokens required to read
	 * mails from the Microsoft exchange server over cloud
	 * @param username
	 * @param password
	 * @return AuthenticationResult
	 * @throws BrillerBatchConnectionException
	 */
	 private AuthenticationResult authenticateAndAcquireTokens(OCMSExchangeConnectionParams connectionParams) 
			 															throws BrillerBatchConnectionException {
		 
	        AuthenticationContext context = null;
	        
	        AuthenticationResult result = null;
	        
	        ExecutorService service = null;
	        
	        try {
	            service = Executors.newFixedThreadPool(1);	
	            
	            context = new AuthenticationContext(connectionParams.getExchangeServerURL(), false, service);
	            
	            ClientCredential credential = 
	            			new ClientCredential(connectionParams.getClientId(), connectionParams.getSecretKey());
	            
	            Future<AuthenticationResult> future = 
								context.acquireToken(connectionParams.getGraphApiURL(),credential,null);
	            
	            result = future.get(timeOutInSeconds, TimeUnit.SECONDS);
	            
	            if (null==result) {
		            throw new BrillerBatchConnectionException("Failed to Authenticate the admin user "
		            								+ "and hence cannot acquire tokens.",ErrorCodes.AUTHENTICATION_ERROR);
		            
		        }
	            
	        }catch(MalformedURLException mu) {
	        	
	        	throw new BrillerBatchConnectionException("Incorrect URL for the Exchange Server. "
	        								+ "Could not acquire access tokens",mu,ErrorCodes.INCORRECT_CONNECTION_URL);
	        }
	        catch(InterruptedException | ExecutionException ie) {
	        	
	        	throw new BrillerBatchConnectionException("Error while fetching the access tokens."
	        										+ "Could not acquire access tokens",ie,ErrorCodes.AUTHENTICATION_ERROR);
	        } catch (TimeoutException te) {
	        	
	        	throw new BrillerBatchConnectionException("Error!!! Timed out while fetching the access tokens."
													+ "Could not acquire access tokens",te,ErrorCodes.REQUEST_TIME_OUT_ERROR);
			}
	        finally {
	        	
	            service.shutdown();
	        }

	        return result;
	    }
}
