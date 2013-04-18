package net.mmberg.nadia.processor.dialogmodel;

import net.mmberg.nadia.dialogmodel.definition.TaskSelectorModel;

public abstract class TaskSelector extends TaskSelectorModel {

	public TaskSelector(){
		super();
	}
	
	public abstract boolean isResponsible(String utterance);
}
