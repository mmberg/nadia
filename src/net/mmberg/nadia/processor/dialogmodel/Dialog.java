package net.mmberg.nadia.processor.dialogmodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.annotation.*;

import net.mmberg.nadia.dialogmodel.definition.DialogModel;
import net.mmberg.nadia.processor.NadiaProcessorConfig;

@XmlRootElement
public class Dialog extends DialogModel{

	private static NadiaProcessorConfig config = NadiaProcessorConfig.getInstance();

	
	public Dialog(){
		super();
	}	
	
	public Dialog(String name){
		super(name);
	}
	
	
	//Serialization / Deserialization
	
	public static Dialog loadFromResourcesDir(String filename){
		return loadFromPath(config.getProperty(NadiaProcessorConfig.DIALOGUEDIR)+"/"+filename+".xml");
	}

	
	public void save(){
		String filename = getName()!=null?getName()+".xml":"dialogue.xml";
		saveAs(config.getProperty(NadiaProcessorConfig.DIALOGUEDIR), filename);
	}
	
	public void saveUnder(String path){
		try {
			String filename = getName()!=null?getName()+".xml":"dialogue.xml";
			save(new FileOutputStream(path+"/"+filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
