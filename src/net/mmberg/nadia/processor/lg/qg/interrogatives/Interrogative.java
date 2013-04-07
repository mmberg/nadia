package net.mmberg.nadia.processor.lg.qg.interrogatives;

import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

public abstract class Interrogative {
		
	protected static Integer w_counter=-1;
	
	public Interrogative(){

	}
	

	public abstract Element createLF(String wh_word, String verb, String noun, boolean opener, boolean sayPlease, boolean subj);
	
	protected class Flags{
		public static final String DEF="def";
		public static final String INDEF="indef";
		public static final String PERSONAL="pers";
	}
	
	protected String getNextW(){
		return "w"+(++w_counter);
	}
	
	protected void addPronoun(Element parent_node, String rel_name, String num, String pers){
		Element node=new Element("node");
		node.setAttribute(new Attribute("id", getNextW()));
		node.setAttribute(new Attribute("pred", "pron"));
		node.setAttribute(new Attribute("num", num));
		node.setAttribute(new Attribute("pers", pers));
		addRel(parent_node,rel_name,node);
	}
	
	protected void addDeterminer(Element parent_node, String type){
		Element detNode=createNode("det");
		detNode.setAttribute("type",type);
		addRel(parent_node,"mod",detNode);
	}
	
	protected Element createNode(String pred){
		Element node=new Element("node");
		node.setAttribute(new Attribute("id", getNextW()));
		node.setAttribute(new Attribute("pred", pred));
		return node;
	}
	
	protected Element createNode(String pred, Map<String, String> attributes){
		Element node=createNode(pred);
		for(Map.Entry<String,String> entry:attributes.entrySet()){
			node.setAttribute(new Attribute(entry.getKey(), entry.getValue()));
		}
		return node;
	}
	
	protected void addRel(Element parent_node, String name, Element child_node){
		Element rel=new Element("rel");
		rel.setAttribute(new Attribute("name", name));
		rel.addContent(child_node);
		parent_node.addContent(rel);
	}

}
