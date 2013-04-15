package net.mmberg.nadia.processor.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage.Meta;

public class ConsoleInterface extends UserInterface{

	private void send(String text) {
		System.out.println(text);
	}

	private String receive() {
		String user_answer="";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			user_answer = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return user_answer;
	}


	@Override
	public void start() {
		UIConsumerMessage message=consumer.processUtterance(null);
		String systemUtterance=message.getSystemUtterance();
		send(systemUtterance);
		
		String userUtterance;		
		while(!(userUtterance = receive()).equals("\r\n")){
			message=consumer.processUtterance(userUtterance);
			systemUtterance=message.getSystemUtterance();
			if(message.getMeta()==Meta.END_OF_DIALOG) break;
			send(systemUtterance);
		}
	}

	@Override
	public void stop() {
		
	}
}
