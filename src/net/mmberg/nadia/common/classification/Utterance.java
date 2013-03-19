package net.mmberg.nadia.common.classification;

import java.util.HashSet;

//TODO merge with other Utterance Classes
public class Utterance {

	private String text;
	private HashSet<String> features=new HashSet<String>();
	private String outcome;
	
	public Utterance(String text){
		this.text=text;
	}
	
	public Utterance(String text, String outcome){
		this.text=text;
		this.outcome=outcome;
	}
	
	public HashSet<String> getFeatures(){
		return features;
	}
	
	public String getText(){
		return text;
	}
	
	public String getOutcome(){
		return outcome;
	}
}
