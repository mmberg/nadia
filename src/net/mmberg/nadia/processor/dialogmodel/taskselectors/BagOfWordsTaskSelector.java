package net.mmberg.nadia.processor.dialogmodel.taskselectors;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import net.mmberg.nadia.dialogmodel.definition.taskselectors.BagOfWordsTaskSelectorModel;

@XmlRootElement
public class BagOfWordsTaskSelector extends BagOfWordsTaskSelectorModel{
	
	public BagOfWordsTaskSelector(){
		super();
	}
	
	public BagOfWordsTaskSelector(ArrayList<String> words){
		super(words);
	}
	
	@Override
	public boolean isResponsible(String utterance) {
		for (String word:words) 
			if (utterance.contains(word)) return true;
		return false;
	}

	
}
