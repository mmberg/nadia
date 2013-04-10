package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;
import java.util.Arrays;

public class CityParser extends Parser{

	public CityParser() {
		super("fact.named_entity.non_animated.location.city");
	}

	@Override
	public ParseResults parse(String utterance) {

		ParseResults results=new ParseResults(utterance);
		String decap_utterance=utterance.toLowerCase();
		ArrayList<String> gazetteer = new ArrayList<String>(Arrays.asList("aberdeen","edinburgh", "glasgow","inverness", "portree", "uig", "malaig", "balloch"));

		ArrayList<String> tokens=new ArrayList<String>(Arrays.asList(decap_utterance.split(" ")));//tokenize
		tokens.retainAll(gazetteer);
		
		String capitalizedCity="";
		if(tokens.size()>0){
			for(String city : tokens){
				capitalizedCity = Character.toString(city.charAt(0)).toUpperCase()+city.substring(1);
				results.add(new ParseResult(this.name,0,0,capitalizedCity,this.type,capitalizedCity));
			}
		}

		return results;
	}

}
