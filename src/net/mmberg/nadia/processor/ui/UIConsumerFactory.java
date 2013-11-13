package net.mmberg.nadia.processor.ui;

import net.mmberg.nadia.processor.dialogmodel.Dialog;
import net.mmberg.nadia.processor.exceptions.RuntimeError;

public abstract class UIConsumerFactory {

	public abstract UIConsumer create() throws RuntimeError;
	public abstract UIConsumer create(Dialog d) throws RuntimeError;
}
