package net.mmberg.nadia.processor.dialogmodel.actions;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.mmberg.nadia.dialogmodel.definition.actions.HTTPActionModel;
import net.mmberg.nadia.processor.dialogmodel.Frame;

@XmlRootElement
public class HTTPAction extends HTTPActionModel {

	public HTTPAction(){
		super();
	}
	
	public HTTPAction(String template){
		super(template);
	}
	
	@Override
	public HashMap<String, String> execute(Frame frame) {
		
		 	/*
		 	 * WikiAPI: http://www.mediawiki.org/wiki/Extension:MobileFrontend
		 	 * e.g.,
		 	 * url="http://en.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts&explaintext&exsentences=3&titles=Edinburgh";
		 	 * xpath="//extract";
		 	 */
		
			String replaced_url=replaceSlotMarkers(url, frame);
			String replaced_params=replaceSlotMarkers(params, frame);
			String[] params_arr = replaced_params.split("&");
			
			String result="not found";
	        try{
	        	
	        	HttpClient client = new HttpClient();
	        	client.start();
	        	 
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
	        	
	        	response = request.send();
	        	
	        	String xml = response.getContentAsString();
	        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder builder = factory.newDocumentBuilder();
	        	Document doc = builder.parse(new InputSource(new StringReader(xml)));
	        	 
		        XPathFactory xPathfactory = XPathFactory.newInstance();
		        XPath xPath = xPathfactory.newXPath();
		        XPathExpression expr = xPath.compile(xpath);
		        result = (String) expr.evaluate(doc, XPathConstants.STRING);
		        
		        //Postprocessing
		        result = result.replaceAll("\\s\\(.*?\\)", ""); //remove content in brackets
	        }
	        catch(Exception ex){
	        	ex.printStackTrace();
	        }
	        executionResults.put("result", result);
			return executionResults;
	}

}
