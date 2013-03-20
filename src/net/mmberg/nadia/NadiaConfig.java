package net.mmberg.nadia;

import java.util.Properties;

/**
 * Store configuration properties (like paths etc) here
 * @author Markus
 *
 */
public class NadiaConfig extends Properties{

	private static final long serialVersionUID = 1L;
	private static NadiaConfig instance;
	public static final String ONTOLOGYPATH="OntologyPath";
	public static final String CCGGRAMMARPATH="CCGgrammarPath";
	
	public NadiaConfig(){
		init();
	}
	
	public static NadiaConfig getInstance(){
		if(instance==null){
			instance=new NadiaConfig();
		}
		return instance;
	}
	
	public void init(){
		this.setProperty(ONTOLOGYPATH, "file:///"+System.getProperty("user.dir")+"/res/ontology/lexicon.owl");
		this.setProperty(CCGGRAMMARPATH, "file:///"+System.getProperty("user.dir")+"/res/ccggrammar/grammar.xml");
	}

}
