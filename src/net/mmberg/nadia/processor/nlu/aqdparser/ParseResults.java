package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;

public class ParseResults extends ArrayList<ParseResult>{

	private String utterance="";
	
	public ParseResults(String utterance){
		this.utterance=utterance;
	}
	
	
	public String getUtterance(){
		return utterance;
	}
	
	@Override
	public String toString(){
		String string="";
		for(ParseResult res : this){
			string+=res.toString()+"\r\n";
		}
		return string;
	}
}
