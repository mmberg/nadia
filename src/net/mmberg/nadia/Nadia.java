package net.mmberg.nadia;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
		//format logging
		logger.setUseParentHandlers(false);	 
		CustomFormatter fmt = new CustomFormatter();
		Handler ch = new ConsoleHandler();
		ch.setFormatter(fmt);
		logger.addHandler(ch);
		
		logger.setLevel(Level.INFO);
	}
	
	public static Logger getLogger(){
		return logger;
	}

	
	public class CustomFormatter extends Formatter {

		public String format(LogRecord record) {
			
			StringBuffer sb = new StringBuffer();
			sb.append(record.getLevel().getName());
			sb.append(" (");
			sb.append(record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.')+1));
			sb.append("): ");
			sb.append(formatMessage(record));
			sb.append("\n");

			return sb.toString();
		}
	}
	
}
