package net.mmberg.nadia.processor.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.dialogmodel.*;
import net.mmberg.nadia.processor.manager.DialogManagerContext.UTTERANCE_TYPE;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResult;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.processor.ui.UIConsumer;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage.Meta;
import net.mmberg.nadia.processor.utterance.UserUtterance;

public class DialogManager implements UIConsumer {

	private SodaRecognizer sodarec=null;
	private static boolean init=false;
	private final static Logger logger = NadiaProcessor.getLogger();
	
	private Dialog dialog=null;
	private DialogManagerContext context=null;
	private Task t=null;
	private ITOs itos=null;
	private Iterator<ITO> ito_iterator=null;
	
	public DialogManager(){
		this(NadiaProcessor.getDefaultDialog()); //load default dialogue
	}
	
	public DialogManager(Dialog dialog){
		init();
		loadDialog(dialog);
	}
	
	public DialogManagerContext getContext(){
		return context;
	}
	
	private void init(){
		context=new DialogManagerContext();
		context.setCreatedOn(new Date());
		sodarec=SodaRecognizer.getInstance();
		if(!init){
			if(!sodarec.isTrained()) sodarec.train(); //train Dialog Act Classifier
			
			Parsers.init(); //init (i.e. activate) Parsers
			
			init=true;
		}
	}
	
	public Dialog getDialog(){
		return dialog;
	}
	
	//experimental
	private UIConsumerMessage restart(){
		context.setStarted(false);
		return processUtterance(null);
	}
	
	@Override
	public void loadDialog(Dialog dialog){
		this.dialog = dialog;
	}
	
	@Override
	public String getDebugInfo() {
		String context="no debug info";
		context = getContext().serialize();
		context += "\r\n\r\n"+(getDialog().toXML());
		return context;
	}
	
	@Override
	public void setAdditionalDebugInfo(String debugInfo) {
		context.setAdditionalDebugInfo(debugInfo);
	}
	
	@Override
	public Date getLastAccess() {
		return context.getLastAccess();
	}
	
	//experimental
	@Override
	public UIConsumerMessage processUtterance(String userUtterance){
		
		context.setLastAccess(new Date());
		
		ParseResults results=null;
		UserUtterance answer=null;
				
		if(!context.isStarted()){
			context.setStarted(true);
			t=dialog.getTask("start"); //get a task and its associated ITOs
			itos=t.getITOs();
			ito_iterator=itos.iterator();
		}
		else if(userUtterance!=null){		
			context.addUtteranceToHistory(userUtterance, UTTERANCE_TYPE.USER);
			context.setQuestionOpen(false);
			//process user answer:
			answer=new UserUtterance(userUtterance);
			sodarec.predict(answer,context); //identify dialog act (sets features and soda by reference), access result: answer.getSoda()
			results=interpret(context.getCurrentQuestion(),answer.getText()); //Parsing; currentQuestion has been set in last call
			
			//TODO do something with the results
			//Beta:
			if(results.getState()==ParseResults.MATCH){
				for(ParseResult pres : results){
					context.getCurrentQuestion().setValue(pres.getResultString());
				}
				
				if(t.isFilled()){
					//execute action:
					String sysAns=t.execute();
					if (t.getAction().isReturnAnswer()){
						context.addUtteranceToHistory(sysAns, UTTERANCE_TYPE.SYSTEM);
						return new UIConsumerMessage(sysAns, Meta.ANSWER);
					}
				}
				
			}
			//--
			
		}
		else return new UIConsumerMessage("", Meta.UNCHANGED); //if there is no user utterance nothing will/should happen
				
		
		
		//return answer or next question or repeat question
		if(results!=null && results.getState()==ParseResults.NOMATCH){
			
			//check for other questions in this task
			
			//check for other tasks
			ArrayList<Task> tasks=dialog.getTasks();
			for(Task tsk : tasks){
				if (tsk.getSelector()!=null && tsk.getSelector().isResponsible(userUtterance)){
					t=tsk;
					itos=t.getITOs();
					ito_iterator=itos.iterator();
					ITO ito=ito_iterator.next();
					context.setCurrentQuestion(ito); //point current question to this ITO
					String question=ito.ask(); //get question
					context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM);
					return new UIConsumerMessage(question, Meta.QUESTION);
				}
			}
			
			//repeat question:
			String question="I did not understand that. Please try again. ";
			question+=context.getCurrentQuestion().ask();
			return new UIConsumerMessage(question, Meta.QUESTION);
		} //next question:
		else if(ito_iterator!=null && ito_iterator.hasNext()){
			ITO ito=ito_iterator.next();
			context.setCurrentQuestion(ito); //point current question to this ITO
			String question=ito.ask(); //get question
			context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM);
			return new UIConsumerMessage(question, Meta.QUESTION);
		}
//		else{
//			//find (new/different) task that matches the utterance if no more ITOs in current task found
//			ArrayList<Task> tasks=dialog.getTasks();
//			for(Task tsk : tasks){
//				if (tsk.getSelector()!=null && tsk.getSelector().isResponsible(userUtterance)){
//					t=tsk;
//					itos=t.getITOs();
//					ito_iterator=itos.iterator();
//					ITO ito=ito_iterator.next();
//					context.setQuestionOpen(true);
//					context.setCurrentQuestion(ito); //point current question to this ITO
//					String question=ito.ask(); //get question
//					return new UIConsumerMessage(question, Meta.QUESTION);
//				}
//			}
//			return new UIConsumerMessage("", Meta.END_OF_DIALOG);
//		}
		else return restart(); //new UIConsumerMessage("", Meta.END_OF_DIALOG);
	}
	
	public UIConsumerMessage processUtterance_old(String userUtterance){
		
		ParseResults results=null;
		
		if(!context.isStarted()){
			context.setStarted(true);
			t=dialog.getTask("bsp"); //get a task and its associated ITOs
			itos=t.getITOs();
			ito_iterator=itos.iterator();
		}
		else if(userUtterance!=null){		
			//process user answer:
			UserUtterance answer=new UserUtterance(userUtterance);
			sodarec.predict(answer,context); //identify dialog act (sets features and soda by reference), access result: answer.getSoda()
			results=interpret(context.getCurrentQuestion(),answer.getText()); //Parsing
			//TODO do something with the results
			
		}
		else return new UIConsumerMessage("", Meta.UNCHANGED); //if there is no user utterance nothing will/should happen
				
		//return answer or next question or repeat question
		if(results!=null && results.getState()==ParseResults.NOMATCH){
			String question="I did not understand that. Please try again. ";
			question+=context.getCurrentQuestion().ask();
			return new UIConsumerMessage(question, Meta.QUESTION);
		}
		else if(ito_iterator.hasNext()){
			ITO ito=ito_iterator.next();
			context.setQuestionOpen(true);
			context.setCurrentQuestion(ito); //point current question to this ITO
			String question=ito.ask(); //get question
			return new UIConsumerMessage(question, Meta.QUESTION);
		}
		else return new UIConsumerMessage("", Meta.END_OF_DIALOG);
	}
	

	private ParseResults interpret(ITO ito, String user_answer){
		ParseResults results=ito.parse(user_answer, true);
		if(results.size()>0) logger.info(results.toString()); else logger.warning("no parser matched");
		return results;
	}

}
