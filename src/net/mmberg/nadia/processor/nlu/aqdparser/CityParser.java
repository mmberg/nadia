package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;
import java.util.Arrays;

public class CityParser extends Parser{

	public CityParser() {
		super("CITY");
	}

	@Override
	public ParseResults parse(String utterance) {

		ParseResults results=new ParseResults(utterance);
		ArrayList<String> gazetteer = new ArrayList<String>(Arrays.asList("Aberdeen","Edinburgh", "Glasgow","Inverness", "Portree", "Uig", "Malaig", "Balloch"));

		ArrayList<String> tokens=new ArrayList<String>(Arrays.asList(utterance.split(" ")));//tokenize
		tokens.retainAll(gazetteer);
				
		if(tokens.size()>0){
			for(String city : tokens){
				results.add(new ParseResult(this.name,0,0,this.klass,city));
			}
		}

		return results;
	}

}
