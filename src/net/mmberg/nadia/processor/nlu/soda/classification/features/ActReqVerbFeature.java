package net.mmberg.nadia.processor.nlu.soda.classification.features;

import java.util.Arrays;
import java.util.HashSet;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class ActReqVerbFeature extends Feature{

	HashSet<String> verbs=new HashSet<String>(Arrays.asList("switch", "turn", "close", "open"));
	
	public ActReqVerbFeature() {
		super("ActReqVerb");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		for(String verb : verbs){
			if (utterance.contains(verb)) return true;
		}
		return false;
	}
	
}
