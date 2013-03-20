package net.mmberg.nadia.processor.nlu.soda.classification.features;

import net.mmberg.nadia.processor.manager.DialogManagerContext;

public class QuestionOpenFeature extends Feature{

	public QuestionOpenFeature() {
		super("open");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		return context.isQuestionOpen();
	}

}
