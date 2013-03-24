package net.mmberg.nadia.processor.nlu.soda.classification.features;

public class NoCueVerbAndNoWhFeature extends Feature{
	
	public NoCueVerbAndNoWhFeature() {
		super("NoCueVerbAndNoWh");
	}

	@Override
	protected boolean hasFeature(String utterance) {
		boolean noActVerb=new ActReqVerbFeature().hasFeature(utterance);
		boolean noSeekVerb=new InfSeekVerbFeature().hasFeature(utterance);
		boolean noWhWord=new WhWordFeature().hasFeature(utterance);
		return (!noActVerb && !noSeekVerb && !noWhWord);
	}
	
}
