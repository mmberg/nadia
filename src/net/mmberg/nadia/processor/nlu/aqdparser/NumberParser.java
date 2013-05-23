package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberParser extends Parser{

	public NumberParser() {
		super("fact.quantity");
	}

	protected void match_regex(ParseResults results, String regex, String className){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(results.getUtterance());
		
		 while(m.find()){
			results.add(new ParseResult(this.name, m.start(), m.end(), m.group(), type, m.group()));
		 }
		}
	
	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);

		this.match_regex(results,"\\d+", this.type);
	
		return results;
	}
	
}
