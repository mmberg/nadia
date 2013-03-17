package net.mmberg.nadia.processor.nlu.soda.features;

import java.util.Arrays;
import java.util.HashSet;

import net.mmberg.nadia.processor.manage.DialogManagerContext;

public class InterrogativeFeature extends Feature{
	
	HashSet<String> condWords=new HashSet<String>(Arrays.asList("could", "should"));
	
	public InterrogativeFeature() {
		super("interrogative"); //in this case we mean questions
	}

	@Override
	protected boolean hasFeature(String utterance, DialogManagerContext context) {
		
		//if second word is you or is or I
		//can YOU tell me ...?
		//what/how IS ...? 
		String[] words=utterance.split(" ");
		if(words.length>1){
			return (
					(words[1].equals("you") || words[1].equals("is") || words[1].equals("i")) || 
					(words[0].equals("do") || words[0].equals("can"))
					);
		}
		else return false;
	}

}
