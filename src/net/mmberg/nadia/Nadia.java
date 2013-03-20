package net.mmberg.nadia;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.mmberg.nadia.dialogmodel.Dialog;
import net.mmberg.nadia.processor.manager.DialogManager;
import net.mmberg.nadia.store.DialogStore;

public class Nadia {

	
	private final static Logger logger = Logger.getLogger("nina"); 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new Nadia().init();
		
		Dialog dialog = DialogStore.getInstance().getDialog("dummy1");
		DialogManager manager = new DialogManager();
		manager.run(dialog);
	}
	
	private void init(){
//		//log to system.out instead system.err
//		SimpleFormatter fmt = new SimpleFormatter();
//		 StreamHandler sh = new StreamHandler(System.out, fmt);
//		 for(Handler h:logger.getHandlers()){
//			 logger.removeHandler(h);
//		 }
//		 logger.addHandler(sh);
		
		logger.setLevel(Level.INFO);
	}
	
	public static Logger getLogger(){
		return logger;
	}

}
