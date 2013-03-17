package net.mmberg.nadia.processor.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import net.mmberg.nadia.dialogmodel.*;
import net.mmberg.nadia.dialogmodel.aqd.*;
import net.mmberg.nadia.processor.nlu.aqdparser.*;

public class DialogTest {

	private final static Logger logger = Logger.getLogger(DialogTest.class.getName()); 
	
	public static void main(String[] args){
//		//log to system.out instead system.err
//		SimpleFormatter fmt = new SimpleFormatter();
//		 StreamHandler sh = new StreamHandler(System.out, fmt);
//		 for(Handler h:logger.getHandlers()){
//			 logger.removeHandler(h);
//		 }
//		 logger.addHandler(sh);
		 
		new DialogTest().init();
	}
	
	private void init(){
		
		Parsers.init();
		
		//define a dialog
		//---------------
		
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
		
		
		runDialog(dialog);
	}
	
	private void runDialog(Dialog dialog){
		
		//ask system question
		//-------------------
		Task t=dialog.getTask("bsp");
		ITOs itos=t.getITOs();
		
		
		//ask every ITO
		for(ITO ito : itos){
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
			ParseResults results=process(ito,user_answer);
			logger.info(results.toString());
		}
		
	}
	
	private ParseResults process(ITO ito, String user_answer){
		return ito.parse(user_answer);
	}
}
