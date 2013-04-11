package net.mmberg.nadia.processor.nlu.actions;

import java.util.HashMap;

import net.mmberg.nadia.dialogmodel.Action;
import net.mmberg.nadia.dialogmodel.Frame;


public class DummyAction extends Action{

	public DummyAction(){
		super();
	}
	
	public DummyAction(String template){
		super(template);
	}
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
		
		//do something
		
		//save results		
		executionResults.put("temperature", "3");
		return executionResults;
	}
	
	

}
