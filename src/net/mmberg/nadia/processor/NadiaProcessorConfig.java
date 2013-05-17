package net.mmberg.nadia.processor;

import java.util.Properties;

/**
 * Store configuration properties (like paths etc) here
 * @author Markus
 *
 */
public class NadiaProcessorConfig extends Properties{

	private static final long serialVersionUID = 1L;
	private static NadiaProcessorConfig instance=null;
	public static final String ONTOLOGYPATH="OntologyPath";
	public static final String CCGGRAMMARPATH="CCGgrammarPath";
	public static final String DIALOGUEDIR="DialogueDir";
	public static final String JETTYKEYSTOREPATH="KeyStorePath";
	public static final String JETTYKEYSTOREPASS="KeyStorePass";
	public static final String JETTYWEBXMLPATH="WebXml";
	public static final String JETTYRESOURCEBASE="ResourceBase";
	public static final String JETTYCONTEXTPATH="ContextPath";
	
	private String basedir = "file:///"+System.getProperty("user.dir");
	
	private NadiaProcessorConfig(){

	}
	
	public static NadiaProcessorConfig getInstance(){
		if(instance==null){
			instance=new NadiaProcessorConfig();
			instance.init();
		}
		return instance;
	}
	
	public void setBaseDir(String path){
		basedir=path;
		init();
	}
	
	private void init(){
		this.setProperty(ONTOLOGYPATH, basedir+"/res/ontology/lexicon.owl");
		this.setProperty(CCGGRAMMARPATH, basedir+"/res/ccggrammar/grammar.xml");
		this.setProperty(DIALOGUEDIR, basedir+"/res/dialogues");
		//Jetty SSL
		this.setProperty(JETTYKEYSTOREPATH, "res/keys/nadia.jks");
		this.setProperty(JETTYKEYSTOREPASS, "naturaldialog");
		//Jetty
		this.setProperty(JETTYWEBXMLPATH, "./WEB-INF/web.xml");
		this.setProperty(JETTYRESOURCEBASE, "./res/html");
		this.setProperty(JETTYCONTEXTPATH, "/nadia");
	}

}
