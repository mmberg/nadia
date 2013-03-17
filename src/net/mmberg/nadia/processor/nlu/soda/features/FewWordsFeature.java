package net.mmberg.nadia.processor.nlu.soda.features;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class FewWordsFeature extends Feature {

	public FewWordsFeature(){
		super("FewWords");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context){
		return (utterance.split(" ").length<=2);
	}

	
}
