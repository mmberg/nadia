package net.mmberg.nadia.processor.exceptions;

import net.mmberg.nadia.processor.NadiaProcessor;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProcessingException(String message){
		super("Processing Exception: "+message);
		NadiaProcessor.getLogger().severe(message);
	}
	
	public ProcessingException(){
		super("Processing Exception");
	}
}
