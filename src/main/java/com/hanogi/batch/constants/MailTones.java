package com.hanogi.batch.constants;

public enum MailTones {
	
	JOY(BaseTones.POSITIVE.name()),
	HAPPY(BaseTones.POSITIVE.name()),
	AFFECTION(BaseTones.POSITIVE.name()),
	NEUTRAL(BaseTones.NEUTRAL.name()),
	WORRY(BaseTones.NEGATIVE.name()),
	SAD(BaseTones.NEGATIVE.name()),
	ANGER(BaseTones.NEGATIVE.name()),
	SADNESS(BaseTones.NEGATIVE.name()),
	FEAR(BaseTones.NEGATIVE.name());
		
	private String value; 
	  
    public String getValue() { 
        return this.value; 
    } 
  
    private MailTones(String value) { 
        this.value = value; 
    } 

}
