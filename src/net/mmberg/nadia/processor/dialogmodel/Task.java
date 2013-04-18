package net.mmberg.nadia.processor.dialogmodel;

import net.mmberg.nadia.dialogmodel.definition.TaskModel;


public class Task extends TaskModel{

	public Task(){
		super();
	}
	
	
	public Task(String name){
		super(name);
	}
	
	public Boolean isFilled(){
		for(ITO ito : itos){
			if (!ito.isFilled()) return false;
		}
		return true;
	}
	
	public String execute(){
		return action.executeAndGetAnswer(this);
	}
	
	public Frame toFrame(){
		Frame frame=new Frame();
		for(ITO ito : itos){
			frame.put(ito.getName(),ito.getValue());
		}
		return frame;
	}
	
}
