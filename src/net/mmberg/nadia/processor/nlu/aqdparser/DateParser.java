package net.mmberg.nadia.processor.nlu.aqdparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser extends Parser{

	public DateParser() {
		super("DATE");
	}

	protected void match_regex(ParseResults results, String regex, String className){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(results.getUtterance());
		
		 while(m.find()){
			 try {
				results.add(new ParseResult(this.name, m.start(), m.end(), klass, DateFormat.getDateInstance().parse(m.group())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		 }
		}
	
	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);

		this.match_regex(results,"\\d\\d.\\d\\d.\\d\\d\\d\\d", this.klass);
	
		return results;
	}
	
}
