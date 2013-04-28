package net.mmberg.nadia.processor.manager.contexthelper;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;

public class HistoryTree {
    private HistoryTree parent=null;
    private HistoryElem data;
    private ArrayList<HistoryTree> children;
    private int level;
    
    public HistoryTree(){
    	this.children = new ArrayList<HistoryTree>();
    }
    
    public HistoryTree(int level){
    	this();
    	this.level=level;
    }
    
    public HistoryTree(HistoryTree parent, int level){
    	this();
    	this.parent=parent;
    	this.level=level;
    }
    public HistoryTree(HistoryElem data, HistoryTree parent, int level) {
    	this(parent, level);
        this.data = data;
    }
    
    public void addLeaf(HistoryElem data){
    	this.data=data;
    }
    
    public void addChild(HistoryTree child){
    	this.children.add(child);
    }
    
    public HistoryTree getParent(){
    	return this.parent;
    }
    
    public int getLevel(){
    	return this.level;
    }
    
    @XmlAttribute(name="hasChildren")
    private boolean getExistChildren(){
    	return !children.isEmpty();
    }
    
    @XmlElement(name="child")
    public ArrayList<HistoryTree> getChildren(){
    	return this.children;
    }
    
    @XmlElement(name="utterance")
    public HistoryElem getLeaf(){
    	return data;
    }
    

}
