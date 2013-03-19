package net.mmberg.nadia.processor.nlu.soda.classification.features;

import java.util.Set;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public abstract class Feature {

	private String name="unnamed";
	
	public Feature(String name){
		this.name=name;
	}
	
	public void analyze(String utterance, DialogManagerContext context, Set<String> features){
		if(hasFeature(utterance,context)) features.add(name+"=yes");
		//else features.add(name+"=no");
	}

	protected abstract boolean hasFeature(String utterance, DialogManagerContext context);
}
