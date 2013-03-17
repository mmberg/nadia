package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;

public class Task {

	private String name;
	private String domain;
	private DialogAct act;
	private TaskSelector selector;
	private ITOs itos;
	private Action action;
	
	
	public Task(String name){
		this.name=name;
		this.itos=new ITOs();
	}
	
	public ITOs getITOs(){
		return itos;
	}
	
	public void addITO(ITO ito){
		this.itos.add(ito);
	}
	
	public String getName(){
		return name;
	}
}
