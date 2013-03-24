package net.mmberg.nadia.processor.nlu.soda.classification.features;

import java.util.Arrays;
import java.util.HashSet;

public class InfSeekVerbFeature extends Feature{

	HashSet<String> verbs=new HashSet<String>(Arrays.asList("say me", "tell me", "know", "name", "recommend"));
	
	public InfSeekVerbFeature() {
		super("InfSeekVerb");
	}

	@Override
	protected boolean hasFeature(String utterance) {
		for(String verb : verbs){
			if (utterance.contains(verb)) return true;
		}
		return false;
	}
	
}
