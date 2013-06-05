package net.mmberg.nadia.processor.nlu.soda.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.nlu.aqdparser.ParseResults;
import net.mmberg.nadia.processor.nlu.soda.Soda;
import net.mmberg.nadia.processor.nlu.soda.classification.features.*;
import net.mmberg.nadia.processor.utterance.TrainingUtterance;
import net.mmberg.nadia.processor.utterance.UserUtterance;
import net.mmberg.nadia.processor.manager.DialogManagerContext;

public class SodaRecognizer {

	private static SodaRecognizer instance;
	private MaximumEntropyModel model;
	private final static Logger logger = NadiaProcessor.getLogger();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SodaRecognizer sr= new SodaRecognizer();
		sr.train();
		sr.test_predict();
	}
	
	private SodaRecognizer(){
		
	}
	
	public static SodaRecognizer getInstance(){
		if(instance==null){
			instance=new SodaRecognizer();
		}
		return instance;
	}
	
	
	public boolean isTrained(){
		return(model!=null);
	}
	
	public void train(){

		ArrayList<TrainingUtterance> training_utterances=new ArrayList<TrainingUtterance>(Arrays.asList(
				//seek:
				new TrainingUtterance("How is the weather in Paris", Soda.INFORMATION_SEEKING),
				new TrainingUtterance("Tell me the weather in Paris",Soda.INFORMATION_SEEKING),
				new TrainingUtterance("Where do you want to go",Soda.INFORMATION_SEEKING),
				new TrainingUtterance("What is your destination",Soda.INFORMATION_SEEKING),
				new TrainingUtterance("Please tell me where you want to go",Soda.INFORMATION_SEEKING),
				new TrainingUtterance("Please tell me your destination",Soda.INFORMATION_SEEKING),
				//action:
				new TrainingUtterance("Could you please turn the light on",Soda.ACTION_REQUESTING),
				new TrainingUtterance("Turn the light on",Soda.ACTION_REQUESTING),
				new TrainingUtterance("Please switch the light off",Soda.ACTION_REQUESTING),
				new TrainingUtterance("Can you switch the light off",Soda.ACTION_REQUESTING),
				new TrainingUtterance("Could you turn off the computer",Soda.ACTION_REQUESTING),
				//prov:
				new TrainingUtterance("To London",Soda.INFORMATION_PROVIDING),
				new TrainingUtterance("I want to go to London",Soda.INFORMATION_PROVIDING),
				new TrainingUtterance("Paris",Soda.INFORMATION_PROVIDING),
				new TrainingUtterance("I'd like to go to Paris",Soda.INFORMATION_PROVIDING)
			));
		
		extractFeatures(training_utterances); //adds features to utterances (by reference); required by MaximumEntropyModel
		model = new MaximumEntropyModel();
		model.train(training_utterances);
	}
	
	public void predict(UserUtterance utterance, DialogManagerContext context){
		extractFeature(utterance);
		String act=(model!=null)?model.predict(utterance):"unknown";
		
		//moved to DialogManager because we need to check all ITOs, not just one
//		//Post-Processing
//		if(act.equals(Soda.INFORMATION_PROVIDING)){
//			//if no question is open or if the answer cannot be parsed with the current question, make it a seeking act
//			ParseResults res = context.getCurrentQuestion().parse(utterance.getText(),true);
//			

//			if( !context.isQuestionOpen() || (res.getState()==ParseResults.NOMATCH))
//			{
//				act=Soda.INFORMATION_SEEKING;
//				logger.info("Postprocessing (open:"+context.isQuestionOpen()+", parseState:"+res.getState()+"): prov -> seek");
//			}
//		}
//		
		utterance.setSoda(act);
	}
	
	
	public void test_predict(){
		ArrayList<UserUtterance> test_utterances=new ArrayList<UserUtterance>(Arrays.asList(
				new UserUtterance("Could you recommend a hotel for next week"),
				new UserUtterance("Close the window"),
				new UserUtterance("When do you want to come back"),
				new UserUtterance("Can you close the door"),
				new UserUtterance("Paris is nice"),
				new UserUtterance("Two adults please"),
				new UserUtterance("I want to have a non smokers room"),
				new UserUtterance("How much would that be"),
				new UserUtterance("Can I have a double room"),
				new UserUtterance("Three persons"),
				new UserUtterance("London would be great"),
				//new UserUtterance("What about London"),
				new UserUtterance("I really prefer London"),
				//new UserUtterance("What the hell, London it is"), //tricky
				new UserUtterance("I want to know about hotels in London")//could also be an answer to "What accommodation may I book for you?"
			));
		
		extractFeatures(test_utterances);
		model.predict(test_utterances);
	}

	public void extractFeature(UserUtterance utterance){
		ArrayList<UserUtterance> utterances=new ArrayList<UserUtterance>();
		utterances.add(utterance);
		extractFeatures(utterances);
	}
	
	public void extractFeatures(ArrayList<? extends UserUtterance> utterances){
					
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
		for(UserUtterance utterance : utterances){
			for(Feature extractor : featureExtractors){
				extractor.analyze(utterance.getText().toLowerCase(), utterance.getFeatures());
			}
		}
		
	}

}
