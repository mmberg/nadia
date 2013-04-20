package net.mmberg.nadia.processor.ui;

import net.mmberg.nadia.processor.dialogmodel.Dialog;

public abstract class UIConsumerFactory {

	public abstract UIConsumer create();
	public abstract UIConsumer create(Dialog d);
}
