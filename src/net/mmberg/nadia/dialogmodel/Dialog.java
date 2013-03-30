package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class Dialog {

	//serializable members
	private String name;
	private ArrayList<Task> tasks;
	
	//non serializable members

	
	//Serialization getter/setter
	public Dialog(){
		tasks=new ArrayList<Task>();
	}
		
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name="tasks")
	@XmlElement(name="task")
	public ArrayList<Task> getTasks(){
		return tasks;
	}
	
	public void setTasks(ArrayList<Task> tasks){
		this.tasks=tasks;
	}
	
	
	//Content
	public Dialog(String name){
		this();
		this.name=name;
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
