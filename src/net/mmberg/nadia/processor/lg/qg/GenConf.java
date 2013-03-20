package net.mmberg.nadia.processor.lg.qg;

/**
 * Language Generation Configuration Package (Meaning and Style) 
 * @author mberg
 *
 */
public class GenConf {

	private String wh_word;
	private String noun;
	private String verb;
	private int formality;
	private int politeness;
	private boolean opener;
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isOpener() {
		return opener;
	}

	public void setOpener(boolean opener) {
		this.opener = opener;
	}

	public int getPoliteness() {
		return politeness;
	}

	public void setPoliteness(int politeness) {
		this.politeness = politeness;
	}

	public int getFormality() {
		return formality;
	}

	public void setFormality(int formality) {
		this.formality = formality;
	}

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
	
	public GenConf(String type, String wh_word, String verb, String noun, int formality, int politeness, boolean opener){
		
		this.type=type;
		this.wh_word=wh_word;
		this.verb=verb;
		this.noun=noun;
		this.formality=formality;
		this.politeness=politeness;
		this.opener=opener;
	}
	
	@Override
	public String toString(){
		return "{politeness: "+politeness+", formality: "+formality+", "+wh_word+", "+noun+", "+verb+"}";
	}
	
}
