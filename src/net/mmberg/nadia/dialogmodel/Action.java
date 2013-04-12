package net.mmberg.nadia.dialogmodel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlSeeAlso;

import net.mmberg.nadia.processor.nlu.actions.DummyAction;
import net.mmberg.nadia.processor.nlu.actions.GroovyAction;
import net.mmberg.nadia.processor.nlu.actions.JavaAction;

@XmlSeeAlso ({DummyAction.class, JavaAction.class, GroovyAction.class})
public abstract class Action {

	//serializable features
	private boolean returnAnswer=true;
	private String utteranceTemplate=""; //e.g. "The temperature in %getWeatherCity is #temperature!"
	
	//non-serializable features
	protected HashMap<String, String> executionResults = new HashMap<String, String>();
	
	//Serialization getters/setters
	public boolean isReturnAnswer() {
		return returnAnswer;
	}

	public void setReturnAnswer(boolean returnAnswer) {
		this.returnAnswer = returnAnswer;
	}
	

	public String getUtteranceTemplate() {
		return utteranceTemplate;
	}

	public void setUtteranceTemplate(String utteranceTemplate) {
		this.utteranceTemplate = utteranceTemplate;
	}

	public Action(){
		
	}
	
	public Action(String utteranceTemplate){
		this.utteranceTemplate=utteranceTemplate;
	}
	
	//content
	public abstract HashMap<String, String> execute(Frame frame);
	
	
	
	//fills template with values from frame
	public String executeAndGetAnswer(Task t){
		String answer="";
		Frame frame=t.toFrame();
		HashMap<String, String> executionResults=execute(frame);
		if(isReturnAnswer()){
			answer= replaceSlotMarkers(utteranceTemplate, frame);
			answer= replaceExecutionResultMarkers(answer, executionResults);
		}
		return answer;
	}
	
	//marker indicated by hash sign (#)
	protected static String replaceExecutionResultMarkers(String template, HashMap<String, String> executionResults){
		String answer="";
		if(template != null && template.length()>0){
			answer=new String(template);
			
			Pattern pattern = Pattern.compile("#(\\w+)");
			Matcher matcher = pattern.matcher(template);	
			
			String exec_res_name;
			while (matcher.find()) {
				exec_res_name=matcher.group(1);
				if(executionResults.containsKey(exec_res_name)){
					answer=answer.replaceFirst("#(\\w+)",executionResults.get(exec_res_name).toString());
				}
			}
		}
		
		return answer;
	}
	
	//marker indicated by percentage sign (%)
	protected static String replaceSlotMarkers(String template, Frame frame){
		String answer="";
		if(template != null && template.length()>0){
			answer=new String(template);
			
			Pattern pattern = Pattern.compile("%(\\w+)");
			Matcher matcher = pattern.matcher(template);	
			
			String ito_name;
			while (matcher.find()) {
				ito_name=matcher.group(1);
				answer=answer.replaceFirst("%(\\w+)",frame.get(ito_name).toString());
			}
		}
		
		return answer;
	}
}
