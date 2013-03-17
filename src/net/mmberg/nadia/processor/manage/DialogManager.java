package net.mmberg.nadia.processor.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.mmberg.nadia.processor.nlu.soda.*;
import net.mmberg.nadia.common.classification.*;
import net.mmberg.nadia.dialogmodel.*;

public class DialogManager {

	private Dialog dialog;
	private SodaRecognizer sodarec;
	
	public static void main(String[] args){
		DialogManager dm=new DialogManager();
		dm.run();
	}
	
	public DialogManager(){
		init();
	}
	
	private void init(){
		
		//Train Dialog Act Classifier
		sodarec=new SodaRecognizer();
		sodarec.train();
		
		/*
		//Dummy: setup dialog
		ITO ito = new ITO("question_destination",new DialogParser(),"Where do you want to go to?");
		DialogFrame frame = new DialogFrame();
		frame.put(ito, null);
		SubDialog subdialog = new SubDialog(frame, new Action());
		dialog = new Dialog();
		dialog.add(subdialog);
		*/
	}
	
	public void run(){
		
		DialogManagerContext context = DialogManagerContext.getInstance();
		context.setQuestionOpen(true);
		//context.setCurrentQuestion(ito);
		
		/*
		//Dummy: get next system question
		ITO ito=dialog.get(0).getFrame().keySet().iterator().next();
		
		System.out.println(ito.getQuestion());
		*/
		
		//get user answer
		String user_answer="";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			user_answer = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String act=sodarec.predict(new Utterance(user_answer),context); //identify dialog act
		System.out.println("result: "+act);
		
	}
}
