package net.mmberg.nadia.processor.manage;

import java.util.logging.Logger;

import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.ui.ConsoleInterface;
import net.mmberg.nadia.ui.UserInterface;
import net.mmberg.nadia.utterance.UserUtterance;
import net.mmberg.nadia.dialogmodel.*;

public class DialogManager {

	private SodaRecognizer sodarec;
	private final static Logger logger = Logger.getLogger(DialogManager.class.getName()); 
	
	public DialogManager(){
		init();
	}
	
	
	private void init(){

		//Train Dialog Act Classifier
		sodarec=new SodaRecognizer();
		sodarec.train();
		
		//init Parsers
		Parsers.init();
	}
		
	public void run(Dialog dialog){
		
		DialogManagerContext context=DialogManagerContext.getInstance();
		context.setQuestionOpen(true);
		UserInterface ui = new ConsoleInterface();
		
		Task t=dialog.getTask("bsp"); //get a task and its associated ITOs
		ITOs itos=t.getITOs();
		
		//ask every ITO
		for(ITO ito : itos){
			
			context.setCurrentQuestion(ito); //point current question to this ITO
			
			String question=ito.ask(); //get question
			String user_answer=ui.exchange(question); //send question to user and receive answer
			
			UserUtterance answer=new UserUtterance(user_answer);
			
			//process user answer:
			//1. Soda
			sodarec.predict(answer,context); //identify dialog act (sets features and soda by reference)
			System.out.println("result: "+answer.getSoda());
			
			//2. Parsing
			ParseResults results=process(ito,answer.getText());
			logger.info(results.toString());
		}
		
	}
	
	private ParseResults process(ITO ito, String user_answer){
		return ito.parse(user_answer);
	}

}
