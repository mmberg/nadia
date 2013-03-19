package net.mmberg.nadia.ui;

public abstract class UserInterface {

	public abstract void send(String text);
	public abstract String receive();
	
	public String exchange(String text){
		send(text);
		return receive();
	}
}
