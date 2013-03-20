package net.mmberg.nadia.dialogmodel.aqd;

public class AQD {

	private AQDForm form;
	private AQDType type;
	private AQDContext context;
	
	public AQD(){
		
	}
	
	public AQD(AQDType type, AQDContext context, AQDForm form){
		this.type=type;
		this.form=form;
		this.context=context;
	}
	
	
	public void setAQDType(AQDType type){
		this.type=type;
	}
	
	public AQDType getAQDType(){
		return type;
	}

	public AQDForm getForm() {
		return form;
	}

	public void setForm(AQDForm form) {
		this.form = form;
	}

	public AQDContext getContext() {
		return context;
	}

	public void setContext(AQDContext context) {
		this.context = context;
	}
	

}
