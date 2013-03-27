package net.mmberg.nadia.dialogmodel.aqd;

public class AQDContext {

		//TODO old Meaning.java -> adapt to AQD2
	
		//serializable members
		private String specification;
		private String reference;

		//Serialization getter/setter
		public AQDContext(){
			
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
		
		
		//Content
		public AQDContext(String specification, String reference){
			this.specification=specification;
			this.reference=reference;
		}
		
}
