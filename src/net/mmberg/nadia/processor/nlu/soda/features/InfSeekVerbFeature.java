package net.mmberg.nadia.processor.nlu.soda.features;

import java.util.Arrays;
import java.util.HashSet;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class InfSeekVerbFeature extends Feature{

	HashSet<String> verbs=new HashSet<String>(Arrays.asList("say me", "tell me", "know", "name", "recommend"));
	
	public InfSeekVerbFeature() {
		super("InfSeekVerb");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		for(String verb : verbs){
			if (utterance.contains(verb)) return true;
		}
		return false;
	}
	
}
