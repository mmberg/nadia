package net.mmberg.nadia.processor.lg.interrogatives;

import org.jdom.Element;

public class CanNQuestion extends CanQuestion {
	
	protected Element createCanNQuestion(String theme, boolean politeness, boolean subj, boolean temp_opener){	
		Element node=super.createBase(politeness, subj);
		Element wh = new NRequest().createNRequest(temp_opener, politeness, theme);
		addRel(node,"theme",wh);
		return node;
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun,
			boolean temp_opener, boolean sayPlease, boolean subj) {
		return createCanNQuestion(noun, sayPlease, subj, temp_opener);
	}

}
