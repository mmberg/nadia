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
		utterance=utterance.replace('?', ' ');
		utterance=utterance.replace('!', ' ');
		utterance=utterance.replace(',', ' ');
		
		String decap_utterance=utterance.toLowerCase();
		ArrayList<String> gazetteer = new ArrayList<String>(Arrays.asList("aberdeen","edinburgh", "glasgow","inverness", "portree", "uig", "malaig", "balloch"));

		ArrayList<String> tokens=new ArrayList<String>(Arrays.asList(decap_utterance.split(" ")));//tokenize
		tokens.retainAll(gazetteer);
		
		String value="";
		if(tokens.size()>0){
			for(String city : tokens){
				String capitalizedCity = Character.toString(city.charAt(0)).toUpperCase()+city.substring(1);
				if(utterance.contains(city)){
					value = city;
				}
				else{ //if capitalized
					value = capitalizedCity;
				}
				int index = utterance.indexOf(value);
				results.add(new ParseResult(this.name,index,index+value.length()-1,value,this.type,capitalizedCity));
			}
		}

		return results;
	}

}
