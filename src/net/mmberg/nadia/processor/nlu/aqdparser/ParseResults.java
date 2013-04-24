package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;
import java.util.Collection;

public class ParseResults extends ArrayList<ParseResult>{

	private static final long serialVersionUID = 1L;
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
	
	@Override
	public boolean addAll(Collection<? extends ParseResult> results){
		if(results.size()>0){
			state=MATCH;
		}
		return super.addAll(results);
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
	
	public ParseResult getFirst(){
		return this.get(0);
	}
}
