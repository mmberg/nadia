package net.mmberg.nadia.dialogmodel;

import javax.xml.bind.annotation.XmlSeeAlso;

import net.mmberg.nadia.dialogmodel.taskselector.BagOfWordsTaskSelector;

@XmlSeeAlso ({BagOfWordsTaskSelector.class})
public abstract class TaskSelector {

	public TaskSelector(){
		
	}
	
	public abstract boolean isResponsible(String utterance);
}
