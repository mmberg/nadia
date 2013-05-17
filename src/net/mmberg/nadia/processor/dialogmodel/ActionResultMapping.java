package net.mmberg.nadia.processor.dialogmodel;

import net.mmberg.nadia.dialogmodel.definition.ActionResultMappingModel;

public class ActionResultMapping extends ActionResultMappingModel{

	public ActionResultMapping(){
		super();
	}
	
	public ActionResultMapping(String resultVarName, String resultValue, String message, String redirectToTask){
		super(resultVarName,resultValue,message,redirectToTask);
	}
}
