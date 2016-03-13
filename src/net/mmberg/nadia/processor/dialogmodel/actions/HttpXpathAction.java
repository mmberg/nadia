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

@XmlRootElement
public class HttpXpathAction extends HttpAction {

	public HttpXpathAction() {
		super();
	}

	public HttpXpathAction(String template) {
		super(template);
	}

	@Override
	protected String extractResults(String content) {
		String result="";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(content)));

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xPath = xPathfactory.newXPath();
			XPathExpression expr = xPath.compile(query);
			result = (String) expr.evaluate(doc, XPathConstants.STRING);

			// Postprocessing
			result = result.replaceAll("\\s\\(.*?\\)", ""); // remove content in brackets
			result = result.replaceAll("\\s\\[.*?\\]", "");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
