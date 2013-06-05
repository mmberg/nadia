package net.mmberg.nadia.processor.dialogmodel.actions;

import java.util.HashMap;

import net.mmberg.nadia.processor.dialogmodel.Frame;

public class TestExtJavaAction extends JavaAction{

	public TestExtJavaAction(){
		super();
	}
	
	public TestExtJavaAction(String template){
		super(template);
	}
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
		
		//do something
		
		//save results		
		executionResults.put("temperature", "5");
		return executionResults;
	}
	
	

}
