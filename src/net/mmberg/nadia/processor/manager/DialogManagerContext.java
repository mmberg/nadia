package net.mmberg.nadia.processor.manager;

import net.mmberg.nadia.dialogmodel.*;

public class DialogManagerContext {

	//Features
	private Boolean question_open=false;
	private ITO current_question;
	private Boolean started=false;
	
	//Accessors
	public void setQuestionOpen(Boolean question_open) {
		this.question_open = question_open;
	}

	public Boolean isQuestionOpen() {
		return question_open;
	}
	
	public void setCurrentQuestion(ITO question){
		this.current_question=question;
	}
	
	public ITO getCurrentQuestion(){
		return this.current_question;
	}

	public Boolean getStarted() {
		return started;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}
	
	
}
