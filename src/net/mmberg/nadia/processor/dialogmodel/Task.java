package net.mmberg.nadia.processor.dialogmodel;

import net.mmberg.nadia.dialogmodel.definition.TaskModel;


public class Task extends TaskModel{

	public Task(){
		super();
	}
	
	
	public Task(String name){
		super(name);
	}
	
	public Boolean isAllFilled(){
		for(ITO ito : itos){
			if (!ito.isFilled()) return false;
		}
		return true;
	}
	
	//TODO
	public Boolean isMandatoryFilled(){
		return false;
	}
	
	public String execute(){
		return getAction().executeAndGetAnswer(this);
	}
	
	public Frame toFrame(){
		Frame frame=new Frame();
		for(ITO ito : itos){
			frame.put(ito.getName(),ito.getValue());
		}
		return frame;
	}
	
}
