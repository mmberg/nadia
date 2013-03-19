package net.mmberg.nadia.utterance;

public class TrainingUtterance extends UserUtterance {

	private String expected_outcome;
	
	public TrainingUtterance(String text, String expected_outcome){
		super(text);
		this.expected_outcome=expected_outcome;
	}
	
	public String getExpectedOutcome(){
		return expected_outcome;
	}
	
}
