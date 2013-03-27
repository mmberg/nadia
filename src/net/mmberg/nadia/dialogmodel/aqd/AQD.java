package net.mmberg.nadia.dialogmodel.aqd;

public class AQD {

	//serializable members
	private AQDForm form;
	private AQDType type;
	private AQDContext context;
	
	//Serialization getter/setter
	public AQD(){
		
	}
	
	public void setType(AQDType type){
		this.type=type;
	}
	
	public AQDType getType(){
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
	
	//Content
	public AQD(AQDType type, AQDContext context, AQDForm form){
		this.type=type;
		this.form=form;
		this.context=context;
	}

}
