package net.mmberg.nadia.processor.lg.interrogatives;

import org.jdom.Element;

public class NRequest extends Request{
	
	protected Element createNRequest(boolean temp_opener, boolean politeness, String theme){		
		Element node=createRequest(temp_opener, politeness);

		Element node4=createNode(theme); //e.g., destination
		//addPronoun(node4, "mod", "sg", "2nd"); //or: addDeterminer(node4, Flags.INDEF);
		addDeterminer(node4, Flags.PERSONAL);
		addRel(node,"theme", node4);

		return node;
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean opener, boolean sayPlease, boolean subj) {
		return createNRequest(opener, sayPlease, noun);
	}

}
