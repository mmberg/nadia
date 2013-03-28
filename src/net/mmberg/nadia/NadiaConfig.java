package net.mmberg.nadia;

import java.util.Properties;

/**
 * Store configuration properties (like paths etc) here
 * @author Markus
 *
 */
public class NadiaConfig extends Properties{

	private static final long serialVersionUID = 1L;
	private static NadiaConfig instance=null;
	public static final String ONTOLOGYPATH="OntologyPath";
	public static final String CCGGRAMMARPATH="CCGgrammarPath";
	public static final String DIALOGUEDIR="DialogueDir";
	public static final String JETTYKEYSTOREPATH="KeyStorePath";
	public static final String JETTYKEYSTOREPASS="KeyStorePass";
	public static final String JETTYWEBXMLPATH="WebXml";
	public static final String JETTYRESOURCEBASE="ResourceBase";
	public static final String JETTYCONTEXTPATH="ContextPath";
	
	private NadiaConfig(){

	}
	
	public static NadiaConfig getInstance(){
		if(instance==null){
			instance=new NadiaConfig();
			instance.init();
		}
		return instance;
	}
	
	private void init(){
		this.setProperty(ONTOLOGYPATH, "file:///"+System.getProperty("user.dir")+"/res/ontology/lexicon.owl");
		this.setProperty(CCGGRAMMARPATH, "file:///"+System.getProperty("user.dir")+"/res/ccggrammar/grammar.xml");
		this.setProperty(DIALOGUEDIR, "res/dialogues/");
		//Jetty SSL
		this.setProperty(JETTYKEYSTOREPATH, "res/keys/nadia.jks");
		this.setProperty(JETTYKEYSTOREPASS, "naturaldialog");
		//Jetty
		this.setProperty(JETTYWEBXMLPATH, "./WEB-INF/web.xml");
		this.setProperty(JETTYRESOURCEBASE, "./res/html");
		this.setProperty(JETTYCONTEXTPATH, "/nadia");
	}

}
