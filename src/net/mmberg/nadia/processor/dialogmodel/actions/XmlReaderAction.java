package net.mmberg.nadia.processor.dialogmodel.actions;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import net.mmberg.nadia.dialogmodel.definition.actions.XmlReaderActionModel;
import net.mmberg.nadia.processor.dialogmodel.Frame;

@XmlRootElement
public class XmlReaderAction extends XmlReaderActionModel {

	public XmlReaderAction(){
		super();
	}
	
	public XmlReaderAction(String template){
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
		
			String dyn_url=replaceSlotMarkers(url, frame);
		
			String result="not found";
	        try{
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder builder = factory.newDocumentBuilder();
		        Document doc = builder.parse(dyn_url);
		        XPathFactory xPathfactory = XPathFactory.newInstance();
		        XPath xPath = xPathfactory.newXPath();
		        XPathExpression expr = xPath.compile(xpath);
		        result = (String) expr.evaluate(doc, XPathConstants.STRING);
		        result = result.replaceAll("\\s\\(.*?\\)", ""); //remove content in brackets
	        }
	        catch(Exception ex){
	        	ex.printStackTrace();
	        }
	        executionResults.put("result", result);
			return executionResults;
	}

}
