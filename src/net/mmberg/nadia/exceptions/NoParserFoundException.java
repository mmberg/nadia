package net.mmberg.nadia.exceptions;

public class NoParserFoundException extends Exception{

	private static final long serialVersionUID = 1L;

	public NoParserFoundException(String message){
		super("No parser found: "+message);
	}
	
	public NoParserFoundException(){
		super("No parser found");
	}

}
