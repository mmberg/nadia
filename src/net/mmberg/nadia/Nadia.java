package net.mmberg.nadia;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.mmberg.nadia.processor.manager.DialogManager;
import net.mmberg.nadia.store.DialogStore;
import net.mmberg.nadia.ui.*;


public class Nadia implements UIConsumer {

	
	private final static Logger logger = Logger.getLogger("nina"); 
	private DialogManager manager=null;
	private UserInterface ui=null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Nadia nadia = new Nadia();
		
		HashMap<String,Class<? extends UserInterface>> interfaces=new HashMap<String, Class<? extends UserInterface>>();
		interfaces.put("console",ConsoleInterface.class);
		interfaces.put("rest",RESTInterface.class);
		interfaces.put("default",RESTInterface.class);
		
		try {
			if(args.length>0 && interfaces.containsKey(args[0])) nadia.ui=interfaces.get(args[0]).newInstance();
			else nadia.ui=interfaces.get("default").newInstance();
		} 
		catch (InstantiationException e){
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		nadia.start(nadia.ui);
	}
	
	public Nadia(){
		init();
		manager = new DialogManager();
		manager.loadDialog(DialogStore.getInstance().getDialog("dummy1"));
	}
	
	public void start(UserInterface ui){
		ui.register(this);
		ui.start();
	}
	
	@Override
	public UIConsumerMessage processUtterance(String userUtterance){
		return manager.processUtterance(userUtterance);
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
