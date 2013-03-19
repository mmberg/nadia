package net.mmberg.nadia.processor.lg;

public class WordConf {

	private String wh_word;
	private String noun;
	private String verb;
	public String getWhWord() {
		return wh_word;
	}
	
	public void setWhWord(String wh_word) {
		this.wh_word = wh_word;
	}
	public String getNoun() {
		return noun;
	}
	public void setNoun(String noun) {
		this.noun = noun;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	
	public WordConf(String wh_word, String verb, String noun){
		this.wh_word=wh_word;
		this.verb=verb;
		this.noun=noun;
	}
	
}
