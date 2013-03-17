package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class Parser {
	
	protected String name;
	protected String klass;

	public Parser(String klass){
		this.name=this.getClass().getName();
		this.klass=klass;
	}

	protected void match_regex(ParseResults results, String regex, String className, String classValue){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(results.getUtterance());
		
		 while(m.find()){
			 results.add(new ParseResult(this.name, m.start(), m.end(), klass, classValue));
		 }
		}
	
	public abstract ParseResults parse(String utterance);
}
