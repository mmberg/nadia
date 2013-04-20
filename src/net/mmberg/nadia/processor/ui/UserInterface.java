package net.mmberg.nadia.processor.ui;

public abstract class UserInterface {

	protected static UIConsumerFactory consumerFactory; 
	
	public void register(UIConsumerFactory factory){
		consumerFactory=factory;
	}
	
	public abstract void start();
	public abstract void stop();
	

	
}
