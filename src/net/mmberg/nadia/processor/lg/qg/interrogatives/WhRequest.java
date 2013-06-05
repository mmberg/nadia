package net.mmberg.nadia.processor.lg.qg.interrogatives;

import org.jdom.Element;

public class WhRequest extends Request{

	public WhRequest(){
		super("!");
	}
	
	public WhRequest(String punctuation){
		super(punctuation);
	}
	
	protected Element createWhRequest(boolean temp_opener, boolean politeness, String wh_word, String verb){
		Element node=createRequest(temp_opener, politeness);
		Element wh=new WhQuestion().createWh(false, false, wh_word, verb);
		
		addRel(node,"theme",wh);

		return node;
	}

	@Override
	public Element createLF(String wh_word, String verb, String noun, boolean temp_opener, boolean sayPlease, boolean subj) {
		return createWhRequest(temp_opener, sayPlease, wh_word, verb);
	}
	
}
