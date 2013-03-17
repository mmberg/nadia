package net.mmberg.nadia.dialogmodel.aqd;

public class AQD {

	private AQDForm form;
	private AQDType type;
	private AQDContext context;
	
	public void setAQDType(AQDType type){
		this.type=type;
	}
	
	public AQDType getAQDType(){
		return type;
	}
}
