package net.mmberg.nadia.dialogmodel;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="task")
public class Task {

	//serializable members
	private String name;
	private String domain;
	private DialogAct act;
	private TaskSelector selector;
	private ITOs itos; //must not be null; otherwise unmarshalling fails
	private Action action;
		
	//non-serializable members
	//...
	
	//Serialization getter/setter
	public Task(){
		itos=new ITOs();
	}
	
	@XmlAttribute(name="name")
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public DialogAct getAct() {
		return act;
	}

	public void setAct(DialogAct act) {
		this.act = act;
	}

	public TaskSelector getSelector() {
		return selector;
	}

	public void setSelector(TaskSelector selector) {
		this.selector = selector;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@XmlElementWrapper(name="itos")
	@XmlElement(name="ito")
	public ITOs getITOs(){
		return itos;
	}
	
	//Content
	public Task(String name){
		this();
		this.name=name;
	}
	
	public void addITO(ITO ito){
		this.itos.add(ito);
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
