package net.mmberg.nadia.store;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.mmberg.nadia.NadiaConfig;
import net.mmberg.nadia.dialogmodel.Action;
import net.mmberg.nadia.dialogmodel.Dialog;
import net.mmberg.nadia.dialogmodel.ITO;
import net.mmberg.nadia.dialogmodel.Task;
import net.mmberg.nadia.dialogmodel.aqd.AQD;
import net.mmberg.nadia.dialogmodel.aqd.AQDContext;
import net.mmberg.nadia.dialogmodel.aqd.AQDForm;
import net.mmberg.nadia.dialogmodel.aqd.AQDType;
import net.mmberg.nadia.processor.nlu.actions.DummyAction;
import net.mmberg.nadia.processor.nlu.taskselector.BagOfWordsTaskSelector;

public class DialogStore {

	private static DialogStore dialogstore= null;
	private HashMap<String, Dialog> store = new HashMap<String, Dialog>();
	private static NadiaConfig config = NadiaConfig.getInstance();
	
	private DialogStore(){
		store.put("dummy1", createDummyDialog());
		store.put("dummy2", createDummyDialog2());
	}
	
	public static DialogStore getInstance(){
		if(dialogstore==null){
			dialogstore=new DialogStore();
		}
		
		return dialogstore;
	}
	
	public static void main(String[] args){
		String test_dialog_name="dummy2";
		//test save
		DialogStore ds=DialogStore.getInstance();
		DialogStore.save(ds.getDialogFromStore(test_dialog_name));
		//test load
		DialogStore.loadFromResourcesDir(test_dialog_name);
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
	
private Dialog createDummyDialog2(){
		
		Dialog dialog = new Dialog("dummy2");
		ITO ito;
		AQD aqd;
		Action action;
		
		//Task 0
		//----------------------------------------------
		Task task0=new Task("start");
		ArrayList<String> bagOfWords = new ArrayList<String>(Arrays.asList("hello"));
		task0.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		dialog.addTask(task0);
	
		//ITO 1
		ito=new ITO("welcome", "Hello! How may I help you?", false);	
		task0.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("open_ended"));
		ito.setAQD(aqd);
		
		
		//Task 1
		//----------------------------------------------
		Task task1=new Task("getTripInformation");
		bagOfWords = new ArrayList<String>(Arrays.asList("travel","book", "journey","trip"));
		task1.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		action=new DummyAction("This trip from %getDepartureCity to %getDestinationCity costs #temperature Euros.");
		task1.setAction(action);
		dialog.addTask(task1);
		
		//ITO 1
		ito=new ITO("getDepartureCity", "Where do you want to start?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);		
		
		//ITO 2
		ito=new ITO("getDestinationCity", "Where do you want to go?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);	
		
		
		//Task2
		//----------------------------------------------
		Task task2=new Task("getWeatherInformation");
		bagOfWords = new ArrayList<String>(Arrays.asList("weather","forecast", "temperature","trip"));
		task2.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		action=new DummyAction("The temperature in %getWeatherCity is #temperature degrees.");
		task2.setAction(action);
		dialog.addTask(task2);
		
		//ITO 1
		ito=new ITO("getWeatherCity", "For which city do you want to know the weather?", false);	
		task2.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);		
		
		return dialog;			
	}	

	
	//load/save dialogues

	public static void save(Dialog d){
		String filename = d.getName()!=null?d.getName()+".xml":"dialogue.xml";
		saveAs(d, filename);
	}
	
	private static void save(Dialog d, OutputStream stream){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dialog.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(d, stream);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAs(Dialog d, String filename){
		try {
			save(d, new FileOutputStream(config.getProperty(NadiaConfig.DIALOGUEDIR)+filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String toXML(Dialog d){
		OutputStream stream = new ByteArrayOutputStream();
		save(d, stream);
		return stream.toString();
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

	public static Dialog loadFromXml(String xml){
		JAXBContext context;
		Dialog d=null;
		try {
			context = JAXBContext.newInstance(Dialog.class);
			Unmarshaller um = context.createUnmarshaller();
			d = (Dialog) um.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return d;
	}
}
