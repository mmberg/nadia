package net.mmberg.nadia.processor.nlu.aqdparser;


public class YNParser extends Parser{

	public YNParser() {
		super("DECISION");
	}

	@Override
	public ParseResults parse(String utterance) {
		
		ParseResults results = new ParseResults(utterance);

		
		super.match_regex(results,"yes", this.klass, "YES");
		super.match_regex(results,"no", this.klass, "NO");
	
		return results;
	}

}
