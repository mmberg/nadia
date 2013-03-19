package net.mmberg.nadia.processor.lg.interrogatives;

import org.jdom.Attribute;
import org.jdom.Element;

public class WhQuestion extends Interrogative{
	
	protected Element createWh(Boolean connective_opener, Boolean interrogative, String wh_word, String verb){
		Element node=createNode(wh_word);
		if (connective_opener) node.setAttribute(new Attribute("modifier", "connective-opener"));
		Element want_node = createNode("want");
		if (interrogative) want_node.setAttribute("mood","interrogative");
		addPronoun(want_node,"agent","sg","2nd");
		Element verb_node = createNode(verb);
		Element agent2_node=new Element("node");
		agent2_node.setAttribute("idref","x1");
		addRel(verb_node,"agent",agent2_node);
		addRel(want_node,"theme",verb_node);
		addRel(node, "prop", want_node);
		return node;
	}
	
	private Element createWhQuestion(Boolean connective_opener, String wh_word, String verb){	
		return createWh(connective_opener, true, wh_word, verb);
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean connective_opener, boolean sayPlease, boolean subj) {
		return createWhQuestion(connective_opener, wh_word, verb);
	}

}
