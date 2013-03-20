package net.mmberg.nadia.processor.manager;

import java.util.logging.Logger;

import net.mmberg.nadia.Nadia;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.ui.ConsoleInterface;
import net.mmberg.nadia.ui.UserInterface;
import net.mmberg.nadia.utterance.UserUtterance;
import net.mmberg.nadia.dialogmodel.*;

public class DialogManager {

	private SodaRecognizer sodarec;
	private final static Logger logger = Nadia.getLogger(); 
	
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
			sodarec.predict(answer,context); //identify dialog act (sets features and soda by reference), access result: answer.getSoda()
			ParseResults results=interpret(ito,answer.getText()); //Parsing
					
		}
		
	}
	
	private ParseResults interpret(ITO ito, String user_answer){
		ParseResults results=ito.parse(user_answer, true);
		if(results.size()>0) logger.info(results.toString()); else logger.warning("no parser matched");
		return results;
	}

}
