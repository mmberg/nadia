package net.mmberg.nadia.processor.nlu.aqdparser;


public class OnOffParser extends Parser{

	public OnOffParser() {
		super("onoff");
	}

	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);
		
		super.match_regex(results,"on", this.type, "on");
		super.match_regex(results,"off", this.type, "off");
	
		return results;
	}

}
