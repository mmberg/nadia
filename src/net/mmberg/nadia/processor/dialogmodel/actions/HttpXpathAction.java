package net.mmberg.nadia.processor.dialogmodel.actions;

import java.io.StringReader;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.mmberg.nadia.processor.exceptions.ModelException;

@XmlRootElement
public class HttpXpathAction extends HttpAction {

	public HttpXpathAction() {
		super();
	}

	public HttpXpathAction(String template) {
		super(template);
	}

	@Override
	/**
	 * Extracts a value from an XML response.
	 * The query needs to be a XPath.
	 */
	protected String extractResults(String content)  throws ModelException{
		
		if(query==null) throw new ModelException("No query provided.");
		
		String result="";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(content)));

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xPath = xPathfactory.newXPath();
			XPathExpression expr = xPath.compile(query);
			result = (String) expr.evaluate(doc, XPathConstants.STRING);

			result = postProcess(result); //remove brackets etc
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
