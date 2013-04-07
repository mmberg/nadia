package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;

public class ITOs extends ArrayList<ITO> {

	private static final long serialVersionUID = 1L;

	public Frame toFrame(){
		return new Frame();
	}
	
	public boolean allRequiredSlotsFilled(){
		return false;
	}
	
	public ArrayList<ITO> getITOs(){
		return this;
	}
	
}
