package net.mmberg.nadia.processor.manager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.mmberg.nadia.processor.dialogmodel.*;
import net.mmberg.nadia.processor.manager.contexthelper.HistoryElem;
import net.mmberg.nadia.processor.manager.contexthelper.HistoryTree;


@XmlRootElement
@XmlType(propOrder={"serializeDialog","dialogHistory","serializeTaskStack","serializeFrameRepresentation","serializeCurrentTask","questionOpen","currentQuestionUtterance","started","lastAccess","createdOn","additionalDebugInfo"})
public class DialogManagerContext {

	//Features
	private Date createdOn;
	private Date lastAccess;
	private String additionalDebugInfo;
	private Boolean question_open=false;
	private Boolean started=false;
	private ArrayList<ITO> ito_history=new ArrayList<ITO>();
	//private Task task;
	private HistoryTree history = new HistoryTree(1);
	private HistoryTree current_node = history;
	private Stack<Task> taskStack=new Stack<Task>();
	private Iterator<ITO> ito_iterator=null;
	private Dialog dialog=null;
	
	public enum UTTERANCE_TYPE {USER,SYSTEM};
	
	//getter
	public Boolean isQuestionOpen() {
		return question_open;
	}
		
	public Date getCreatedOn() {
		return createdOn;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getAdditionalDebugInfo() {
		return additionalDebugInfo;
	}
	
	public Boolean isStarted() {
		return started;
	}


	@XmlElement(name="currentQuestion")
	private String getCurrentQuestionUtterance(){
		if(question_open){
			return getCurrentQuestion().getUtteranceText();
		}
		else return "no current question";
	}

	
	@XmlElement(name="dialogHistory")
	private HistoryTree getDialogHistory(){
		return history;
	}
	
	
	/**
	 * 
	 * @return an XML-compatible list-representation of the frame for pretty printing
	 */
	@XmlElementWrapper(name="frame")
	@XmlElement(name="entry")
	public List<String> getSerializeFrameRepresentation(){
		ArrayList<String> frame = new ArrayList<String>();
		//for(Map.Entry<Object, Object> entry : task.toFrame().entrySet()){
		for(Map.Entry<Object, Object> entry : taskStack.lastElement().toFrame().entrySet()){
			frame.add(entry.getKey().toString()+": "+entry.getValue().toString());
		}
		return frame;
		//return task.toFrame(); //does not work on my Windows Setup... On Mac it works fine...
	}
	
	@XmlElement(name="currentTask")
	public String getSerializeCurrentTask() {
		if(taskStack.size()>0) return taskStack.lastElement().getName();
		else return "none";
	}
	
	
	@XmlElementWrapper(name="taskStack")
	@XmlElement(name="task")
	public List<String> getSerializeTaskStack(){
		ArrayList<String> stack = new ArrayList<String>();
		for(Task t : taskStack){
			stack.add(t.getName());
		}
		return stack;
	}
	
	@XmlElement(name="dialog")
	public String getSerializeDialog() {
		return dialog.getName();
	}


	//transient getters
	
	@XmlTransient
	public Dialog getDialog(){
		return dialog;
	}
	
	
	@XmlTransient
	public ITO getCurrentQuestion(){
		return ito_history.get(ito_history.size()-1);
	}
	
	@XmlTransient
	public Iterator<ITO> getIto_iterator() {
		return ito_iterator;
	}
	
	@XmlTransient
	public Task getCurrentTask() {
		if(taskStack.size()>0) return taskStack.lastElement();
		else return null;
	}

	@XmlTransient
	public Stack<Task> getTaskStack() {
		return taskStack;
	}
	
	@XmlTransient
	public ArrayList<ITO> getHistory(){
		return ito_history;
	}
	
	//setters

	public void setDialog(Dialog dialog){
		this.dialog=dialog;
	}
	
	
	public void setStarted(Boolean started) {
		this.started = started;
	}

	public void setQuestionOpen(Boolean question_open) {
		this.question_open = question_open;
	}
	
	public void setCurrentQuestion(ITO question){
		if(question!=null){
			setQuestionOpen(true);
			ito_history.add(question);
		}
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public void setAdditionalDebugInfo(String additionalDebugInfo) {
		this.additionalDebugInfo = additionalDebugInfo;
	}
	
	public void addUtteranceToHistory(String utterance, UTTERANCE_TYPE type, int level){
		
		//create tree (hierarchical representation according to task level)
		//used as basis for output as HTML list
		if(level==current_node.getLevel()){
			current_node.addChild(new HistoryTree(new HistoryElem(utterance, level, type), current_node, level));
		}
		else if (level>current_node.getLevel()){
			HistoryTree child=new HistoryTree(current_node, level);
				HistoryTree leaf=new HistoryTree(new HistoryElem(utterance, level, type), child, level);
				child.addChild(leaf);
			current_node.addChild(child);
			current_node=child;
		}
		else{
			current_node=current_node.getParent();
			current_node.addChild(new HistoryTree(new HistoryElem(utterance, level, type), current_node, level));
		}
		
	}
	
//	public void setTask(Task task){
//		this.task=task;
//	}
	
	public void setIto_iterator(Iterator<ITO> ito_iterator) {
		this.ito_iterator = ito_iterator;
	}
	
	//serialize
	
	public String serialize(){
		JAXBContext context;
		String result="";
		try {
			context = JAXBContext.newInstance(DialogManagerContext.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    m.marshal(this, bout);
		    result = bout.toString();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void print(){
		System.out.println(serialize());
	}
	
	
	
}
