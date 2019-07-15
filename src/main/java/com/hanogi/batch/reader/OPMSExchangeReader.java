package com.hanogi.batch.reader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.hanogi.batch.constants.EmailDirection;
import com.hanogi.batch.constants.EmailPreference;
import com.hanogi.batch.constants.ErrorCodes;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.dto.Email;
import com.hanogi.batch.dto.EmailDomainDetails;
import com.hanogi.batch.dto.EmailHeader;
import com.hanogi.batch.dto.EmailMetadata;
import com.hanogi.batch.dto.EmailPreferenceMap;
import com.hanogi.batch.dto.batch.BatchRunDetails;
import com.hanogi.batch.exceptions.BrillerBatchConnectionException;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.exceptions.BrillerBatchIOException;
import com.hanogi.batch.repositories.EmailPreferenceMapRepositry;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.utils.bo.EmailMessageData;
import com.hanogi.batch.utils.bo.OPMSExchangeConnectionParams;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import net.sf.ehcache.Cache;

@Component
public class OPMSExchangeReader implements IEmailReader {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private Calendar calendar = Calendar.getInstance();

	@Autowired
	@Qualifier("emailDataQueue")
	private BlockingQueue<EmailMessageData> emailDataProcessingQueue;

	@Autowired
	private EmailPreferenceMapRepositry emailPreferenceMapRepo;

	@Autowired
	@Qualifier("cacheService")
	private ICacheService cacheService;

	@Value("${connectionRetryAttems}")
	private Integer maxRetryConnectionAttempt = 1;

