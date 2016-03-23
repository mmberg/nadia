package net.mmberg.nadia.processor.dialogmodel.actions;

import javax.xml.bind.annotation.XmlRootElement;
import com.jayway.jsonpath.JsonPath;

import net.mmberg.nadia.processor.exceptions.ModelException;

@XmlRootElement
public class HttpJsonAction extends HttpAction {

	public HttpJsonAction() {
		super();
	}

	public HttpJsonAction(String template) {
		super(template);
	}

	@Override
	/**
	 * Extracts a value from a JSON response.
	 * The query needs to be a Javascript object formulated as a JsonPath as described at https://github.com/jayway/JsonPath.
	 */
	protected String extractResults(String content) throws ModelException{
		
		if(query==null) throw new ModelException("No query provided.");
		
		String result = "";
		try {
			result = JsonPath.parse(content).read("$." + query);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
