package net.mmberg.nadia.processor.dialogmodel.actions;

import javax.xml.bind.annotation.XmlRootElement;
import com.jayway.jsonpath.JsonPath;

@XmlRootElement
public class HttpJsonAction extends HttpAction {

	public HttpJsonAction() {
		super();
	}

	public HttpJsonAction(String template) {
		super(template);
	}

	@Override
	protected String extractResults(String content) {
		String result = "";
		try {
			result = JsonPath.parse(content).read("$." + query);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
