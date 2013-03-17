package net.mmberg.nadia.processor.nlu.soda;

import java.util.ArrayList;
import java.util.Arrays;

import net.mmberg.nadia.common.classification.*;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.soda.features.*;
import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class SodaRecognizer {

	private MaximumEntropyModel model;
	
	public SodaRecognizer(){
		
	}
		

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SodaRecognizer sr= new SodaRecognizer();
		sr.train();
		sr.test_predict();
	}
	
	
	public void train(){
		ArrayList<Utterance> training_utterances=new ArrayList<Utterance>(Arrays.asList(
				//seek:
				new Utterance("How is the weather in Paris","seek"),
				new Utterance("Tell me the weather in Paris","seek"),
				new Utterance("Where do you want to go","seek"),
				new Utterance("What is your destination","seek"),
				new Utterance("Please tell me where you want to go","seek"),
				new Utterance("Please tell me your destination","seek"),
				//action:
				new Utterance("Could you please turn the light on","action"),
				new Utterance("Turn the light on","action"),
				new Utterance("Please switch the light off","action"),
				new Utterance("Can you switch the light off","action"),
				new Utterance("Could you turn off the computer","action"),
				//prov:
				new Utterance("To London","prov"),
				new Utterance("I want to go to London","prov"),
				new Utterance("Paris","prov"),
				new Utterance("I'd like to go to Paris","prov")
			));
		
		extractFeatures(training_utterances); //adds features to utterances (by reference); required by MaximumEntropyModel
		model = new MaximumEntropyModel();
		model.train(training_utterances);
	}
	
	public String predict(Utterance utterance, DialogManagerContext context){
		extractFeature(utterance);
		String act=(model!=null)?model.predict(utterance):"";
		
		//Post-Processing
		if(act.equals("prov")){
			//if no question is open or if the answer cannot be parsed with the current question, make it a seeking act
			if(!context.isQuestionOpen() || 
					(context.getCurrentQuestion().parse(utterance.getText()).getState()==ParseResults.NOMATCH)){
				act="seek";
				System.out.println("Postprocessing: prov -> seek");
			}
		}
		
		return act;
	}
	
	public void test_predict(){
		ArrayList<Utterance> test_utterances=new ArrayList<Utterance>(Arrays.asList(
				new Utterance("Could you recommend a hotel for next week"),
				new Utterance("Close the window"),
				new Utterance("When do you want to come back"),
				new Utterance("Can you close the door"),
				new Utterance("Paris is nice"),
				new Utterance("Two adults please"),
				new Utterance("I want to have a non smokers room"),
				new Utterance("How much would that be"),
				new Utterance("Can I have a double room"),
				new Utterance("Three persons"),
				new Utterance("London would be great"),
				//new Utterance("What about London"),
				new Utterance("I really prefer London"),
				//new Utterance("What the hell, London it is"), //tricky
				new Utterance("I want to know about hotels in London")//could also be an answer to "What accommodation may I book for you?"
			));
		
		extractFeatures(test_utterances);
		model.predict(test_utterances);
	}

	public void extractFeature(Utterance utterance){
		ArrayList<Utterance> utterances=new ArrayList<Utterance>();
		utterances.add(utterance);
		extractFeatures(utterances);
	}
	
	public void extractFeatures(ArrayList<Utterance> utterances){
		
		//Context
		DialogManagerContext context=DialogManagerContext.getInstance();
		context.setQuestionOpen(true);
				
		//Select Feature Extractors
		ArrayList<Feature> featureExtractors=new ArrayList<Feature>(Arrays.asList(
				new DummyFeature(),
				new ActReqVerbFeature(),
				new ConditionalFeature(),
				new InfSeekVerbFeature(),
				new InterrogativeFeature(),
				new WhWordFeature(),
				new NoCueVerbFeature(),
				new NoCueVerbAndNoWhFeature(),
				new FewWordsFeature()
		));
		
		
		//Extract Features
		for(Utterance utterance : utterances){
			for(Feature extractor : featureExtractors){
				extractor.analyze(utterance.getText().toLowerCase(), context, utterance.getFeatures());
			}
			System.out.println(utterance.getFeatures());	
		}
		
	}

}
