package net.mmberg.nadia.dialogmodel.actions;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import net.mmberg.nadia.dialogmodel.Action;
import net.mmberg.nadia.dialogmodel.Frame;

@XmlRootElement
public class JavaAction extends Action{
	
	private String path;
	private String className;
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public JavaAction(){
		super();
	}
	
	public JavaAction(String template){
		super(template);
	}
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
		Action action=load();
		return action.execute(frame);		
	}
	
	private Action load(){
		Action extAct = null;
		URLClassLoader cl;
		try {
			URL url = new URL("file",path,"");
			cl = new URLClassLoader(new URL[] {url});
			Class<?> loadedClass = cl.loadClass(className);
			extAct = (Action) loadedClass.newInstance();
			cl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return extAct;
	}


	
	

}
