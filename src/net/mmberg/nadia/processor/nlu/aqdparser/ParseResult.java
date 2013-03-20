package net.mmberg.nadia.processor.nlu.aqdparser;

public class ParseResult {

	private Object result_object=null;
	private int begin_match=0;
	private int end_match=0;
	private String matched_sequence="";
	private String parser_name="";
	private String result_classname="";
	
	public ParseResult(String parser_name, int begin_match, int end_match, String matched_sequence, String class_name, Object class_object){
		this.result_object=class_object;
		this.result_classname=class_name;
		this.begin_match=begin_match;
		this.end_match=end_match;
		this.parser_name=parser_name;
		this.matched_sequence=matched_sequence;
	}
	
	
	public String getMatchedSequence(){
		return this.matched_sequence;
	}
	
	public String getParserName(){
		return this.parser_name;
	}
	
	public Object getResultObject(){
		return result_object;
	}
	
	public String getResultString(){
		return result_object.toString();
	}
	
	@Override
	public String toString(){
		return parser_name+" matched sequence "+begin_match+"-"+end_match+" ("+ matched_sequence +") as "+result_classname+":"+result_object.toString();
	}
}
