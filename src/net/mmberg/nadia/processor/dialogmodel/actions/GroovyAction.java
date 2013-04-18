package net.mmberg.nadia.processor.dialogmodel.actions;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import net.mmberg.nadia.dialogmodel.definition.actions.GroovyActionModel;
import net.mmberg.nadia.processor.dialogmodel.Frame;

@XmlRootElement
public class GroovyAction extends GroovyActionModel {

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
