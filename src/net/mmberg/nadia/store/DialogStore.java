package net.mmberg.nadia.store;

import java.util.HashMap;

import net.mmberg.nadia.dialogmodel.Dialog;
import net.mmberg.nadia.dialogmodel.ITO;
import net.mmberg.nadia.dialogmodel.Task;
import net.mmberg.nadia.dialogmodel.aqd.AQD;
import net.mmberg.nadia.dialogmodel.aqd.AQDContext;
import net.mmberg.nadia.dialogmodel.aqd.AQDForm;
import net.mmberg.nadia.dialogmodel.aqd.AQDType;

public class DialogStore {

	private static DialogStore dialogstore= null;
	private HashMap<String, Dialog> store = new HashMap<String, Dialog>();
	
	private DialogStore(){
		store.put("dummy1", createDummyDialog());
	}
	
	public static DialogStore getInstance(){
		if(dialogstore==null){
			dialogstore=new DialogStore();
		}
		
		return dialogstore;
	}
	
	public Dialog getDialog(String key){
		return store.get(key);
	}
	
	private Dialog createDummyDialog(){
		
		Dialog dialog = new Dialog();
				
		//a dialog consists of tasks
		Task task1=new Task("bsp");
		dialog.addTask(task1);
		
		//a task consists of ITOs
		ITO ito;
		AQD aqd;
		
		//1
		ito=new ITO("getDrinkDecision", "Would you like a drink?");	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setAQDType(new AQDType("decision"));
		ito.setAQD(aqd);	
		
		//2
		ito=new ITO("getCity", "Where do you want to go?");	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setAQDType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);	
				
		//3
		ito=new ITO("getDate", "When do you want to leave?");	
		task1.addITO(ito);
		//aqd=new AQD();
		//aqd.setAQDType(new AQDType("fact.temporal.date"));
		aqd=new AQD(new AQDType("fact.temporal.date"), new AQDContext("begin","trip"), new AQDForm()); //TODO politeness and formality set on dialogue level
		ito.setAQD(aqd);	
		
		return dialog;
				
	}

}
