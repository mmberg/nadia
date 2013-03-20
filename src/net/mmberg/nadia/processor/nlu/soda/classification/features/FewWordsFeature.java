package net.mmberg.nadia.processor.nlu.soda.classification.features;

import net.mmberg.nadia.processor.manager.DialogManagerContext;

public class FewWordsFeature extends Feature {

	public FewWordsFeature(){
		super("FewWords");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context){
		return (utterance.split(" ").length<=2);
	}

	
}
