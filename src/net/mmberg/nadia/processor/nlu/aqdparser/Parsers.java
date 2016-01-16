package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.mmberg.nadia.processor.exceptions.NoParserFoundException;

public class Parsers {

	private static Set<Parser> active_parsers=new HashSet<Parser>();
	
	public static void init(){
		//enabled parsers
		if(active_parsers.isEmpty()){
			active_parsers.add(new YNParser());
			active_parsers.add(new CityParser());
			active_parsers.add(new DateParser());
			active_parsers.add(new OpenEndedParser());
			active_parsers.add(new NumberParser());
			active_parsers.add(new OnOffParser());
			//active_parsers.add(new ItemParser());
			active_parsers.add(new ItemParser2(new ArrayList<String>(Arrays.asList("weather","game"))));
			active_parsers.add(new ItemParser2(new ArrayList<String>(Arrays.asList("today","tomorrow"))));
			active_parsers.add(new ItemParser2(new ArrayList<String>(Arrays.asList("sport","business","economy","politics"))));
			active_parsers.add(new ItemParser2(new ArrayList<String>(Arrays.asList("add","subtract","multiply","divide"))));
		}
	}
	
	public static Set<Parser> getParserForType(String type){
		Set<Parser> matchingParsers=new HashSet<Parser>();
		
		for(Parser parser : active_parsers){
			if(parser.getType().equals(type)){
				matchingParsers.add(parser);
			}
		}
		
		return matchingParsers;
	}
	
	
	public static ParseResults parseExact(String utterance, String type) throws NoParserFoundException{
		Set<Parser> matchingParsers = getParserForType(type);	
		return parse(matchingParsers,utterance);
	}
	
	public static ParseResults parseWithAllParsers(String utterance) throws NoParserFoundException{
		return parse(active_parsers,utterance);
	}
	
	private static ParseResults parse(Set<Parser> parsers, String utterance) throws NoParserFoundException{
		
		ParseResults resultList = new ParseResults(utterance);
		if(parsers.size()>0){
			for(Parser p : parsers){
				ParseResults res=p.parse(utterance);
				resultList.addAll(res);
			}
			return resultList;
		}
		else throw new NoParserFoundException();
	}
	
}
