package net.mmberg.nadia.processor.nlu.actions;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.HashMap;

import net.mmberg.nadia.dialogmodel.Action;
import net.mmberg.nadia.dialogmodel.Frame;

public class GroovyAction extends Action{

	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public GroovyAction(){
		super();
	}
	
	public GroovyAction(String template){
		super(template);
	}
	
	public static void main(String[] args){
		new GroovyAction().execute(null);
	}
	
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
				
		//Groovy
		if(code!=null && code.length()>0){
			Binding binding = new Binding();
			binding.setVariable("executionResults", executionResults);
			GroovyShell shell = new GroovyShell(binding);
	
			shell.evaluate(code); //e.g. "executionResults.put(\"temperature\",\"6\")"
		}
		
		return executionResults;
	}

}
