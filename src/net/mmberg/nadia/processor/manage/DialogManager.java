package net.mmberg.nadia.processor.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.aqdparser.Parsers;
import net.mmberg.nadia.processor.nlu.soda.*;
import net.mmberg.nadia.processor.nlu.soda.classification.SodaRecognizer;
import net.mmberg.nadia.utterance.UserUtterance;
import net.mmberg.nadia.dialogmodel.*;
import net.mmberg.nadia.dialogmodel.aqd.AQD;
import net.mmberg.nadia.dialogmodel.aqd.AQDType;

public class DialogManager {

	private SodaRecognizer sodarec;
	private final static Logger logger = Logger.getLogger(DialogManager.class.getName()); 
	
	public static void main(String[] args){
		DialogManager dm=new DialogManager();
		dm.run(dm.createDummyDialog());
	}
	
	public DialogManager(){
		init();
	}
	
	
	private void init(){
		
//		//log to system.out instead system.err
//		SimpleFormatter fmt = new SimpleFormatter();
//		 StreamHandler sh = new StreamHandler(System.out, fmt);
//		 for(Handler h:logger.getHandlers()){
//			 logger.removeHandler(h);
//		 }
//		 logger.addHandler(sh);
		
		//Train Dialog Act Classifier
		sodarec=new SodaRecognizer();
		sodarec.train();
		
		//init Parsers
		Parsers.init();
	}
	
	private Dialog createDummyDialog(){
		
		Dialog dialog = new Dialog();
				
		//a dialog consists of tasks
		Task task1=new Task("bsp");
		dialog.addTask(task1);
		
		//a task consists of ITOs
		ITO ito;
		AQD aqd;
		
		//1
		ito=new ITO("getDrinkDecision", "Would you like a drink?");	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setAQDType(new AQDType("decision.yn"));
		ito.setAQD(aqd);	
		
		//2
		ito=new ITO("getCity", "Where do you want to go?");	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setAQDType(new AQDType("fact.location.city"));
		ito.setAQD(aqd);	
		
		//3
		ito=new ITO("getDate", "When do you want to leave?");	
		task1.addITO(ito);
		aqd=new AQD();
		aqd.setAQDType(new AQDType("fact.temporal.date"));
		ito.setAQD(aqd);	
		
		return dialog;
				
	}
	
	private void run(Dialog dialog){
		
		DialogManagerContext context=DialogManagerContext.getInstance();
		context.setQuestionOpen(true);
		
		//ask system question
		//-------------------
		Task t=dialog.getTask("bsp");
		ITOs itos=t.getITOs();
		
		
		//ask every ITO
		for(ITO ito : itos){
			
			context.setCurrentQuestion(ito); //point current question to this ITO
			
			String question="";
			question=ito.ask();
			
			System.out.println(question);
			
			//get user answer
			//---------------
			String user_answer="";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				user_answer = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//process user answer
			//-------------------
			
			UserUtterance answer=new UserUtterance(user_answer);
			sodarec.predict(answer,context); //identify dialog act (sets features and soda by reference)
			System.out.println("result: "+answer.getSoda());
			
			ParseResults results=process(ito,user_answer);
			logger.info(results.toString());
		}
		
	}
	
	private ParseResults process(ITO ito, String user_answer){
		return ito.parse(user_answer);
	}

}
