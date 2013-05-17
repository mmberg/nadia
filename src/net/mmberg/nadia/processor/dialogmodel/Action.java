package net.mmberg.nadia.processor.dialogmodel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mmberg.nadia.dialogmodel.definition.ActionModel;

public abstract class Action extends ActionModel{

	//non-serializable features
	protected HashMap<String, String> executionResults = new HashMap<String, String>();
	
	public Action(){
		super();
	}
	
	public Action(String utteranceTemplate){
		super(utteranceTemplate);
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
		
		//add further message to utterance depending on result
		ActionResultMapping resultMapping = getFirstMatchingResultMapping();
		if(resultMapping!=null){
			String conditionalAnswer;
			conditionalAnswer= resultMapping.getMessage();
			conditionalAnswer= replaceSlotMarkers(conditionalAnswer, frame);
			conditionalAnswer= replaceExecutionResultMarkers(conditionalAnswer, executionResults);
			answer=answer + " "+ conditionalAnswer;
		}
		
		return answer;
	}
	
	public ActionResultMapping getFirstMatchingResultMapping(){
		if(resultMappingList!=null){
			for(ActionResultMapping resultMapping : resultMappingList){
				if(executionResults.containsKey(resultMapping.getResultVarName())){
					if(executionResults.get(resultMapping.getResultVarName()).equals(resultMapping.getResultValue())){
						return resultMapping;
					}
				}
			}
		}
		return null;
	}
	
	//marker indicated by hash sign (#), i.e. references to execution results
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
	
	//marker indicated by percentage sign (%), i.e. references to ITOs
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