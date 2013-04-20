package net.mmberg.nadia.processor.manager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
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
	private ITO current_question;
	private Boolean started=false;
	private ArrayList<ITO> history=new ArrayList<ITO>();
	
	//Accessors
	public void setQuestionOpen(Boolean question_open) {
		this.question_open = question_open;
	}

	public Boolean isQuestionOpen() {
		return question_open;
	}
	
	public void setCurrentQuestion(ITO question){
		this.current_question=question;
		if(question!=null){
			setQuestionOpen(true);
			history.add(question);
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
		return this.current_question;
	}
	
	@XmlElement(name="currentQuestion")
	private String getCurrentQuestionUtterance(){
		return this.current_question.ask();
	}

	public ArrayList<ITO> getHistory(){
		return history;
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
