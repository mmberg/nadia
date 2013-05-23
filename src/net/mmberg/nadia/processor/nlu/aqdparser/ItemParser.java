package net.mmberg.nadia.processor.nlu.aqdparser;


public class ItemParser extends Parser{

	public ItemParser() {
		super("item(game,weather)");
	}

	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);
		
		super.match_regex(results,"game", this.type, "GAME");
		super.match_regex(results,"wetter", this.type, "WEATHER");
	
		return results;
	}

}
