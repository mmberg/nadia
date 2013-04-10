package net.mmberg.nadia.processor.nlu.aqdparser;

public class OpenEndedParser extends Parser{

	public OpenEndedParser() {
		super("open_ended");
	}

	@Override
	public ParseResults parse(String utterance) {

		ParseResults results=new ParseResults(utterance);
//		results.add(new ParseResult(this.name,0,0,"dummy",this.type,"dummy"));
		return results;
	}

}
