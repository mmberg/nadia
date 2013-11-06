
package net.mmberg.nadia.processor.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.dialogmodel.*;
import net.mmberg.nadia.processor.exceptions.ProcessingException;
import net.mmberg.nadia.processor.manager.DialogManagerContext.UTTERANCE_TYPE;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.Soda;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.processor.ui.UIConsumer;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage.Meta;
import net.mmberg.nadia.processor.utterance.UserUtterance;

public class DialogManager implements UIConsumer {

	private SodaRecognizer sodarec=null;
	private static boolean init=false;
	private final static Logger logger = NadiaProcessor.getLogger();
	
	private boolean followup=false; //move to context?
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
	

	//UIConsumer:
	
	@Override
	public void loadDialog(Dialog dialog){
		context.setDialog(dialog);
	}
	
	@Override
	public String getDebugInfo() {
		return getContext().serialize();
	}
	
	@Override
	public String getDialogXml() {
		return context.getDialog().toXML();
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
		//ParseResults results=null;
		UserUtterance answer=null;
		Boolean found=false;
				
		//STEP 1:
		//1a) INITIALIZE THE DIALOGUE
		if(!context.isStarted()){
			logger.info("init dialogue");
			context.setStarted(true);
			Task t=context.getDialog().getStartTask(); //get start task and its associated ITOs
			if(t==null) {
				t=context.getDialog().getFirstTask(); //if no start task defined just take the first one
			}
			return initTaskAndGetNextQuestion(t);
		}
		//or 1b) DO NOTHING: if there is no user utterance and the dialogue has already been initialized, nothing will/should happen:
		else if (userUtterance==null || userUtterance.length()==0){
			logger.info("no utterance, no change");
			return new UIConsumerMessage("", Meta.UNCHANGED); 
		}
		//or 1c) PROCESS USER UTTERANCE
		else if(context.getCurrentTask()!=null){
			context.addUtteranceToHistory(userUtterance, UTTERANCE_TYPE.USER, context.getTaskStack().size());
			answer=new UserUtterance(userUtterance);
			sodarec.predict(answer, context); //identify dialog act (sets features and soda by reference), access result: answer.getSoda()
			//context.setQuestionOpen(false); //needs to be set AFTER dialogue act recognition!
			
			//Parse:
			if((context.getDialog().isUseSODA() && answer.getSoda().equals(Soda.INFORMATION_PROVIDING))||(!context.getDialog().isUseSODA())){
				//results=interpret(context.getCurrentQuestion(), answer.getText()); //Parsing; currentQuestion has been set in last call		
				//ParseResults results=interpret(context.getCurrentQuestion(), answer.getText());
				//found = (results.getState()==ParseResults.MATCH);
				found = lookForAnswers(context.getCurrentQuestion(), context.getCurrentTask().getITOs(), answer);
				
				//SODA Postprocessing:
				if(answer.getSoda().equals(Soda.INFORMATION_PROVIDING) && (!found || !context.isQuestionOpen())){
					answer.setSoda(Soda.INFORMATION_SEEKING);
					logger.info("SODA Postprocessing: prov -> seek");
				}
			}
			
			context.setQuestionOpen(false); //needs to be set AFTER dialogue act recognition!
						
			//STEP 2:			
			//2a) IF PARSING SUCCESSFUL, SAVE RESULTS
//			if(results!=null && results.getState()==ParseResults.MATCH){
			if(found){
				logger.info("interpretation was successful in task: "+context.getCurrentTask().getName());
				//storeResults(context.getCurrentQuestion(), results);
				
				//follow-up
				if(followup){
					logger.info("processing follow up");
					followup=false; //TODO might be better to make a FollowUp:ITO and check type on the fly in process_utterance()
					HashMap<String, String> mapping=context.getCurrentTask().getFollowup().getAnswerMapping();
//					String taskToStart=mapping.get(results.getFirst().getResultString());
					String taskToStart=mapping.get(context.getCurrentTask().getFollowup().getIto().getValue());
					if(taskToStart!=null){
						return abortAndStartNewTask(taskToStart,null);
					}
					else return getNextQuestion();
				}

				
//				//TODO: multiple information in one answer (mixed initiative)
//				//...
//				//beta
//				if(context.getDialog().isAllowOverAnswering()){
//					String processedAnswer=answer.getText();
//					processedAnswer=processedAnswer.replace(results.getFirst().getMatchedSequence(),"");
//					lookForAnswers(context.getCurrentTask().getITOs(), new UserUtterance(processedAnswer));
//				}
//				//...
				
				if(!context.getCurrentQuestion().isFilled()){
					logger.info("detected and realised a correction or answer for a different question");
					String message="Ok, I got that. "; //acknowledge that different question has been filled
					String question= message + context.getCurrentQuestion().ask(context.getDialog().getGlobal_politeness(), context.getDialog().getGlobal_formality());
					context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
					context.setQuestionOpen(true);					
					return new UIConsumerMessage(question, Meta.QUESTION);
				}
			}
			//or 2b) IF PARSING NOT SUCCESSFUL, CHECK FOR OTHER POSSIBILITIES
//			else if(results==null || results.getState()==ParseResults.NOMATCH){
			else if(!found){		
				logger.info("Could not be interpreted in current task: "+context.getCurrentTask().getName());
				String message=null;
				boolean found_question_for_given_answer=false;
				boolean found_different_task=false;
				
//				//2b1) check for all/other questions in THIS task
//				if((context.getDialog().isUseSODA() && answer.getSoda().equals("prov"))||(!context.getDialog().isUseSODA())){
//					if(context.getDialog().isAllowDifferentQuestion()){
//						if(found_question_for_given_answer = lookForAnswers(context.getCurrentTask().getITOs(), answer)){
//							message="Ok. "; //acknowledge that different question has been filled
//						}					
//					}
//				}
					
				//2b2) if not successful, check for OTHER tasks
				if((context.getDialog().isUseSODA() && (answer.getSoda().equals(Soda.INFORMATION_SEEKING) || answer.getSoda().equals(Soda.ACTION_REQUESTING))) || !context.getDialog().isUseSODA()){
					if(context.getDialog().isAllowSwitchTasks() && !found_question_for_given_answer){
						ArrayList<Task> tasklist=context.getDialog().getTasks();
						for(Task tsk : tasklist){
							if (tsk==context.getCurrentTask()) continue; //except this task
							if (tsk.getSelector()!=null && tsk.getSelector().isResponsible(userUtterance)){
								
								//prevent follow-up-stacking!
								//if the current question is a follow-up question and the user wants to start another task,
								//remove it from stack, also if it has not been answered,
								//i.e. if you ignore a follow-up question, it will not be asked again
								if(followup){
									followup=false;
									context.getTaskStack().pop().reset(); //remove current task from stack and reset
								}

								
								//check act
								if(context.getDialog().isUseSODA()){
									if(tsk.getAct()!=null && tsk.getAct().length()>0){
										if (!answer.getSoda().equals(tsk.getAct())) continue;
									}
								}								
								
								found_different_task=true;
								logger.info("responsible task is: "+tsk.getName());
								
								/* check if task is already on stack (anti recursion!)
								 * i.e. if user goes back (if he does not call a new subdialog but instead calls  a previous (existing) dialog, 
								 * i.e. goes back in history), destroy until desired task is active again */
								if(context.getTaskStack().contains(tsk)){
									Task poppedTask;
									while((poppedTask=context.getTaskStack().pop())!=tsk){ //pop and reset until selected task is active
										poppedTask.reset();
									}
								}
																
								//check this task-switch-request for further information
								switchTask(tsk);
								if (context.getDialog().isAllowOverAnswering()){
									logger.info("check task-switch-request for more information (over answering active)");
									lookForAnswers(tsk.getITOs(), answer);	
								}
								break;
							}
						}
					}
				}
					
				//2b3) or repeat question (if directed dialogue or alternatives not successful):
				if(!found_different_task){
					logger.info("Could not be interpreted in any task. Try again.");
					if(message==null) message="I did not understand that. Please try again. ";
					String question=message + context.getCurrentQuestion().ask(context.getDialog().getGlobal_politeness(), context.getDialog().getGlobal_formality());
					context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
					context.setQuestionOpen(true);					
					return new UIConsumerMessage(question, Meta.QUESTION);
				}
			} //-- endif 2b (parsing unsuccessful)
					
			
			//STEP 3:
			//3a) IF ALL REQUIRED INFORMATION RETRIEVED, EXECUTE ACTION
			UIConsumerMessage answer_msg=null;
			Action action=context.getCurrentTask().getAction();
			//if(context.getCurrentTask().isAllFilled() && (action!=null)){
			if(context.getCurrentTask().isMandatoryFilled() && (action!=null)){
				logger.info("frame filled, executing action");
				String sysAns=context.getCurrentTask().execute();
				if (action.isReturnAnswer()){
					answer_msg=new UIConsumerMessage(sysAns, Meta.ANSWER); //the answer is integrated into the next question
				}
				//TODO beta: task redirection depending on action result
				ActionResultMapping resultMapping;
				if((resultMapping=action.getFirstMatchingResultMapping())!=null){
					if(resultMapping.getRedirectToTask()!=null && resultMapping.getRedirectToTask().length()>0){
						return abortAndStartNewTask(resultMapping.getRedirectToTask(),answer_msg); //TODO maybe this should not necessarily abort the current task? 
					}
				}
				//--
			}
			return getNextQuestion(answer_msg);
		}
		else return end();
		
		//this line should never be reached, else throw exception:
		//throw new ProcessingException("Unexpected dialogue state. This error should never occur!");
	}
	
	

