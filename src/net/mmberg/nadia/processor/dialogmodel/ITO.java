package net.mmberg.nadia.processor.dialogmodel;

import javax.xml.bind.annotation.XmlTransient;

import net.mmberg.nadia.dialogmodel.definition.ITOModel;
import net.mmberg.nadia.processor.dialogmodel.aqd.*;
import net.mmberg.nadia.processor.lg.qg.Generator;
import net.mmberg.nadia.processor.nlu.aqdparser.*;

public class ITO extends ITOModel{
	
	//unserializable members
	private boolean filled;
	private Object value;
	private static Generator generator=Generator.getInstance();
	
	
	public ITO(){
		super();
	}	
	
	public ITO(String name, String fallback_question){
		super(name, fallback_question);
	}
	
	public ITO(String name, String fallback_question, boolean useLG){
		super(name, fallback_question, useLG);
	}
	
	@XmlTransient
	public Boolean isFilled(){
		return this.filled;
	}
	
	@XmlTransient
	public Object getValue(){
		return this.value;
	}
	
	public void setValue(Object value){
		this.value=value;
		this.filled=true;
	}
	
	
	public ParseResults parse(String utterance, boolean exact){
		//may use AQD but don't need to
		String answer_type=aqd.getType().getAnswerType();
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
			tempAQD.setType(aqd.getType());
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
