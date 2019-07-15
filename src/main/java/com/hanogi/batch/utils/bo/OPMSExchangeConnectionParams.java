package com.hanogi.batch.utils.bo;

public class OPMSExchangeConnectionParams{
	
	private String adminUserName;
	
	private String adminPassword;
	
	private String exchangeServerURL;
	
	private String exchangeVersion;

	
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getExchangeServerURL() {
		return exchangeServerURL;
	}

	public void setExchangeServerURL(String exchangeServerURL) {
		this.exchangeServerURL = exchangeServerURL;
	}

	public String getExchangeVersion() {
		return exchangeVersion;
	}

	public void setExchangeVersion(String exchangeVersion) {
		this.exchangeVersion = exchangeVersion;
	}

	@Override
	public String toString() {
		return "OPMSExchangeConnectionParams [adminUserName=" + adminUserName + ", adminPassword=" + adminPassword
				+ ", exchangeServerURL=" + exchangeServerURL + ", exchangeVersion=" + exchangeVersion + "]";
	}

}
