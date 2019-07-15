package com.hanogi.batch.reader;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanogi.batch.constants.ApplicationConstant;
import com.hanogi.batch.constants.ErrorCodes;
import com.hanogi.batch.exceptions.BrillerBatchConnectionException;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.utils.bo.OCMSEmailMessage;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

/**
 * This class will be contain methods to read the mails from 
 * Microsoft online exchange over cloud
 * @author mayank.agarwal
 *
 */
@Component
public class TestOffice365MailConnection {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final static String AUTHORITY = "https://login.microsoftonline.com/VRNetworks.onmicrosoft.com/";
   // private final static String CLIENT_ID = "b891df90-39b8-4c2f-bc22-3a15ad05aadb";
    
    private final static String CLIENT_ID = "c2a1b0d9-3bd5-46da-b99e-9ef4104d6a2e";
    
   // private final static String SECRET_KEY="JqH2F9xGWjyH/LQEuwp/e8ljohsDKnfcPuP4u89AYSg=";
    
    private final static String SECRET_KEY="8o6YWAAU2HLS3r7d0IvTFTb5PWT3qwkFrYOExpve9ZQ="; 
	
	
	public String getAuthenticationTokens() throws BrillerBatchConnectionException {

		String accessToken = null;

		// Fetch access tokens
		AuthenticationResult authTokenResult = authenticateAndAcquireTokens();

		if (null != authTokenResult) {

			accessToken = authTokenResult.getAccessToken();

			if (StringUtils.isBlank(accessToken)) {

				throw new BrillerBatchConnectionException("Failed to Authenticate the admin user"
									+ "and hence cannot acquire tokens.",ErrorCodes.AUTHENTICATION_ERROR);
			}
		}

		return accessToken;

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

	private void extractAndProcessMails(String accessToken) throws BrillerBatchConnectionException,BrillerBatchDataException{
		
		String messageURL = createMessageURL();
		
		HttpClient client = HttpClientBuilder.create().build();
		
		HttpGet post = new HttpGet(messageURL);
		
		try {
			post.addHeader("Accept", "application/json");
			post.addHeader("Prefer", "outlook.body-content-type=text");
			post.addHeader("Authorization", "Bearer " + accessToken);

			HttpResponse response = client.execute(post);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode()==(int)200) {
				
				
				 
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			 
				StringBuffer result = new StringBuffer();
				String line = null;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				
				System.out.println("Results Length ********************************************"+result.length());
				ObjectMapper objectMapper1 = new ObjectMapper();
				JSONObject  jsonObject1 = new JSONObject(result.toString());
				jsonObject1.getJSONArray("value");
				objectMapper1.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				
				TypeReference<List<OCMSEmailMessage>> mapType1 = new TypeReference<List<OCMSEmailMessage>>() {};
				
				List<OCMSEmailMessage> messegeList = objectMapper1.readValue(jsonObject1.getJSONArray("value").toString(), mapType1);
				
				System.out.println("Results Length ********************************************"+messegeList.size());
				
				FileWriter fileWriter = null;
				 String NEW_LINE_SEPARATOR = "\n";
                
		        try {
		            fileWriter = new FileWriter("F://dataBriller.csv");
		        
		        
				
				for (OCMSEmailMessage emailMessage : messegeList) {
					
					System.out.println("emailMessage : "+ emailMessage);
					
					fileWriter.append(emailMessage.toString());
					
					fileWriter.append(NEW_LINE_SEPARATOR);
					
				}

			} catch (Exception e) {
	            System.out.println("Error in CsvFileWriter !!!");
	            e.printStackTrace();
	        } finally {
	             
	            try {
	                fileWriter.flush();
	                fileWriter.close();
	            } catch (IOException e) {
	                System.out.println("Error while flushing/closing fileWriter !!!");
	                e.printStackTrace();
	            }
	             
	        }
				System.out.println("List OfEmployee **************************"+result.toString());
				

				
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		finally {

		}
		
	}
	

	private String createMessageURL() throws BrillerBatchDataException {
		
		StringBuffer sbURL = new StringBuffer();
		
		String folderName = WellKnownFolderName.valueOf("Inbox").name();
		
		if(StringUtils.isBlank(folderName)) {
			throw new BrillerBatchDataException("Incorrect Standard folder name. Could not"
							+ "Create Graph URL to fetch messages",ErrorCodes.INCORRECT_FOLDER_NAME);
		}

		sbURL.append(ApplicationConstant.MS_GRAPH_API_URL.getValue());
		sbURL.append("support@vrninc.com");
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_MAILFOLDERS.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(WellKnownFolderName.valueOf("Calendar"));
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_SEPARATOR.getValue());
		sbURL.append(ApplicationConstant.MS_GRAPH_URL_MESSAGES.getValue());
		//sbURL.append(ApplicationConstant.MS_GRAPH_QUERY_SEPARATOR.getValue());
		//sbURL.append(ApplicationConstant.MS_GRAPH_API_SELECT.getValue());
		
		
		/*List<String> queryParamList = new ArrayList<>();
		
		for (OCMSQueryParams queryParam : OCMSQueryParams.values()) {
			
			queryParamList.add(queryParam.name());
		}
		
		sbURL.append(String.join(",", queryParamList));*/
		
		System.out.println("Request String**************************** : " + sbURL);
		
		return sbURL.toString();
	}


	
	/**
	 * This method will be used to fetch the Authentication tokens required to read
	 * mails from the Microsoft exchange server over cloud
	 * @param username
	 * @param password
	 * @return AuthenticationResult
	 * @throws BrillerBatchConnectionException
	 */
	 private AuthenticationResult authenticateAndAcquireTokens() throws BrillerBatchConnectionException {
		 
	        AuthenticationContext context = null;
	        
	        AuthenticationResult result = null;
	        
	        ExecutorService service = null;
	        
	        try {
	            service = Executors.newFixedThreadPool(1);	
	            
	            context = new AuthenticationContext(AUTHORITY, false, service);
	            
	            ClientCredential credential = new ClientCredential(CLIENT_ID, SECRET_KEY);
	            
	            Future<AuthenticationResult> future = 
						context.acquireToken("https://graph.microsoft.com",credential,null);
	           /* 
	            Future<AuthenticationResult> future = 
	            						context.acquireToken("https://graph.microsoft.com",CLIENT_ID,"abhishekg@brillerdev.onmicrosoft.com", "Password@123",null);*/
	            
	            result = future.get(10, TimeUnit.SECONDS);
	            
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
	 
	 public static void main(String[] args) {
		 
		/*TestOffice365MailConnection objTest = new TestOffice365MailConnection();
		 try {
			 String accessToken = objTest.getAuthenticationTokens();
			 
			 
			 System.out.println("Access Token ********************************************* "+ accessToken);
			 
			 objTest.extractAndProcessMails(accessToken);
			 
		 }catch(Exception e ) {
			 e.printStackTrace();
			 
		 }
		 
		*/
		 
		 double csat = ((double)(120-67)/120)*100;
		 
		 System.out.println(csat);
			
		
	}

}
