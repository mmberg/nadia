package net.mmberg.nadia.processor.nlu.soda.classification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.mmberg.nadia.Nadia;
import net.mmberg.nadia.utterance.TrainingUtterance;
import net.mmberg.nadia.utterance.UserUtterance;

import opennlp.maxent.GIS;
import opennlp.model.Event;
import opennlp.model.MaxentModel;

public class MaximumEntropyModel {
	
	public static boolean USE_SMOOTHING = false;
	public static double SMOOTHING_OBSERVATION = 0.1;
	private MaxentModel model;
	private boolean trained=false;
	private final static Logger logger = Nadia.getLogger();
	
	public boolean isTrained(){
		return trained;
	}
	
	public void train(ArrayList<TrainingUtterance> training_utterances){
		
		trained=false;
		ArrayList<Event> events=new ArrayList<Event>();
		
		for(TrainingUtterance utterance:training_utterances){
			String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
			Event event=new Event(utterance.getExpectedOutcome(),features);
			events.add(event);
		}
		
		ListEventStream eventStream = new ListEventStream(events);
		
		GIS.SMOOTHING_OBSERVATION = SMOOTHING_OBSERVATION;
		GIS.PRINT_MESSAGES = false;
	    try {
			model = GIS.trainModel(eventStream,USE_SMOOTHING);
			
			trained=true;
			
		} catch (IOException e) {
			model=null;
			trained=false;
			e.printStackTrace();
		}
	}
	
	public String predict(UserUtterance utterance){
		if(trained){
				String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
				double[] ocs = model.eval(features);
				logger.info("Utterance: '" +  utterance.getText() + "' and feats " + utterance.getFeatures() + "\n   resulted in: " + model.getAllOutcomes(ocs) + " -> " + model.getBestOutcome(ocs));
				return model.getBestOutcome(ocs);
		}
		return "untrained";
	}
	
	public void predict(ArrayList<UserUtterance> utterances){
		if(trained){
			for(UserUtterance utterance:utterances){
				String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
				double[] ocs = model.eval(features);
				logger.info("Utterance: '" +  utterance.getText() + "' and feats " + utterance.getFeatures() + "\n   resulted in: " + model.getAllOutcomes(ocs) + " -> " + model.getBestOutcome(ocs));
			}
		}
	}
	
}
