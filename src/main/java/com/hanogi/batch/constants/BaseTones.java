package com.hanogi.batch.constants;

public enum BaseTones {
	
	POSITIVE(4),
	
	NEGATIVE(-4),
	
	NEUTRAL(0);
	
	private int value; 
	  
    public int getValue() { 
        return this.value; 
    } 
  
    private BaseTones(int value) { 
        this.value = value; 
    } 
	
}
