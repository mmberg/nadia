package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;

public class ParseResults extends ArrayList<ParseResult>{

	public static final int NOMATCH=0;
	public static final int MATCH=1;
	
	private String utterance="";
	private int state=NOMATCH;
	
	public ParseResults(String utterance){
		this.utterance=utterance;
	}
	
	@Override
	public boolean add(ParseResult result){
		state=MATCH;
		return super.add(result);
	}
	
	public int getState(){
		return state;
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
