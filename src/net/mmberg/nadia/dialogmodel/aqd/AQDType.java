package net.mmberg.nadia.dialogmodel.aqd;

public class AQDType {

	//serializable members
	private String answerType;
	
	//Serialization getter/setter
	public AQDType(){
		
	}
	
	public String getAnswerType(){
		return answerType;
	}
	
	public void setAnswerType(String answerType){
		this.answerType=answerType;
	}
	
	//Content
	public AQDType(String answerType){
		this.answerType=answerType;
	}
	
}
