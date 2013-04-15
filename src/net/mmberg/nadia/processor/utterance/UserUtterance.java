package net.mmberg.nadia.processor.utterance;

import java.util.HashSet;

public class UserUtterance extends Utterance {

	private HashSet<String> features=new HashSet<String>();
	private String soda=null;
	
	public UserUtterance(String text){
		super(text);
	}
		
	public HashSet<String> getFeatures(){
		return features; //set in SodaRecognizer
	}
	
	public String getSoda(){
		return soda; //set in SodaRecognizer
	}
	
	public void setSoda(String soda){
		this.soda=soda; //set in SodaRecognizer
	}
	
	

}
