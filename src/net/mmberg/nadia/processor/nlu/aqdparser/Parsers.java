package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.HashMap;

public class Parsers {

	private static HashMap<String, Parser> parsers=new HashMap<String, Parser>();  
	
	public static void init(){
		parsers.put("decision.yn", new YNParser());
		parsers.put("fact.location.city", new CityParser());
		parsers.put("fact.temporal.date", new DateParser());
	}
	
	
	public static ParseResults parse(String utterance, String type) throws Exception{
		if(parsers.containsKey(type)){
			return parsers.get(type).parse(utterance);
		}
		else throw new Exception("No parser found");
	}
	
	public static ParseResults parseAll(String utterance){
		ParseResults resultList = new ParseResults(utterance);
		for (Parser p : parsers.values()){
		 ParseResults res = p.parse(utterance);
		 resultList.addAll(res);
		}
		
		return resultList;
	}
	
}