	//Helpers:
	
	private ParseResults interpret(ITO ito, String user_answer){
		ParseResults results=ito.parse(user_answer, true);
		if(results.size()>0){
			logger.info("a parser matched for ITO "+ito.getName() +": "+ results.toString()); 
		}
		else logger.warning("no parser matched for ITO "+ito.getName());

		return results;
	}
	
	private void storeResults(ITO ito, ParseResults results){
		if(results!=null && results.size()>0 && results.getState()==ParseResults.MATCH){
			ito.setValue(results.getFirst().getResultString()); //TODO what if more than one parse result?
			if (results.size()>1) logger.warning("Multiple Parse Results. Only first one will be processed!");
		}
		else logger.warning("ParseResults were empty and could not be stored in frame.");
	}
	
	private boolean lookForAnswers(ArrayList<ITO> all_itos, UserUtterance answer){
		return lookForAnswers(null, all_itos, answer);
	}
	
	/*
	 * check for all/other questions in this task
	 */
	private boolean lookForAnswers(ITO current_ito, ArrayList<ITO> all_itos, UserUtterance answer){

		logger.info("looking for answers...");	
		boolean found=false;
		
		//if((context.getDialog().isUseSODA() && answer.getSoda().equals(Soda.INFORMATION_PROVIDING))||(!context.getDialog().isUseSODA())){

			String processedAnswer=answer.getText();
			if(processedAnswer.length()>0){
				ParseResults results=null;
						
				//first check current ITOs
				if(current_ito!=null){
					results=interpret(current_ito, processedAnswer);
					if(results!=null && results.getState()==ParseResults.MATCH){
						found=true;
						processedAnswer=processedAnswer.replace(results.getFirst().getMatchedSequence(),""); //remove matched parts to prevent multiple processing of same information; otherwise CITY fills both departure and destination
						storeResults(current_ito, results);
					}
				}
				
				boolean overanswering = (found && context.getDialog().isAllowOverAnswering());
				boolean differentquestion_correction = (!found && (context.getDialog().isAllowDifferentQuestion() || context.getDialog().isAllowCorrection()));
				if(overanswering || differentquestion_correction){

					if (overanswering) logger.info("(over answering allowed)");
					if (differentquestion_correction) logger.info("(different question or correction allowed)");
		
					//then check all (other) ITOs
					for(ITO ito : all_itos){
						if(current_ito != null && ito == current_ito) continue;
						if(!context.getDialog().isAllowCorrection()){ //if correction not allowed, only use unanswered ITOs
							if (ito.isFilled()) continue;
						}
									
						results=interpret(ito, processedAnswer); //answer.getText()
						if(results.getState()==ParseResults.MATCH){
							found=true;
							processedAnswer=processedAnswer.replace(results.getFirst().getMatchedSequence(),""); //remove matched parts to prevent multiple processing of same information; otherwise CITY fills both departure and destination
							storeResults(ito, results);
							if(processedAnswer.length()==0) break;
							//break; //abort after first success 
						}
					}
				}
			}
		//}
		
		if(!found) logger.info("no answers found");
		return found;
	}
	
	
	
