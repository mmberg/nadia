package net.mmberg.nadia.processor.dialogmodel;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
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
		//TODO beta:analyse action result 
//		if(mapping!=null){
//			String conditionalAnswer;
//			Set<Entry<String, String>> entryset = mapping.entrySet();
//			for(Entry<String,String> entry : entryset){
//				if (executionResults.containsKey(entry.getKey())){
//					conditionalAnswer=entry.getValue();
//					conditionalAnswer= replaceSlotMarkers(conditionalAnswer, frame);
//					conditionalAnswer= replaceExecutionResultMarkers(conditionalAnswer, executionResults);
//					answer=answer + conditionalAnswer;
//					break;
//				}
//			}
//		}
//		//--
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