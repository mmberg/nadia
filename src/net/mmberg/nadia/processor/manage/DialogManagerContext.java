package net.mmberg.nadia.processor.manage;

import net.mmberg.nadia.dialogmodel.*;

public class DialogManagerContext {

	private static DialogManagerContext context=null;
	
	//Features
	private Boolean question_open=false;
	private ITO current_question;

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
	
	//Singleton
	private DialogManagerContext(){
		
	}
	
	public static DialogManagerContext getInstance(){
		if(context==null){
			context=new DialogManagerContext();
		}
		return context;
	}
	
	
}
