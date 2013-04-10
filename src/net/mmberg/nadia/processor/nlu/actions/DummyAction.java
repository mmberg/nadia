package net.mmberg.nadia.processor.nlu.actions;

import java.util.HashMap;

import net.mmberg.nadia.dialogmodel.Action;
import net.mmberg.nadia.dialogmodel.Task;


public class DummyAction extends Action{

	public DummyAction(){
		super();
	}
	
	public DummyAction(String template){
		super(template);
	}
	
	@Override
	public HashMap<String, String> execute(Task t) {
		
		//do something
		
		//save results		
		HashMap<String, String> executionResults = new HashMap<String, String>();	
		executionResults.put("temperature", "3");
		return executionResults;
	}
	
	

}