	@Async("batchProcessorThreadPool")
	public void readMail(Email email, BatchRunDetails batchRunDetails,
			Map<String, ExecutionStatusEnum> emailProcessingStatusMap, Cache cache) throws Exception {

		try {

			// Load Connection Configurations
			OPMSExchangeConnectionParams connectionParams = populateConnectionParams(email.getObjEmailDomainDetails());

			// Create connection
			ExchangeService exchangeService = createExchangeConnection(connectionParams);

			Mailbox userMailbox = new Mailbox(email.getEmailId());

			if (null != email.getObjEmailPreferences()) {

				EmailPreference emailPreferences = getMailPreferences(email);

				// Reading the standard folders
				if (null != emailPreferences.getStandardFolders() & emailPreferences.getStandardFolders().size() > 0) {

					List<String> standardFolders = emailPreferences.getStandardFolders();

					for (String folder : standardFolders) {

						FolderId folderId = new FolderId(WellKnownFolderName.valueOf(folder), userMailbox);

						processEmails(folderId, email, exchangeService, batchRunDetails, emailPreferences, cache);

						log.warn("Processing complete for:" + folder + ",at:" + calendar.getTimeInMillis());
					}

					log.warn("Finished all standard folders.");

				}

				// Reading the custom folders

				if (null != emailPreferences.getCustomFolders() & emailPreferences.getCustomFolders().size() > 0) {

					List<String> customFolders = emailPreferences.getCustomFolders();

					for (String folder : customFolders) {

						Folder customFolder = getFolderFromPath(folder, exchangeService, userMailbox);

						processEmails(customFolder.getId(), email, exchangeService, batchRunDetails, emailPreferences,
								cache);

						log.warn("Processing complete for:" + folder + ",at:" + calendar.getTimeInMillis());
					}
				}
			}

			log.warn("Processing complete for:" + email.getEmailId() + ",at:" + calendar.getTimeInMillis());

			log.info(email.getEmailId() + ":mailId execution completed");

			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.Complete);

		} catch (BrillerBatchConnectionException | BrillerBatchIOException e) {
			log.error("Connection Error - email address :" + email.getEmailId()
					+ "marked email processing as failed due to" + e.getMessage());

			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.failure);

		} catch (Exception e) {
			log.error("Error dew to:" + e.getMessage());
			emailProcessingStatusMap.put(email.getEmailId(), ExecutionStatusEnum.failure);

		}
	}

	/**
	 * Method to read the connection parameters for the Microsoft Exchange server
	 * from the Database
	 * 
	 * @param objEmailDomainDetails
	 * @return
	 * @throws BrillerBatchConnectionException
	 */
	private OPMSExchangeConnectionParams populateConnectionParams(EmailDomainDetails objEmailDomainDetails)
			throws Exception {

		Gson g = new Gson();

		OPMSExchangeConnectionParams connectionParams = null;

		if (StringUtils.isNotBlank(objEmailDomainDetails.getEmailServerConfig())) {

			connectionParams = g.fromJson(objEmailDomainDetails.getEmailServerConfig(),
					OPMSExchangeConnectionParams.class);
		} else {
			throw new BrillerBatchConnectionException(
					"Missing Mail Server Connection Parameters for email Domain : " + objEmailDomainDetails,
					ErrorCodes.MISSING_CONNECTION_PARAMETERS);
		}

		return connectionParams;
	}

	/**
	 * Method to create connection with on-premise Microsoft exchange server
	 * 
	 * @param connectionParams
	 * @return
	 * @throws Exception
	 */
	private ExchangeService createExchangeConnection(OPMSExchangeConnectionParams connectionParams)
			throws BrillerBatchConnectionException {

		ExchangeService exchangeService = null;

		try {

			exchangeService = new ExchangeService(ExchangeVersion.valueOf(connectionParams.getExchangeVersion()));

			exchangeService.setUrl(new URI(connectionParams.getExchangeServerURL()));

			ExchangeCredentials credentials = new WebCredentials(connectionParams.getAdminUserName(),
					connectionParams.getAdminPassword());

			exchangeService.setCredentials(credentials);

		} catch (URISyntaxException e) {

			log.error("Error while making connection with exchange server ( Incorrect URL ): ", e);

			throw new BrillerBatchConnectionException(
					"Error while making connection with exchange server ( Incorrect URL )",
					ErrorCodes.INCORRECT_CONNECTION_URL);
		}

		return exchangeService;
	}

	private void processEmails(FolderId folderId, Email email, ExchangeService exchangeService,
			BatchRunDetails batchRunDetails, EmailPreference emailPreferences, Cache cache)
			throws BrillerBatchConnectionException {

		log.info("Processing mail(s) for mailId:" + email.getEmailId());

		log.warn(email.getEmailId() + ":: Email processing start time:" + calendar.getTimeInMillis());

		List<String> emailsFromToRead = emailPreferences.getEmailsFromToRead();

		// Start reading mails
		SearchFilter unreadFilter = emailFilterCriteriaDateRange(batchRunDetails.getFromDate(),
				batchRunDetails.getToDate());

		if (unreadFilter != null) {

			if (null != folderId) {

				String folderName = folderId.getFolderName().name();

				log.info("processing mail(s) for folder name:" + folderName);

				log.warn("Processing for folder name:" + folderName + ",at:" + calendar.getTimeInMillis());

				ItemView view = new ItemView(50);

				FindItemsResults<Item> results = readFolderItemsFromExchangeServer(folderId, unreadFilter, view,
						exchangeService);

				log.info("Total no of mails count : " + results.getTotalCount() + " for the email id : "
						+ email.getEmailId());

				for (Item item : results.getItems()) {

					int noOfTriedCOunt = 0;

					boolean continueReadingMails = true;

					while (continueReadingMails) {

						try {

							noOfTriedCOunt++;

							PropertySet psPropset = new PropertySet(EmailMessageSchema.UniqueBody);
							psPropset.setRequestedBodyType(BodyType.Text);
							psPropset.setBasePropertySet(BasePropertySet.FirstClassProperties);

							EmailMessage emailMessage = EmailMessage.bind(exchangeService, item.getId(), psPropset);

							String senderMailAddress = emailMessage.getSender().getAddress();

							EmailMessageData emailMessageData = new EmailMessageData();

							if (!folderName.equalsIgnoreCase(WellKnownFolderName.SentItems.name())) {

								boolean filterEmails = emailsFromToRead.stream()
										.anyMatch(e -> e.equalsIgnoreCase(senderMailAddress));

								// Filter the email from the sender that is not mentioned in
								// the FromEmailToRead list
								if (filterEmails) {
									continue;
								}
							}

							emailMessageData.setEmailBody(emailMessage.getBody().toString());
							emailMessageData.setUniqueEmailBody(emailMessage.getUniqueBody().getText());

							EmailMetadata emailMetaData = extractAndSetEmailMetadata(emailMessage, folderName,
									batchRunDetails);

							emailMessageData.setEmailMetaData(emailMetaData);

							// Update the Cache with the EmailMessage if not already present
							String MessageId = emailMessageData.getEmailMetaData().getEmailHeader().getMessageId();

							boolean doNotExists = 
								cacheService.checkAddUpdateCache(MessageId,ExecutionStatusEnum.Inprogress, cache);

							if (!doNotExists) {
								emailDataProcessingQueue.put(emailMessageData);
							}

						} catch (BrillerBatchDataException bde) {

							continueReadingMails = false;

							log.error("Error while processing email data : " + bde.getMessage());

						} catch (Exception e) {

							log.error("Error fetching the mail details from the exchange server : " + e.getMessage());

							if (noOfTriedCOunt > maxRetryConnectionAttempt) {

								continueReadingMails = false;

								log.error("Exceeded maximum no. of retry attempts & hence ignoring this email ");
							}

						}

					}

				}

			}
		}
	}

	/**
	 * This method will extract the data from email message object & set the
	 * required data in the email header object
	 * 
	 * @param emailMessage
	 * @param folderName
	 * @return
	 * @throws BrillerBatchDataException
	 */
	public EmailHeader extractAndSetEmailHeader(EmailMessage emailMessage, String folderName)
			throws BrillerBatchDataException {

		EmailHeader emailHeader = new EmailHeader();

		try {

			if (null != emailMessage.getCcRecipients()) {

				String ccEmailAddresses = String.join(",", emailMessage.getCcRecipients().getItems().stream()
						.map(eAdd -> eAdd.getAddress()).collect(Collectors.toList()));

				emailHeader.setCcEmailId(ccEmailAddresses);
			}

			if (folderName.equalsIgnoreCase(WellKnownFolderName.SentItems.name())) {
				emailHeader.setEmailDate(emailMessage.getDateTimeSent());
			} else {
				emailHeader.setEmailDate(emailMessage.getDateTimeReceived());
			}

			emailHeader.setImportance(emailMessage.getImportance().name());

			emailHeader.setInReplyTo(emailMessage.getInReplyTo());

			emailHeader.setMessageId(emailMessage.getInternetMessageId());

			emailHeader.setReferenceMessageId(emailMessage.getReferences());

			emailHeader.setSubject(emailMessage.getSubject());

			if (null != emailMessage.getToRecipients()) {

				String toEmailAddresses = String.join(",", emailMessage.getToRecipients().getItems().stream()
						.map(eAdd -> eAdd.getAddress()).collect(Collectors.toList()));

				emailHeader.setToEmailId(toEmailAddresses);
			}

		} catch (ServiceLocalException sle) {

			emailHeader = null;

			throw new BrillerBatchDataException("Error while extracting the Header data from mail", sle,
					ErrorCodes.HEADER_DATA_ERROR);

		}

		return emailHeader;
	}

	/**
	 * Method to get the date range filter criteria for filtering the emails
	 * 
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

	/**
	 * Method to read the custom folder path.
	 * 
	 * @param FolderPath
	 * @param exchangeService
	 * @param userMailbox
	 * @return
	 * @throws Exception
	 */
	public Folder getFolderFromPath(String FolderPath, ExchangeService exchangeService, Mailbox userMailbox)
			throws BrillerBatchIOException, BrillerBatchConnectionException {

		FolderId folderid = new FolderId(WellKnownFolderName.MsgFolderRoot, userMailbox);

		try {

			Folder tfTargetFolder = Folder.bind(exchangeService, folderid);

			PropertySet psPropset = new PropertySet(BasePropertySet.FirstClassProperties);

			String[] fldArray = FolderPath.split("#");

			for (int lint = 0; lint < fldArray.length; lint++) {

				FolderView fvFolderView = new FolderView(1);

				try {
					fvFolderView.setPropertySet(psPropset);

					SearchFilter SfSearchFilter = new SearchFilter.IsEqualTo(FolderSchema.DisplayName, fldArray[lint]);

					FindFoldersResults findFolderResults = exchangeService.findFolders(tfTargetFolder.getId(),
							SfSearchFilter, fvFolderView);

					if (findFolderResults.getTotalCount() > 0) {

						for (Folder folder : findFolderResults.getFolders()) {
							tfTargetFolder = folder;
						}
					} else {
						tfTargetFolder = null;
						break;
					}

				} catch (Exception e) {

					log.error(
							"Could not find the folder : " + fldArray[lint] + " Incorrect folder path : " + FolderPath);

					throw new BrillerBatchIOException(
							"Could not find the folder : " + fldArray[lint] + " Incorrect folder path : " + FolderPath);

				}

			}
			if (tfTargetFolder != null) {
				return tfTargetFolder;
			} else {
				throw new BrillerBatchIOException("Folder Not found : " + FolderPath);
			}

		} catch (Exception e) {

			log.error("Error while connecting the exchnage server and getting details for the mailbox : "
					+ userMailbox.getAddress());

			throw new BrillerBatchConnectionException(
					"Error while connecting the exchnage server and getting details for the mailbox : "
							+ userMailbox.getAddress(),
					e, ErrorCodes.SERVER_CONNECTION_ERROR);
		}

	}

	private EmailPreference getMailPreferences(Email email) {

		EmailPreference emailPreferences = null;

		Gson gson = new Gson();

		EmailPreferenceMap emailPreferencesMap = email.getObjEmailPreferences();

		try {
			log.info("Getting mail preferences......");

			if (emailPreferencesMap != null) {

				emailPreferences = gson.fromJson(emailPreferencesMap.getEmailPreferencesJson(), EmailPreference.class);
			}

		} catch (Exception e) {

			log.error("Email preferences string is not as expected with error:" + e.getMessage());

		}
		return emailPreferences;
	}

	public FindItemsResults<Item> readFolderItemsFromExchangeServer(FolderId folderId, SearchFilter unreadFilter,
			ItemView view, ExchangeService exchangeService) throws BrillerBatchConnectionException {

		log.info("In the method readFolderItemsFromExchangeServer ");

		boolean tryConnectionAttempt = true;

		FindItemsResults<Item> results = null;

		int noOfTriedCOunt = 0;

		while (tryConnectionAttempt) {

			try {

				noOfTriedCOunt++;

				log.info(noOfTriedCOunt + " attempt to connect to exchnage server fopr the folder :  "
						+ folderId.getFolderName());

				results = exchangeService.findItems(folderId, unreadFilter, view);

				log.info("Connection Successfull " + folderId.getFolderName());

				tryConnectionAttempt = false;

			} catch (Exception e) {

				log.error("Error connectinng & getting mail data the exchange server : " + e.getMessage());

				if (noOfTriedCOunt > maxRetryConnectionAttempt) {

					log.error("Exceeded maximum no. of retry attempts for exchange server connection");

					tryConnectionAttempt = false;

					throw new BrillerBatchConnectionException(
							" Error connecting & getting mail data the exchange server", e,
							ErrorCodes.SERVER_CONNECTION_ERROR);

				}
			}
		}
		return results;
	}

	/**
	 * Method to set the email meta data from the email data fetched from Microsoft
	 * exchange server
	 * 
	 * @param emailMessage
	 * @param folderName
	 * @param batchRunDetails
	 * @return
	 * @throws BrillerBatchDataException
	 */
	public EmailMetadata extractAndSetEmailMetadata(EmailMessage emailMessage, String folderName,
			BatchRunDetails batchRunDetails) throws BrillerBatchDataException {

		// Setting up Meta data
		EmailMetadata emailMetaData = new EmailMetadata();
		try {

			emailMetaData.setBatchRunDetails(batchRunDetails.getBatchRunId());
			emailMetaData.setFromEmailId(emailMessage.getSender().getAddress());
			emailMetaData.setEmailProcessingExecutionStatus(ExecutionStatusEnum.Inprogress.name());

			if (folderName.equalsIgnoreCase(WellKnownFolderName.SentItems.name())) {
				emailMetaData.setEmailDirection(EmailDirection.Sent.name());
			} else {
				emailMetaData.setEmailDirection(EmailDirection.Received.name());
			}

			EmailHeader emailHeader = extractAndSetEmailHeader(emailMessage, folderName);

			emailMetaData.setEmailHeader(emailHeader);

		} catch (ServiceLocalException sle) {

			emailMetaData = null;

			throw new BrillerBatchDataException("Error while extracting the Email metadata", sle,
					ErrorCodes.EMAIL_METADATA_ERROR);

		}

		return emailMetaData;
	}

}
