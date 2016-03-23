package net.mmberg.nadia.processor.dialogmodel.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import net.mmberg.nadia.processor.exceptions.ModelException;

@XmlRootElement
public class HttpTextAction extends HttpAction {

	public HttpTextAction() {
		super();
	}

	public HttpTextAction(String template) {
		super(template);
	}

	@Override
	/**
	 * Extracts a substring from a text response.
	 * The optional query needs to be a regular expression.
	 */
	protected String extractResults(String content)  throws ModelException{
		
		if(query==null) return content;
		
		String result = "";
		try {
			Pattern pattern = Pattern.compile(query);
			Matcher matcher = pattern.matcher(content);
			if(matcher.find()){
				result=content.substring(matcher.start(), matcher.end());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
