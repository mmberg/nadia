package net.mmberg.nadia.processor.ui;

import java.util.Date;

import net.mmberg.nadia.processor.dialogmodel.Dialog;
import net.mmberg.nadia.processor.exceptions.ProcessingException;

public interface UIConsumer {

	public abstract void loadDialog (Dialog dialog);
	public abstract String getDialogXml();
	public abstract UIConsumerMessage processUtterance(String userUtterance) throws ProcessingException;
	public abstract String getDebugInfo();
	public abstract void setAdditionalDebugInfo(String debuginfo);
	public abstract Date getLastAccess();
	
	public class UIConsumerMessage{
		private String systemUtterance;
		private Meta meta;
		public enum Meta{QUESTION, ANSWER, UNCHANGED, END_OF_DIALOG, ERROR};
		
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
	
}
