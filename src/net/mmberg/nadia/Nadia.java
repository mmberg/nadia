package net.mmberg.nadia;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.mmberg.nadia.dialogmodel.Dialog;
import net.mmberg.nadia.processor.manager.DialogManager;
import net.mmberg.nadia.store.DialogStore;
import net.mmberg.nadia.ui.*;


public class Nadia implements UIConsumer {

	
	private final static Logger logger = Logger.getLogger("nina"); 
	private DialogManager manager=null;
	private static boolean init=false;
	private static String default_dialog=DialogStore.toResourcesDirPath("dummy1"); //default dialogue
	
	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
			
		Class<? extends UserInterface> ui_class=ConsoleInterface.class; //default UI
		String dialog_file=default_dialog; //default dialogue
		
		//process command line args
		Options cli_options = new Options();
		cli_options.addOption("h", "help", false, "print this message");
		cli_options.addOption(OptionBuilder.withLongOpt( "interface" )
                .withDescription( "select user interface" )
                .hasArg(true)
                .withArgName("console, rest")
                .create("i"));
		cli_options.addOption("f", "file", true, "specify dialogue path and file, e.g. -f /res/dialogue1.xml");
		cli_options.addOption("r", "resource", true, "load dialogue (by name) from resources, e.g. -r dialogue1");
		cli_options.addOption("s", "store", true, "load dialogue (by name) from internal store, e.g. -s dialogue1");
		
		CommandLineParser parser = new org.apache.commons.cli.BasicParser();
		try {
			CommandLine cmd = parser.parse(cli_options, args);
			
			//Help
			if(cmd.hasOption("h")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("nadia", cli_options, true);
				return;
			}
			
			//UI
			if(cmd.hasOption("i")){
				String interf=cmd.getOptionValue("i");
				if(interf.equals("console")) ui_class=ConsoleInterface.class;
				else if (interf.equals("rest")) ui_class=RESTInterface.class;
			}
			
			//load dialogue from path file
			if(cmd.hasOption("f")){
				dialog_file=cmd.getOptionValue("f");
			}
			//load dialogue from resources
			if(cmd.hasOption("r")){
				dialog_file=DialogStore.toResourcesDirPath(cmd.getOptionValue("r"));
			}
			//load dialogue from internal store
			if(cmd.hasOption("s")){
				Dialog store_dialog=DialogStore.getInstance().getDialogFromStore((cmd.getOptionValue("s")));
				DialogStore.save(store_dialog);
				dialog_file=DialogStore.toResourcesDirPath(cmd.getOptionValue("s"));
			}
		
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
			
		//start Nadia with selected UI
		default_dialog=dialog_file;
		Nadia nadia = new Nadia(DialogStore.loadFromPath(dialog_file));
		UserInterface ui;
		try {
			ui = ui_class.newInstance();
			nadia.startUI(ui);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Nadia(){
		if (!init) init();
		manager = new DialogManager();
		manager.loadDialog(DialogStore.loadFromPath(default_dialog)); //load default dialogue
	}
	
	public Nadia(Dialog d){
		manager = new DialogManager();
		manager.loadDialog(d);	
	}
	
	private void startUI(UserInterface ui){
		ui.register(this);
		ui.start();
	}
	
	@Override
	public void loadDialog(Dialog d){
		manager.loadDialog(d);
	}
	
	@Override
	public UIConsumerMessage processUtterance(String userUtterance){
		return manager.processUtterance(userUtterance);
	}
		
	@Override
	public String getDebugInfo() {
		String context="no debug info";
		if(manager!=null){
			context = manager.getContext().serialize();
		}
		context += "\r\n\r\n"+DialogStore.toXML(manager.getDialog());
		return context;
	}
	
	private void init(){
		init=true;
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
