package net.mmberg.nadia.dialogmodel;

import net.mmberg.nadia.processor.nlu.aqdparser.*;
import net.mmberg.nadia.dialogmodel.aqd.*;

public class ITO {
	private String name;
	private Object value;
	private int group;
	private int index;
	private boolean required;
	private boolean filled;
	private AQD aqd;
	private String fallback_question;
	
	public ITO(String name, String fallback_question){
		this.name=name;
		this.fallback_question=fallback_question;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setAQD(AQD aqd){
		this.aqd=aqd;
	}
	
	public ParseResults parse(String utterance){
		//may use AQD but don't need to
		String answer_type=aqd.getAQDType().getAnswerType();
		ParseResults results = new ParseResults(utterance);
		try{
			results=Parsers.parse(utterance, answer_type);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return results;
	}
	
	public String ask(){
		//may use AQD (LG) but does't need to
		return fallback_question;
	}
	
}
