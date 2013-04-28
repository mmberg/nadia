package net.mmberg.nadia.processor.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.dialogmodel.*;
import net.mmberg.nadia.processor.exceptions.ProcessingException;
import net.mmberg.nadia.processor.manager.DialogManagerContext.UTTERANCE_TYPE;
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
	
	private Dialog dialog=null; //move to context?
	private DialogManagerContext context=null;
	
	
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
	

	//UIConsumer:
	
	@Override
	public void loadDialog(Dialog dialog){
		this.dialog = dialog;
		
	}
	
	@Override
	public String getDebugInfo() {
		return getContext().serialize();
	}
	
	@Override
	public String getDialogXml() {
		return getDialog().toXML();
	}
	
	
	@Override
	public void setAdditionalDebugInfo(String debugInfo) {
		context.setAdditionalDebugInfo(debugInfo);
	}
	
	@Override
	public Date getLastAccess() {
		return context.getLastAccess();
	}
	
	//TODO experimental
	@Override
	public UIConsumerMessage processUtterance(String userUtterance) throws ProcessingException{
		
		context.setLastAccess(new Date());
		
		ParseResults results=null;
		UserUtterance answer=null;
				
		//STEP 1:
		//1a) INITIALIZE THE DIALOGUE
		if(!context.isStarted()){
			context.setStarted(true);
			Task t=dialog.getStartTask(); //get start task and its associated ITOs
			if(t==null) {
				t=dialog.getFirstTask(); //if no start task defined just take the first one
			}
			return initTaskAndGetNextQuestion(t);
		}
		//or 1b) DO NOTHING: if there is no user utterance and the dialogue has already been initialized, nothing will/should happen:
		else if (userUtterance==null || userUtterance.length()==0){
			return new UIConsumerMessage("", Meta.UNCHANGED); 
		}
		//or 1c) PROCESS USER UTTERANCE
		else if(context.getCurrentTask()!=null){
			context.addUtteranceToHistory(userUtterance, UTTERANCE_TYPE.USER, context.getTaskStack().size());
			context.setQuestionOpen(false);

			answer=new UserUtterance(userUtterance);
			sodarec.predict(answer, context); //identify dialog act (sets features and soda by reference), access result: answer.getSoda()
			results=interpret(context.getCurrentQuestion(), answer.getText()); //Parsing; currentQuestion has been set in last call
			
			//STEP 2:
			//2a) IF PARSING SUCCESSFUL, SAVE RESULTS
			if(results.getState()==ParseResults.MATCH){
				storeResults(context.getCurrentQuestion(), results);
			}
			//or 2b) IF PARSING NOT SUCCESSFUL, CHECK FOR OTHER POSSIBILITIES
			else if(results!=null && results.getState()==ParseResults.NOMATCH){
					
				String message=null;
				
				//if mixed initiative
				if(dialog.getStrategy().equals("mixed")){
					
					//check for all/other questions in this task
					//TODO buggy (e.g. do not proceed to next question) 
					boolean found_question_for_given_answer=false;
					for(ITO ito : context.getCurrentTask().getITOs()){
						results=interpret(ito, answer.getText());
						if(results.getState()==ParseResults.MATCH){
							found_question_for_given_answer=true;
							storeResults(ito, results);
							message="Ok. "; //acknowledge that different question has been filled
							break;
						}
					}
						
					//if not successful, check for other tasks
					if(!found_question_for_given_answer){
						ArrayList<Task> tasklist=dialog.getTasks();
						for(Task tsk : tasklist){
							if (tsk.getSelector()!=null && tsk.getSelector().isResponsible(userUtterance)){
								//if suitable task found, switch to this task and load first question
								return initTaskAndGetNextQuestion(tsk);
							}
						}
					}
				}
					
				//or repeat question (if directed dialogue or alternatives not successful):
				if(message==null) message="I did not understand that. Please try again. ";
				String question=message + context.getCurrentQuestion().ask(dialog.getGlobal_politeness(), dialog.getGlobal_formality());
				context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
				return new UIConsumerMessage(question, Meta.QUESTION);
					
			}
			
			//STEP 3:
			//3a) IF ALL INFORMATION RETRIEVED, EXECUTE ACTION
			UIConsumerMessage answer_msg=null;
			if(context.getCurrentTask().isAllFilled()){
				String sysAns=context.getCurrentTask().execute();
				if (context.getCurrentTask().getAction().isReturnAnswer()){
					//context.addUtteranceToHistory(sysAns, UTTERANCE_TYPE.SYSTEM);
					//return new UIConsumerMessage(sysAns, Meta.ANSWER);
					answer_msg=new UIConsumerMessage(sysAns, Meta.ANSWER); //the answer is integrated into the next question
				}
			}
//			//or 3b) GET NEXT QUESTION
//			else if(ito_iterator!=null && ito_iterator.hasNext()){
//				return getNextQuestion();
//			}
//			//or 3c) RESTART DIALOGUE IF NO MORE QUESTIONS AVAILABLE 
//			else return restart(); //or indicate end of dialogue: new UIConsumerMessage("", Meta.END_OF_DIALOG);
						
			//else return getNextQuestion(); //get next question or restart if no more questions available
			return getNextQuestion(answer_msg);
		}
		else return end();
		
		//this line should never be reached, else throw exception:
		//throw new ProcessingException("Unexpected dialogue state. This error should never occur!");
	}
	

	//Helpers:
	
	private ParseResults interpret(ITO ito, String user_answer){
		ParseResults results=ito.parse(user_answer, true);
		if(results.size()>0) logger.info(results.toString()); else logger.warning("no parser matched");
		return results;
	}
	
	private void storeResults(ITO ito, ParseResults results){
		if(results!=null && results.size()>0 && results.getState()==ParseResults.MATCH){
			ito.setValue(results.getFirst().getResultString()); //TODO what if more than one parse result?
		}
		else logger.warning("ParseResults were empty and could not be stored in frame.");
	}
	
	private UIConsumerMessage getNextQuestion() throws ProcessingException{
		return getNextQuestion(null);
	}
	
	private UIConsumerMessage getNextQuestion(UIConsumerMessage questionPrefix) throws ProcessingException{
		if(context.getIto_iterator().hasNext()){ //if more questions in current task
			ITO ito=context.getIto_iterator().next();
			if (ito.isFilled()) return getNextQuestion(questionPrefix); //if already answered, get next question
			else{
				context.setCurrentQuestion(ito); //point current question to this ITO
				String question=(questionPrefix==null)?"":(questionPrefix.getSystemUtterance()+" ");
				question+=ito.ask(dialog.getGlobal_politeness(), dialog.getGlobal_formality()); //get question
				context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
				return new UIConsumerMessage(question, Meta.QUESTION);
			}
		}
		//if current task has no more questions, get next task from stack
		else if(context.getTaskStack().size()>1){ //other tasks on stack (apart from current task)
			context.getTaskStack().pop().reset(); //remove current (finished task) from stack and reset
			return initTaskAndGetNextQuestion(context.getTaskStack().pop(), questionPrefix);//get next task from stack 
		}
		//if stack is empty and no more questions available but an answer from the last execution is still pending, return this answer
		else if(questionPrefix!=null){
			context.getTaskStack().pop().reset(); //remove current (finished task) from stack
			return questionPrefix;
		}
		//else return restart(); //restart if no more questions available
		//else return new UIConsumerMessage("-- END OF DIALOG --", Meta.END_OF_DIALOG); //or indicate end of dialogue
		else return end();
	}
	
	private UIConsumerMessage initTaskAndGetNextQuestion(Task t) throws ProcessingException{
		return initTaskAndGetNextQuestion(t, null);
	}
	
	private UIConsumerMessage initTaskAndGetNextQuestion(Task t, UIConsumerMessage questionPrefix) throws ProcessingException{
		context.getTaskStack().push(t);
		context.setTask(t);
		context.setIto_iterator(t.getITOs().iterator());
		
		return getNextQuestion(questionPrefix);
	}
	
	//TODO still experimental
		private UIConsumerMessage end() throws ProcessingException{
			return new UIConsumerMessage("-- END OF DIALOG --", Meta.END_OF_DIALOG); //or indicate end of dialogue
		}
	
	//TODO still experimental
	private UIConsumerMessage restart() throws ProcessingException{
		context.setStarted(false);
		return processUtterance(null);
	}
	
}
