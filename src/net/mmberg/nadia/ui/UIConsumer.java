package net.mmberg.nadia.ui;

import net.mmberg.nadia.dialogmodel.Dialog;

public interface UIConsumer {

	
	
	public class UIConsumerMessage{
		private String systemUtterance;
		private Meta meta;
		public enum Meta{QUESTION, ANSWER, UNCHANGED, END_OF_DIALOG};
		
		public UIConsumerMessage(String systemUtterance, Meta meta){
			this.systemUtterance=systemUtterance;
			this.meta=meta;
		}
		
		public String getSystemUtterance(){
			return systemUtterance;
		}
		
		public Meta getMeta(){
			return meta;
		}
	}
	
	public abstract void loadDialog (Dialog dialog);
	public abstract UIConsumerMessage processUtterance(String userUtterance);
	public abstract String getDebugInfo();
	
}
