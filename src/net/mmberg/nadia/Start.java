package net.mmberg.nadia;

import java.util.logging.Logger;

import net.mmberg.nadia.dialogmodel.Dialog;
import net.mmberg.nadia.processor.manage.DialogManager;
import net.mmberg.nadia.store.DialogStore;

public class Start {

	
	private final static Logger logger = Logger.getLogger(DialogManager.class.getName()); 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		//log to system.out instead system.err
//		SimpleFormatter fmt = new SimpleFormatter();
//		 StreamHandler sh = new StreamHandler(System.out, fmt);
//		 for(Handler h:logger.getHandlers()){
//			 logger.removeHandler(h);
//		 }
//		 logger.addHandler(sh);
		
		Dialog dialog = DialogStore.getInstance().getDialog("dummy1");
		DialogManager manager = new DialogManager();
		manager.run(dialog);
	}

}
