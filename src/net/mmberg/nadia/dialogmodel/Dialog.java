package net.mmberg.nadia.dialogmodel;

import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class Dialog {

	//serializable members
	private String name;
	private ArrayList<Task> tasks;
	
	
	//Serialization getter/setter
	public Dialog(){
		tasks=new ArrayList<Task>();
	}
		
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name="tasks")
	@XmlElement(name="task")
	public ArrayList<Task> getTasks(){
		return tasks;
	}
	
	public void setTasks(ArrayList<Task> tasks){
		this.tasks=tasks;
	}
	
	
	//Content
	public Dialog(String name){
		this();
		this.name=name;
	}
	
	public void addTask(Task task){
		this.tasks.add(task);
	}
	
	public Task getTask(String name){
		for(Task t : tasks){
			if(t.getName().equals(name)) return t;
		}
		return null;
	}
	
	
	public void save(){
		String filename = this.getName()!=null?this.getName()+".xml":"result.xml";
		saveAs(filename);
	}
	
	public void saveAs(String filename){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dialog.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		    //m.marshal(this, System.out);
		    m.marshal(this, new FileOutputStream(filename));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Dialog loadFromFile(String filename){
		JAXBContext context;
		Dialog d=null;
		try {
			context = JAXBContext.newInstance(Dialog.class);
			Unmarshaller um = context.createUnmarshaller();
			d = (Dialog) um.unmarshal(new java.io.FileInputStream(filename+".xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return d;
	}
}
