package net.mmberg.nadia.processor.dialogmodel.actions;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import net.mmberg.nadia.processor.dialogmodel.Action;
import net.mmberg.nadia.processor.dialogmodel.Frame;

@XmlRootElement
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
