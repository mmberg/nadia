package net.mmberg.nadia.processor.lg.qg.interrogatives;

import org.jdom.Element;

public class YNQuestion extends Interrogative{

	//Do you have a customer card?
	
	public YNQuestion(){
		super("?");
	}
	
	public YNQuestion(String punctuation){
		super(punctuation);
	}
	protected Element createYNQuestion(String theme, String verb, boolean connective_opener){	
		Element node=createNode(verb);
		node.setAttribute("mood","interrogative");
		addPronoun(node,"agent","sg","2nd");
		Element pred=createNode(theme);
		addDeterminer(pred, Flags.INDEF);
		addRel(node,"theme",pred);
		return node;
	}
	
	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean opener, boolean sayPlease, boolean subj) {
		return createYNQuestion(noun, verb, opener);
	}


}
