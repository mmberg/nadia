package net.mmberg.nadia.dialogmodel.aqd;

public class AQDContext {

	//TODO old Meaning.java -> adapt to AQD2
		private String specification;
		private String reference;

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
		
		public AQDContext(String specification, String reference){
			this.specification=specification;
			this.reference=reference;
		}
		
}
