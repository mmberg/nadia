package net.mmberg.nadia.processor.nlu.soda.features;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class QuestionOpenFeature extends Feature{

	public QuestionOpenFeature() {
		super("open");
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		return context.isQuestionOpen();
	}

}
