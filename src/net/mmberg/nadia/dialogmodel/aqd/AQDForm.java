package net.mmberg.nadia.dialogmodel.aqd;

public class AQDForm {

	//TODO old Style.java -> adapt to AQD2
	private int politeness;
	private int formality;
	private boolean temporal_opener=false;
	
	public AQDForm(int politeness, int formality){
		this.politeness=politeness;
		this.formality=formality;
	}
	
	public AQDForm(){
		
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
	
	public void setTemporalOpener(boolean active){
		this.temporal_opener=active;
	}
	
	public boolean getTemporalOpener(){
		return this.temporal_opener;
	}


}
