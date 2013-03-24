package net.mmberg.nadia.processor.nlu.soda.classification.features;


public class DummyFeature extends Feature{
	
	public DummyFeature() {
		super("dummy");
	}

	@Override
	protected boolean hasFeature(String utterance) {
		return true;
	}

}
