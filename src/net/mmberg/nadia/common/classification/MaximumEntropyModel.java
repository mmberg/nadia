package net.mmberg.nadia.common.classification;

import java.io.IOException;
import java.util.ArrayList;

import opennlp.maxent.GIS;
import opennlp.model.Event;
import opennlp.model.MaxentModel;

public class MaximumEntropyModel {
	
	public static boolean USE_SMOOTHING = false;
	public static double SMOOTHING_OBSERVATION = 0.1;
	private MaxentModel model;
	private boolean trained=false;
	
	public void train(ArrayList<Utterance> training_utterances){
		
		trained=false;
		ArrayList<Event> events=new ArrayList<Event>();
		
		for(Utterance utterance:training_utterances){
			String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
			Event event=new Event(utterance.getOutcome(),features);
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
	
	public String predict(Utterance utterance){
		if(trained){
				String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
				double[] ocs = model.eval(features);
				System.out.println("For utterance: '" +  utterance.getText() + "' and feats: " + utterance.getFeatures() + "\n" + model.getAllOutcomes(ocs) + " -> " + model.getBestOutcome(ocs) + "\n");
				return model.getBestOutcome(ocs);
		}
		return "";
	}
	
	public void predict(ArrayList<Utterance> utterances){
		if(trained){
			for(Utterance utterance:utterances){
				String[] features = utterance.getFeatures().toArray(new String[utterance.getFeatures().size()]);
				double[] ocs = model.eval(features);
				System.out.println("For utterance: '" +  utterance.getText() + "' and feats: " + utterance.getFeatures() + "\n" + model.getAllOutcomes(ocs) + " -> " + model.getBestOutcome(ocs) + "\n");
			}
		}
	}
	
}
