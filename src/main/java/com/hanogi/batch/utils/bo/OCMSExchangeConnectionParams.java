package com.hanogi.batch.utils.bo;

public class OCMSExchangeConnectionParams{
	
	private String adminUserName;
	
	private String adminPassword;
	
	private String exchangeServerURL;
	
	private String clientId;

	private String secretKey;
	
	private String graphApiURL;
	
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "OCMSExchangeConnectionParams [adminUserName=" + adminUserName + ", adminPassword=" + adminPassword
				+ ", exchangeServerURL=" + exchangeServerURL + ", clientId=" + clientId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adminPassword == null) ? 0 : adminPassword.hashCode());
		result = prime * result + ((adminUserName == null) ? 0 : adminUserName.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((exchangeServerURL == null) ? 0 : exchangeServerURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OCMSExchangeConnectionParams other = (OCMSExchangeConnectionParams) obj;
		if (adminPassword == null) {
			if (other.adminPassword != null)
				return false;
		} else if (!adminPassword.equals(other.adminPassword))
			return false;
		if (adminUserName == null) {
			if (other.adminUserName != null)
				return false;
		} else if (!adminUserName.equals(other.adminUserName))
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (exchangeServerURL == null) {
			if (other.exchangeServerURL != null)
				return false;
		} else if (!exchangeServerURL.equals(other.exchangeServerURL))
			return false;
		return true;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getGraphApiURL() {
		return graphApiURL;
	}

	public void setGraphApiURL(String graphApiURL) {
		this.graphApiURL = graphApiURL;
	}

	

}
