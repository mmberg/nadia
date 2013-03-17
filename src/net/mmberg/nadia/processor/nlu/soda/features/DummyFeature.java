package net.mmberg.nadia.processor.nlu.soda.features;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class DummyFeature extends Feature{
	
	public DummyFeature() {
		super("dummy");
	}

	@Override
	protected boolean hasFeature(String utterance , DialogManagerContext context) {
		return true;
	}

}
