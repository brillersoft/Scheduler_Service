package com.hanogi.batch.constants;

import java.util.List;

public class EmailPreference {

	private List<String> standardFolders;

	private List<String> customFolders;

	private List<String> emailsFromToFilters;

	private List<String> emailsFromToRead;

	public List<String> getStandardFolders() {
		return standardFolders;
	}

	public void setStandardFolders(List<String> standardFolders) {
		this.standardFolders = standardFolders;
	}

	public List<String> getCustomFolders() {
		return customFolders;
	}

	public void setCustomFolders(List<String> customFolders) {
		this.customFolders = customFolders;
	}

	public List<String> getEmailsFromToFilters() {
		return emailsFromToFilters;
	}

	public void setEmailsFromToFilters(List<String> emailsFromToFilters) {
		this.emailsFromToFilters = emailsFromToFilters;
	}

	public List<String> getEmailsFromToRead() {
		return emailsFromToRead;
	}

	public void setEmailsFromToRead(List<String> emailsFromToRead) {
		this.emailsFromToRead = emailsFromToRead;
	}

}
