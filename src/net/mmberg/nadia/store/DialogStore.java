package net.mmberg.nadia.store;

import java.io.FileOutputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.mmberg.nadia.NadiaConfig;
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
	private static NadiaConfig config = NadiaConfig.getInstance();
	
	private DialogStore(){
		store.put("dummy1", createDummyDialog());
	}
	
	public static DialogStore getInstance(){
		if(dialogstore==null){
			dialogstore=new DialogStore();
		}
		
		return dialogstore;
	}
	
	public Dialog getDialogFromStore(String key){
		return store.get(key);
	}
	
	private Dialog createDummyDialog(){
		
		Dialog dialog = new Dialog("dummy1");
				
		//a dialog consists of tasks
		Task task1=new Task("bsp");
		dialog.addTask(task1);
		
		//a task consists of ITOs
		ITO ito;
		AQD aqd;
		
		//1
		ito=new ITO("getDrinkDecision", "Would you like a drink?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("decision"));
		ito.setAQD(aqd);	
		
		//2
		ito=new ITO("getCity", "Where do you want to go?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);	
				
		//3
		ito=new ITO("getDate", "When do you want to leave?");	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.temporal.date"), new AQDContext("begin","trip"), new AQDForm()); //TODO politeness and formality set on dialogue level
		ito.setAQD(aqd);	
		
		return dialog;			
	}	
	
	//load/save dialogues

	public void save(Dialog d){
		String filename = d.getName()!=null?d.getName()+".xml":"dialogue.xml";
		saveAs(d, filename);
	}
	
	public void saveAs(Dialog d, String filename){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dialog.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		    //m.marshal(d, System.out);
		    m.marshal(d, new FileOutputStream(config.getProperty(NadiaConfig.DIALOGUEDIR)+filename));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Dialog loadFromPath(String path){
		JAXBContext context;
		Dialog d=null;
		try {
			context = JAXBContext.newInstance(Dialog.class);
			Unmarshaller um = context.createUnmarshaller();
			d = (Dialog) um.unmarshal(new java.io.FileInputStream(path));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return d;
	}
	
	public static Dialog loadFromResourcesDir(String filename){
		return loadFromPath(toResourcesDirPath(filename));
	}
	
	public static String toResourcesDirPath(String filename){
		return config.getProperty(NadiaConfig.DIALOGUEDIR)+filename+".xml";
	}

}
