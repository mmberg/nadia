package net.mmberg.nadia.processor.nlu.soda.classification.features;

import net.mmberg.nadia.processor.manager.DialogManagerContext;

public class NoCueVerbFeature extends Feature{
	
	public NoCueVerbFeature() {
		super("NoCueVerb");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		boolean noActVerb=new ActReqVerbFeature().hasFeature(utterance,context);
		boolean noSeekVerb=new InfSeekVerbFeature().hasFeature(utterance,context);
		return (!noActVerb && !noSeekVerb);
	}
	
}
