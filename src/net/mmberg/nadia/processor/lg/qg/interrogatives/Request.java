package net.mmberg.nadia.processor.lg.qg.interrogatives;

import org.jdom.Attribute;
import org.jdom.Element;

public abstract class Request extends Interrogative{

	protected Element createRequest(boolean temp_opener, boolean politeness){
		Element node=createNode("tell");
		if (temp_opener) node.setAttribute(new Attribute("modifier", "temporal-opener"));
		if (politeness) node.setAttribute(new Attribute("politeness", "yes"));

		addPronoun(node, "patient", "sg", "1st");
		return node;
	}
	
}
