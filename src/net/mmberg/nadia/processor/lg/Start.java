package net.mmberg.nadia.processor.lg;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.mmberg.nadia.processor.lg.ccg.Generator;
import net.mmberg.nadia.processor.lg.dialog.Meaning;

public class Start {

	private Generator gen;
	private static Logger logger=Logger.getLogger("net.mmberg.qg");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.setLevel(Level.WARNING);
		Start start=new Start();
		start.test();
	}
	
	public Start(){
		try{
			gen = new Generator(new URL("file:///"+System.getProperty("user.dir")+"/res/ccggrammar/grammar.xml"), new URL("file:///"+System.getProperty("user.dir")+"/res/ontology/lexicon.owl"));
		}
		catch(MalformedURLException ex){
			ex.printStackTrace();
		}
	}
	
	public void test(){
		
		ArrayList<Meaning> questions=new ArrayList<Meaning>();
		//dimension, semantic constraint, referent
		questions.add(new Meaning("fact.temporal.date", "begin", "trip")); //date of the begin of the trip
		questions.add(new Meaning("fact.temporal.date", "end", "trip")); //date of the end of the trip
		questions.add(new Meaning("fact.named_entity.non_animated.location.city", "begin", "trip")); //location of the begin of the trip
		questions.add(new Meaning("fact.named_entity.non_animated.location.city", "end", "trip"));
		questions.add(new Meaning("decision", "possession", "customer_card")); //decision about the possession of a card
		
		realiseDialogue(questions, 4, 4, true); //formality, politeness, variation
		
		
		
//		//Low-Level: Create what you want
//		WordConf wconf=gen.chooseWords("location", "begin", "trip", 2);
//		System.out.println(gen.generateQuestion(new N(), wconf, true, true, false));

	}
	
	
	public void realiseDialogue(ArrayList<Meaning> questions, int intended_formality, int intended_politeness, boolean variate){
		
		for(int i=0; i<questions.size(); i++){
			Meaning question=questions.get(i);
			
			//openers (and/now) only if not the first question and only every second question
			boolean opener=((i+1)%2==1)?false:true; //i==0
			
			int politeness=intended_politeness;
	
			if(variate){
				if(intended_politeness>=-1 && intended_politeness <=4){
					//politeness variation: pol,pol+1,pol-1
					int variation=((i+1)%3)-1; //add 0,1,-1,...
					politeness=intended_politeness+variation;
				}
			}
					
			//realize
			//gen.printParaphrases(question.getType(), question.getSpecification(), question.getReference(), politeness, intended_formality, opener);
			String utterance=gen.generateQuestion(question, politeness, intended_formality, opener);
			System.out.println(utterance);

		}
	}
	
}
