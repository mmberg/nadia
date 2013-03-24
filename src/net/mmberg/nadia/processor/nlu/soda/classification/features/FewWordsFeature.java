package net.mmberg.nadia.processor.nlu.soda.classification.features;

public class FewWordsFeature extends Feature {

	public FewWordsFeature(){
		super("FewWords");
	}

	@Override
	protected boolean hasFeature(String utterance){
		return (utterance.split(" ").length<=2);
	}

	
}
