package net.mmberg.nadia.processor.nlu.aqdparser;

import java.util.ArrayList;


public class ItemParser2 extends Parser{

	private ArrayList<String> items;
	
	public ItemParser2(ArrayList<String> items) {
		super("item");
		this.items=items;
		String itemstring="";
		for(String item : items) itemstring+=item+",";
		itemstring = itemstring.substring(0,itemstring.length()-1);
		this.setType("item("+itemstring+")");
	}

	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);
		
		for(String item : items){
			super.match_regex(results,item, this.type, item.toUpperCase());
		}
	
		return results;
	}

}
