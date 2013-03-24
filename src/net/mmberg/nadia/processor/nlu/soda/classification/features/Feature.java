package net.mmberg.nadia.processor.nlu.soda.classification.features;

import java.util.Set;

public abstract class Feature {

	private String name="unnamed";
	
	public Feature(String name){
		this.name=name;
	}
	
	public void analyze(String utterance, Set<String> features){
		if(hasFeature(utterance)) features.add(name+"=yes");
		//else features.add(name+"=no");
	}

	protected abstract boolean hasFeature(String utterance);
}
