package net.mmberg.nadia.processor.lg.interrogatives;

import org.jdom.Attribute;
import org.jdom.Element;

public class N extends Interrogative{
	
	protected Element createN(boolean politeness, boolean opener, String theme){		
		Element node=createNode(theme); //e.g., destination
		if (opener) {
			node.setAttribute(new Attribute("modifier", "connective-opener"));
			addDeterminer(node, Flags.DEF);
		}
		if (politeness) node.setAttribute(new Attribute("politeness", "yes"));
		
		return node;
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean opener, boolean sayPlease, boolean subj) {
		return createN(sayPlease, opener, noun);
	}

}
