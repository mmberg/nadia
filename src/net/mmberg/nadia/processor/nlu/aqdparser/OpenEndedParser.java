package net.mmberg.nadia.processor.nlu.aqdparser;

public class OpenEndedParser extends Parser{

	public OpenEndedParser() {
		super("open_ended");
	}

	@Override
	public ParseResults parse(String utterance) {

		ParseResults results=new ParseResults(utterance);
		if(utterance.equals("bye")) results.add(new ParseResult(this.name,0,2,"bye",this.type,"bye"));
//		results.add(new ParseResult(this.name,0,0,"dummy",this.type,"dummy"));
		return results;
	}

}
