package net.mmberg.nadia.processor.nlu.aqdparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser extends Parser{

	public DateParser() {
		super("fact.temporal.date");
	}

	protected void match_regex(ParseResults results, String regex, String className){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(results.getUtterance());
		
		 while(m.find()){
			 try {
				results.add(new ParseResult(this.name, m.start(), m.end(), m.group(), type, DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).parse(m.group())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		 }
		}
	
	private void checkForWeekdays(String utterance, ParseResults results){
		Calendar newDate = Calendar.getInstance();
		
		ArrayList<String> weekdays = new ArrayList<String>(Arrays.asList("sunday","monday","tuesday","wednesday","thursday","friday","saturday")); //sunday=1
		for(int i=0; i<=6; i++){
			String day=weekdays.get(i);
			if (utterance.contains(day)){
				int daysToGo = (7-Calendar.getInstance().get(Calendar.DAY_OF_WEEK))+i+1;
				newDate.add(Calendar.DAY_OF_MONTH, daysToGo);
				int pos=utterance.indexOf(day);
				results.add(new ParseResult(this.name, pos, pos+day.length(), day, type, DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(newDate.getTime())));
			}
		}
	}
	
	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);

		//check for dates
		this.match_regex(results,"\\d\\d/\\d\\d/\\d\\d\\d\\d", this.type);
		
		//check for words referencing dates
		if(results.isEmpty()){
			if (utterance.contains("today")){
				int pos=utterance.indexOf("today");
				results.add(new ParseResult(this.name, pos, pos+5, "today", type, DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(Calendar.getInstance().getTime())));
			}
			else checkForWeekdays(utterance.toLowerCase(),results);
		}
	
		return results;
	}
	
}
