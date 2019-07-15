package com.hanogi.batch.utils.bo;

import com.hanogi.batch.dto.EmailMetadata;

public class EmailMessageData {

	private Integer messageDataId;

	private String emailBody;

	private String uniqueEmailBody;

	private EmailMetadata emailMetaData;
	
	private String emailDomainName;

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getUniqueEmailBody() {
		return uniqueEmailBody;
	}

	public void setUniqueEmailBody(String uniqueEmailBody) {
		this.uniqueEmailBody = uniqueEmailBody;
	}

	public EmailMetadata getEmailMetaData() {
		return emailMetaData;
	}

	public void setEmailMetaData(EmailMetadata emailMetaData) {
		this.emailMetaData = emailMetaData;
	}

	@Override
	public String toString() {
		return "EmailMessage [emailBody=" + emailBody + ", uniqueEmailBody=" + uniqueEmailBody + ", emailMetaData="
				+ emailMetaData + "]";
	}

	public Integer getMessageDataId() {
		return messageDataId;
	}

	public void setMessageDataId(Integer messageDataId) {
		this.messageDataId = messageDataId;
	}

	public String getEmailDomainName() {
		return emailDomainName;
	}

	public void setEmailDomainName(String emailDomainName) {
		this.emailDomainName = emailDomainName;
	}

}
