package net.mmberg.nadia.processor.lg.dialog;

public class Meaning {

	private String type;
	private String specification;
	private String reference;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public Meaning(String type, String specification, String reference){
		this.type=type;
		this.specification=specification;
		this.reference=reference;
	}
}
