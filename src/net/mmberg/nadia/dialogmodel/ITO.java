package net.mmberg.nadia.dialogmodel;

import net.mmberg.nadia.processor.lg.qg.Generator;
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
	private boolean useLG=true;
	private static Generator generator=Generator.getInstance();
	
	public ITO(String name, String fallback_question){
		this.name=name;
		this.fallback_question=fallback_question;
	}
	
	public ITO(String name, String fallback_question, boolean useLG){
		this.name=name;
		this.fallback_question=fallback_question;
		this.useLG=useLG;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setAQD(AQD aqd){
		this.aqd=aqd;
	}
	
	public ParseResults parse(String utterance, boolean exact){
		//may use AQD but don't need to
		String answer_type=aqd.getAQDType().getAnswerType();
		ParseResults results = null;
		try{
			if(exact)
				results=Parsers.parseExact(utterance, answer_type);
			else
				results=Parsers.parseWithAllParsers(utterance);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return results;
	}
	
	public String askWithLG(){
		if(aqd.getForm().getPoliteness()==null){
			//clone AQD and set form according to generic dialogue settings, i.e. do not manipulate the AQD
			AQD tempAQD=new AQD();
			tempAQD.setAQDType(aqd.getAQDType());
			tempAQD.setContext(aqd.getContext());
			tempAQD.setForm(new AQDForm(3,3)); //TODO get values from dialogue
			return generator.generateQuestion(tempAQD);
		}
		return generator.generateQuestion(aqd);
	}
	
	public String askFallback(){
		return fallback_question;
	}
	
	//automatic mode
	public String ask(){
		//may use AQD (LG) but does't need to
		if(useLG && (generator!=null)){
			try{
				String question = askWithLG();
				if (question != null) return question;
				else return askFallback();
			}
			catch(Exception ex){
				ex.printStackTrace();
				return askFallback();
			}
		}
		else return askFallback();
	}
	
}
