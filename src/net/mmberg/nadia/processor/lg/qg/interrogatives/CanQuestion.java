package net.mmberg.nadia.processor.lg.qg.interrogatives;

import java.util.HashMap;

import org.jdom.Element;

public abstract class CanQuestion extends Interrogative{
	
	public CanQuestion(){
		super("?");
	}
	
	public CanQuestion(String punctuation){
		super(punctuation);
	}
	
	protected Element createBase(boolean politeness, boolean subj){
		Element node;
		if(subj){
			HashMap<String, String> atts=new HashMap<String,String>();
			atts.put("mood", "subj");
			node=createNode("can",atts);
		}
		else node=createNode("can");;
		addPronoun(node, "actor", "sg", "2nd");
		return node;
	}

}
