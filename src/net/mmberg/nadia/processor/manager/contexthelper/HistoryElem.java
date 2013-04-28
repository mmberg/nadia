package net.mmberg.nadia.processor.manager.contexthelper;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import net.mmberg.nadia.processor.manager.DialogManagerContext.UTTERANCE_TYPE;

public class HistoryElem{
	
	private String utterance;
	private UTTERANCE_TYPE speaker;
	
	@XmlAttribute
	public int taskLevel;

	
	public HistoryElem(){
	}
	
	public HistoryElem(String utterance, int level, UTTERANCE_TYPE speaker){
		this.utterance=utterance;
		this.taskLevel=level;
		this.speaker=speaker;
	}
	
	@XmlValue
	public String getUtterance(){
		return utterance;
	}
	
	@XmlAttribute
	public String getType(){
		return (speaker==UTTERANCE_TYPE.SYSTEM)?"S":"U";
	}

}