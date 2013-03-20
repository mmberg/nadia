package net.mmberg.nadia.processor.lg.qg;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.mmberg.nadia.Nadia;
import net.mmberg.nadia.dialogmodel.aqd.AQD;
import net.mmberg.nadia.dialogmodel.aqd.AQDContext;
import net.mmberg.nadia.dialogmodel.aqd.AQDForm;
import net.mmberg.nadia.dialogmodel.aqd.AQDType;

public class GeneratorTest {

	private Generator gen;
	private final static Logger logger = Nadia.getLogger();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeneratorTest start=new GeneratorTest();
		start.test();
	}
	
	public GeneratorTest(){
		try{
			gen = new Generator(new URL("file:///"+System.getProperty("user.dir")+"/res/ccggrammar/grammar.xml"), new URL("file:///"+System.getProperty("user.dir")+"/res/ontology/lexicon.owl"));
		}
		catch(MalformedURLException ex){
			ex.printStackTrace();
		}
	}
	
	public void test(){
		
		ArrayList<AQD> questions=new ArrayList<AQD>();
		int politeness=4;
		int formality=4;
		
		questions.add(new AQD(new AQDType("fact.temporal.date"), new AQDContext("begin","trip"), new AQDForm(politeness, formality))); //date of the begin of the trip
		questions.add(new AQD(new AQDType("fact.temporal.date"), new AQDContext("end","trip"), new AQDForm(politeness, formality)));
		questions.add(new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("begin","trip"), new AQDForm(politeness, formality)));
		questions.add(new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("end","trip"), new AQDForm(politeness, formality)));
		questions.add(new AQD(new AQDType("decision"), new AQDContext("possession", "customer_card"), new AQDForm(politeness, formality))); //decision about the possession of a card
		
		realiseDialogue(questions, true);
		
//		//Low-Level: Create what you want
//		WordConf wconf=gen.chooseWords("location", "begin", "trip", 2);
//		System.out.println(gen.generateQuestion(new N(), wconf, true, true, false));

	}
	
	
	public void realiseDialogue(ArrayList<AQD> questions, boolean variate){
		
		for(int i=0; i<questions.size(); i++){
			AQD question=questions.get(i);
			
			//openers (and/now) only if not the first question and only every second question
			boolean opener=((i+1)%2==1)?false:true; //i==0
			question.getForm().setTemporalOpener(opener);
			
			int intended_politeness=question.getForm().getPoliteness();
			int politeness=intended_politeness;
	
			if(variate){
				if(intended_politeness>=-1 && intended_politeness <=4){
					//politeness variation: pol,pol+1,pol-1
					int variation=((i+1)%3)-1; //add 0,1,-1,...
					politeness=intended_politeness+variation;
				}
			}
			
			question.getForm().setPoliteness(politeness);
					
			//realize
			//gen.printParaphrases(question.getType(), question.getSpecification(), question.getReference(), politeness, intended_formality, opener);
			String utterance=gen.generateQuestion(question);
			System.out.println(utterance);

		}
	}
	
}