	private UIConsumerMessage getNextQuestion() throws ProcessingException{
		return getNextQuestion(null);
	}
	
	private UIConsumerMessage getNextQuestion(UIConsumerMessage questionPrefix) throws ProcessingException{
		
		logger.info("getting next question...");
		
		if(context.getIto_iterator().hasNext()){ //if more questions in current task
			ITO ito=context.getIto_iterator().next();
			
			if (ito.isFilled()){
				logger.info("ITO "+ito.getName() + " already filled");
				return getNextQuestion(questionPrefix); //if already answered, get next question
			}
			//beta
			else if(!ito.isRequired()){
				logger.info("ITO "+ito.getName() + " is optional and thus skipped");
				return getNextQuestion(questionPrefix); 
			}
			//---
			else{
				context.setCurrentQuestion(ito); //point current question to this ITO
				String answer_message=(questionPrefix==null)?"":(questionPrefix.getSystemUtterance()+" ");
				String question=ito.ask(context.getDialog().getGlobal_politeness(), context.getDialog().getGlobal_formality()); //get question
				String utterance = answer_message+question;
				context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size()); //answer_message is recorded in different else-branch
				return new UIConsumerMessage(utterance, Meta.QUESTION);
			}
		}
		//-- beta --
		//if follow up exists:
		else if(context.getCurrentTask().getFollowup()!=null && context.getCurrentTask().getFollowup().getIto()!=null && !context.getCurrentTask().getFollowup().getIto().isFilled()){
			followup=true;
			ITO ito=context.getCurrentTask().getFollowup().getIto();
			context.setCurrentQuestion(ito);
			String answer_message=(questionPrefix==null)?"":(questionPrefix.getSystemUtterance()+" ");
			String question=ito.ask(context.getDialog().getGlobal_politeness(), context.getDialog().getGlobal_formality()); //get question
			String utterance = answer_message+question;
			if(questionPrefix != null) context.addUtteranceToHistory(questionPrefix.getSystemUtterance(), UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
			context.addUtteranceToHistory(question, UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size()); //answer is recorded in different else-branch
			return new UIConsumerMessage(utterance, Meta.QUESTION);
		}
		//--
		//if current task has no more questions, get next task from stack
		else if(context.getTaskStack().size()>1){ //other tasks on stack (apart from current task)
			logger.info("no more questions, task completed");
			if(questionPrefix != null) context.addUtteranceToHistory(questionPrefix.getSystemUtterance(), UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
			context.getTaskStack().pop().reset(); //remove current (finished task) from stack and reset
			return initTaskAndGetNextQuestion(context.getTaskStack().pop(), questionPrefix);//get next task from stack 
		}
		//if stack is empty and no more questions available but an answer from the last execution is still pending, return this answer
		else if(questionPrefix!=null){
			context.getTaskStack().pop().reset(); //remove current (finished task) from stack
			return questionPrefix;
		}
		//else return restart(); //restart if no more questions available
		else return end(); //indicate end of dialogue
	}
	
	private UIConsumerMessage initTaskAndGetNextQuestion(Task t) throws ProcessingException{
		return initTaskAndGetNextQuestion(t, null);
	}
	
	
	private void switchTask(Task t){
		logger.info("switch task to: "+t.getName());
		context.getTaskStack().push(t);
//		context.setTask(t);
		context.setIto_iterator(t.getITOs().iterator());
	}
	
	//TODO beta
	private UIConsumerMessage abortAndStartNewTask(String taskName, UIConsumerMessage questionPrefix) throws ProcessingException{
		if(questionPrefix != null) context.addUtteranceToHistory(questionPrefix.getSystemUtterance(), UTTERANCE_TYPE.SYSTEM, context.getTaskStack().size());
		context.getTaskStack().pop().reset(); //remove current (finished task) from stack
		Task newTask=context.getDialog().getTask(taskName);
		return initTaskAndGetNextQuestion(newTask,questionPrefix);
	}
	
	private UIConsumerMessage initTaskAndGetNextQuestion(Task t, UIConsumerMessage questionPrefix) throws ProcessingException{
		switchTask(t);
		return getNextQuestion(questionPrefix);
	}
	
	//TODO
	private void restartTask(){
		//pop
		//reset
		//switch task
	}
	
	//TODO still experimental
	private UIConsumerMessage end() throws ProcessingException{
		while(!context.getTaskStack().isEmpty()){
			context.getTaskStack().pop().reset(); //destroy all tasks
		}
		context.setStarted(false); //allows user to (re)start the dialogue again
		logger.info("end of dialogue");
		return new UIConsumerMessage("-- END OF DIALOG --", Meta.END_OF_DIALOG); //or indicate end of dialogue
	}
	
	//TODO still experimental
	private UIConsumerMessage restart() throws ProcessingException{
		context.setStarted(false);
		return processUtterance(null);
	}
	
}
