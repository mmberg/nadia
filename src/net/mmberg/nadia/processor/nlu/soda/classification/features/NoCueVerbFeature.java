package net.mmberg.nadia.processor.nlu.soda.classification.features;

public class NoCueVerbFeature extends Feature{
	
	public NoCueVerbFeature() {
		super("NoCueVerb");
	}

	@Override
	protected boolean hasFeature(String utterance) {
		boolean noActVerb=new ActReqVerbFeature().hasFeature(utterance);
		boolean noSeekVerb=new InfSeekVerbFeature().hasFeature(utterance);
		return (!noActVerb && !noSeekVerb);
	}
	
}
