package net.mmberg.nadia.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInterface extends UserInterface{

	@Override
	public void send(String text) {
		System.out.println(text);
		
	}

	@Override
	public String receive() {
		String user_answer="";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			user_answer = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return user_answer;
	}

}
