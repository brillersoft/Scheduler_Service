package com.hanogi.batch.utils.bo;

import java.util.List;

public class OCMSEmailMessage {
	
	private String id;
	private String createdDateTime;
	private String receivedDateTime;
	private String sentDateTime;
	private String subject;
	
	private String lastModifiedDateTime;
	private Content body;
	private Content uniqueBody;
	private String internetMessageId;
	private String conversationId;
	private Sender sender;
	private List<Sender> toRecipients; 
	private List<Sender> ccRecipients;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public String getReceivedDateTime() {
		return receivedDateTime;
	}
	public void setReceivedDateTime(String receivedDateTime) {
		this.receivedDateTime = receivedDateTime;
	}
	public String getSentDateTime() {
		return sentDateTime;
	}
	public void setSentDateTime(String sentDateTime) {
		this.sentDateTime = sentDateTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}
	public void setLastModifiedDateTime(String lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

	
	
	public Content getBody() {
		return body;
	}
	public void setBody(Content body) {
		this.body = body;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public List<Sender> getToRecipients() {
		return toRecipients;
	}
	public void setToRecipients(List<Sender> toRecipients) {
		this.toRecipients = toRecipients;
	}
	public List<Sender> getCcRecipients() {
		return ccRecipients;
	}
	public void setCcRecipients(List<Sender> ccRecipients) {
		this.ccRecipients = ccRecipients;
	}
	public Content getUniqueBody() {
		return uniqueBody;
	}
	public void setUniqueBody(Content uniqueBody) {
		this.uniqueBody = uniqueBody;
	}
	public String getInternetMessageId() {
		return internetMessageId;
	}
	public void setInternetMessageId(String internetMessageId) {
		this.internetMessageId = internetMessageId;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	@Override
	public String toString() {
		return "OCMSEmailMessage [id=" + id + ", createdDateTime=" + createdDateTime + ", receivedDateTime="
				+ receivedDateTime + ", sentDateTime=" + sentDateTime + ", subject=" + subject
				+ ", lastModifiedDateTime=" + lastModifiedDateTime + ", body=" + body + ", uniqueBody=" + uniqueBody
				+ ", internetMessageId=" + internetMessageId + ", conversationId=" + conversationId + ", sender="
				+ sender + ", toRecipients=" + toRecipients + ", ccRecipients=" + ccRecipients + "]";
	}
	
	
	
	

}
