package net.mmberg.nadia.processor.nlu.soda.classification.features;

import net.mmberg.nadia.processor.manager.DialogManagerContext;

public class DummyFeature extends Feature{
	
	public DummyFeature() {
		super("dummy");
	}

	@Override
	protected boolean hasFeature(String utterance , DialogManagerContext context) {
		return true;
	}

}
