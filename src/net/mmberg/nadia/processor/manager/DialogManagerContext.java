package net.mmberg.nadia.processor.manager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.mmberg.nadia.processor.dialogmodel.*;

@XmlRootElement
public class DialogManagerContext {

	//Features
	private Date createdOn;
	private Date lastAccess;
	private String additionalDebugInfo;
	private Boolean question_open=false;
	//private ITO current_question;
	private Boolean started=false;
	private ArrayList<ITO> ito_history=new ArrayList<ITO>();
	private ArrayList<String> dialog_history=new ArrayList<String>();
	private Task task;
	
	public enum UTTERANCE_TYPE {USER,SYSTEM};
	
	//Accessors
	public void setQuestionOpen(Boolean question_open) {
		this.question_open = question_open;
	}

	public Boolean isQuestionOpen() {
		return question_open;
	}
	
	public void setCurrentQuestion(ITO question){
		//this.current_question=question;
		if(question!=null){
			setQuestionOpen(true);
			ito_history.add(question);
		}
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getAdditionalDebugInfo() {
		return additionalDebugInfo;
	}

	public void setAdditionalDebugInfo(String additionalDebugInfo) {
		this.additionalDebugInfo = additionalDebugInfo;
	}
	
	@XmlTransient
	public ITO getCurrentQuestion(){
		return ito_history.get(ito_history.size()-1);
	}
	
	@XmlElement(name="currentQuestion")
	private String getCurrentQuestionUtterance(){
		if(question_open){
			return getCurrentQuestion().getUtteranceText();
		}
		else return "no current question";
	}

	@XmlElement(name="utterance")
	@XmlElementWrapper(name="dialogHistory")
	private ArrayList<String> getDialogHistory(){
		return dialog_history;
	}
	
	public void addUtteranceToHistory(String utterance, UTTERANCE_TYPE type){
		String prefix = (type==UTTERANCE_TYPE.SYSTEM)?"S: ":"U: ";
		dialog_history.add(prefix+utterance);
	}
	
	@XmlTransient
	public ArrayList<ITO> getHistory(){
		return ito_history;
	}
	
	public void setTask(Task task){
		this.task=task;
	}
	
	@XmlElement(name="frame")
	public Frame getFrame(){
		return task.toFrame();
	}
	
	public Boolean isStarted() {
		return started;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}
	
	public String serialize(){
		JAXBContext context;
		String result="";
		try {
			context = JAXBContext.newInstance(DialogManagerContext.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    m.marshal(this, bout);
		    result = bout.toString();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void print(){
		System.out.println(serialize());
	}
	
	
	
}
