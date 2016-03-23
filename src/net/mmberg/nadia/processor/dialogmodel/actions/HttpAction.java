package net.mmberg.nadia.processor.dialogmodel.actions;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import net.mmberg.nadia.dialogmodel.definition.actions.HttpActionModel;
import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.dialogmodel.Frame;
import net.mmberg.nadia.processor.exceptions.ModelException;

@XmlRootElement
public abstract class HttpAction extends HttpActionModel {

	private HttpClient client;
	private final static Logger logger = NadiaProcessor.getLogger();
	
	public HttpAction(){
		super();
		init();
	}
	
	public HttpAction(String template){
		super(template);
		init();
	}
	
	private void init(){
		SslContextFactory sslContextFactory = new SslContextFactory();
    	client = new HttpClient(sslContextFactory);
    	try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract String extractResults(String content) throws ModelException;
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
		
		 	/*
		 	 * WikiAPI: http://www.mediawiki.org/wiki/Extension:MobileFrontend
		 	 * e.g.,
		 	 * url="http://en.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts&explaintext&exsentences=3&titles=Edinburgh";
		 	 * xpath="//extract";
		 	 * 
		 	 * or
		 	 * 
		 	 * curl --data "state=on" http://mmt.et.hs-wismar.de:8080/Lightbulb/Lightbulb
		 	 * 
		 	 */
		
			String replaced_url=replaceSlotMarkers(url, frame);
			String replaced_params=replaceSlotMarkers(params, frame);
			String[] params_arr = replaced_params.split("&");
			
			String result="Sorry, that did not work. ";
	        try{
	        		        	 
	        	ContentResponse response;
	        	Request request;
        		
	        	request = client.newRequest(replaced_url);

	        	//choose method
	        	if(method.toLowerCase().equals("get")){
	        		request.method(HttpMethod.GET);
	        	}
	        	else{
	        		request.method(HttpMethod.POST);
	        	}
	        	
	        	//process parameters
	        	String[] key_value;
	        	for(String paramPair : params_arr){
	        		key_value=paramPair.split("=");
	        		if(key_value.length>1) request.param(key_value[0],key_value[1]);
	        		else request.param(key_value[0], "");
	        	}       	
	        	
	        	logger.info("requesting: "+request.getURI()+", "+request.getParams().toString());
	        	response = request.send();
	        	logger.info("HTTP status: "+response.getStatus());
	        	
	        	String content = response.getContentAsString();
	        	logger.info("Response: " + content);
	        	result=extractResults(content);
	        	logger.info("Extracting '" + query + "': " + result);
	        }
	        catch(Exception ex){
	        	ex.printStackTrace();
	        }
	        executionResults.put("result", result);
			return executionResults;
	}
	
	/**
	 * postprocessing (can be useful for Wiki pages)
	 */
	protected String postProcess(String content){
		
		content = content.replaceAll("\\s\\(.*?\\)", ""); // remove content in brackets
		content = content.replaceAll("\\s\\[.*?\\]", "");
		
		return content;
	}

}
