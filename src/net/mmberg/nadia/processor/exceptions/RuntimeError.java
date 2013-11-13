package net.mmberg.nadia.processor.exceptions;

import net.mmberg.nadia.processor.NadiaProcessor;

public class RuntimeError extends Exception {

	private static final long serialVersionUID = 1L;

	public RuntimeError(String message){
		super("RuntimeError: "+message);
		NadiaProcessor.getLogger().severe(message);
	}
	
	public RuntimeError(){
		super("RuntimeError");
	}
}
