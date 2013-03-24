package net.mmberg.nadia.dialogmodel.aqd;

public class AQDForm {

	//TODO old Style.java -> adapt to AQD2
	private Integer politeness;
	private Integer formality;
	private boolean temporal_opener=false;
	
	public AQDForm(Integer politeness, Integer formality){
		this.politeness=politeness;
		this.formality=formality;
	}
	
	public AQDForm(){
		
	}
	
	
	public Integer getPoliteness() {
		return politeness;
	}
	public void setPoliteness(Integer politeness) {
		this.politeness = politeness;
	}
	public Integer getFormality() {
		return formality;
	}
	public void setFormality(Integer formality) {
		this.formality = formality;
	}
	
	public void setTemporalOpener(boolean active){
		this.temporal_opener=active;
	}
	
	public boolean getTemporalOpener(){
		return this.temporal_opener;
	}


}
