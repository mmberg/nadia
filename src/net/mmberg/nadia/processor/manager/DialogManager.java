package net.mmberg.nadia.processor.manager;

import java.util.Iterator;
import java.util.logging.Logger;

import net.mmberg.nadia.Nadia;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage.Meta;
import net.mmberg.nadia.utterance.UserUtterance;
import net.mmberg.nadia.dialogmodel.*;

public class DialogManager {

	private SodaRecognizer sodarec=null;
	private static boolean init=false;
	private final static Logger logger = Nadia.getLogger();
	
	private Dialog dialog=null;
	private DialogManagerContext context=null;
	private Task t=null;
	private ITOs itos=null;
	private Iterator<ITO> ito_iterator=null;

	
	public DialogManager(){
		init();
	}
	
	public DialogManager(Dialog dialog){
		init();
		loadDialog(dialog);
	}
	
	public DialogManagerContext getContext(){
		if(context==null) context=new DialogManagerContext();
		return context;
	}
	
	private void init(){
			sodarec=SodaRecognizer.getInstance();
			if(!init){
				if(!sodarec.isTrained()) sodarec.train(); //train Dialog Act Classifier
				
				Parsers.init(); //init (i.e. activate) Parsers
				
				init=true;
		}
	}
		
	public void loadDialog(Dialog dialog){
		this.dialog = dialog;
		context=new DialogManagerContext();
	}
	
	public UIConsumerMessage processUtterance(String userUtterance){
		
		ParseResults results=null;
		
		if(!context.getStarted()){
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
