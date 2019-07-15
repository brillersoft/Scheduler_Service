package com.hanogi.batch.constants;

public enum ApplicationConstant {
	
	MS_GRAPH_API_URL("https://graph.microsoft.com/v1.0/users/"),
	
	MS_GRAPH_URL_MAILFOLDERS("mailFolders"),
	
	MS_GRAPH_URL_MESSAGES("messages?$top=300"),
	
	//MS_GRAPH_URL_MESSAGES("messages?$select=internetMessageHeaders"),
	
	MS_GRAPH_QUERY_SEPARATOR("&"),
	
	MS_GRAPH_API_FILTER("$filter="),
	
	MS_GRAPH_API_SELECT("$select="),
	
	MS_GRAPH_URL_SEPARATOR("/"),
	
	MS_GRAPH_RECEIVED_TIME_GREATER_THAN("ReceivedDateTime ge "),
	
	MS_GRAPH_RECEIVED_TIME_LESSER_THAN("ReceivedDateTime lt "),
	
	MS_GRAPH_AND_SEPARATOR(" and ");

		
	private String value; 
	  
    public String getValue() { 
        return this.value; 
    } 
  
    private ApplicationConstant(String value) { 
        this.value = value; 
    } 

}
