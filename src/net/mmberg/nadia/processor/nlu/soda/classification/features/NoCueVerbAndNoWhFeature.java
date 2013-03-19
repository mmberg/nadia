package net.mmberg.nadia.processor.nlu.soda.classification.features;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class NoCueVerbAndNoWhFeature extends Feature{
	
	public NoCueVerbAndNoWhFeature() {
		super("NoCueVerbAndNoWh");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		boolean noActVerb=new ActReqVerbFeature().hasFeature(utterance,context);
		boolean noSeekVerb=new InfSeekVerbFeature().hasFeature(utterance,context);
		boolean noWhWord=new WhWordFeature().hasFeature(utterance,context);
		return (!noActVerb && !noSeekVerb && !noWhWord);
	}
	
}
