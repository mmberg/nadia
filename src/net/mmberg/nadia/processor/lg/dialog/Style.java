package net.mmberg.nadia.processor.lg.dialog;

public class Style {

	private int politeness;
	private int formality;
	
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
	
	public Style(int politeness, int formality){
		this.politeness=politeness;
		this.formality=formality;
	}
}
