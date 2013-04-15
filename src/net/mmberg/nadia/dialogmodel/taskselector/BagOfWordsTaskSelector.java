package net.mmberg.nadia.dialogmodel.taskselector;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import net.mmberg.nadia.dialogmodel.TaskSelector;

public class BagOfWordsTaskSelector extends TaskSelector{

	private ArrayList<String> words;
	
	@XmlElementWrapper(name="bagOfWords")
	@XmlElement(name="word")
	public ArrayList<String> getWords() {
		return words;
	}

	public void setWords(ArrayList<String> words) {
		this.words = words;
	}

	public BagOfWordsTaskSelector(){
		
	}
	
	public BagOfWordsTaskSelector(ArrayList<String> words){
		this.words=words;
	}
	
	@Override
	public boolean isResponsible(String utterance) {
		for (String word:words) 
			if (utterance.contains(word)) return true;
		return false;
	}

	
}
