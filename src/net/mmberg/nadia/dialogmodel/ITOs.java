package net.mmberg.nadia.dialogmodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

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
	
//	public void setITOs(ArrayList<ITO> itos){
//		super.addAll(itos);
//	}
}
