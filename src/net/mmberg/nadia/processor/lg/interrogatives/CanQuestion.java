package net.mmberg.nadia.processor.lg.interrogatives;

import java.util.HashMap;

import org.jdom.Element;

public abstract class CanQuestion extends Interrogative{
	
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
