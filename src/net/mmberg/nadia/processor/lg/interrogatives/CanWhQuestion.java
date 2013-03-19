package net.mmberg.nadia.processor.lg.interrogatives;

import org.jdom.Element;

public class CanWhQuestion extends CanQuestion{
	
	private Element createCanWhQuestion(String wh_word, String verb, boolean politeness, boolean subj, boolean opener){
		Element node=super.createBase(politeness, subj);
		Element wh = new WhRequest().createWhRequest(opener, politeness, wh_word, verb);
		addRel(node,"theme",wh);
		return node;
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean opener, boolean sayPlease, boolean subj) {
		return createCanWhQuestion(wh_word, verb, sayPlease, subj, opener);
	}

}
