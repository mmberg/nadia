package net.mmberg.nadia.processor.exceptions;

import net.mmberg.nadia.processor.NadiaProcessor;

/**
 * Error within the dialogue model that prevents the processor from running it correctly.
 * @author markus
 *
 */
public class ModelException extends Exception {

	private static final long serialVersionUID = 1L;

	public ModelException(String message){
		super("Model Exception: "+message);
		NadiaProcessor.getLogger().severe(message);
	}
	
	public ModelException(){
		super("Processing Exception");
	}
}
