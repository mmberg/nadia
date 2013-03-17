package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;

public class Dialog {

	private ArrayList<Task> tasks;
	
	public Dialog(){
		tasks=new ArrayList<Task>();
	}
	
	public void addTask(Task task){
		this.tasks.add(task);
	}
	
	public Task getTask(String name){
		for(Task t : tasks){
			if(t.getName().equals(name)) return t;
		}
		return null;
	}
}
