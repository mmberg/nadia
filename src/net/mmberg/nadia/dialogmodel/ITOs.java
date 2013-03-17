package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;

public class ITOs extends ArrayList<ITO> {

	
	public Frame toFrame(){
		return new Frame();
	}
	
	public boolean allRequiredSlotsFilled(){
		return false;
	}
}
