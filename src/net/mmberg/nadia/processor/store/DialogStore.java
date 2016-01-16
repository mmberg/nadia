package net.mmberg.nadia.processor.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.mmberg.nadia.processor.dialogmodel.*;
import net.mmberg.nadia.processor.dialogmodel.actions.*;
import net.mmberg.nadia.processor.dialogmodel.aqd.*;
import net.mmberg.nadia.processor.dialogmodel.taskselectors.*;

public class DialogStore {

	private static DialogStore dialogstore= null;
	private HashMap<String, Dialog> store = new HashMap<String, Dialog>();
	
	private DialogStore(){
		store.put("dummy1", createDummyDialog());
		store.put("dummy2", createDummyDialog2());
		store.put("dummy3", createDummyDialog3());
		store.put("eval", createEvaluationDialog());
		store.put("eval2", createEvaluationDialog2());
		store.put("eval3", createEvaluationDialog3());
	}
	
	public static DialogStore getInstance(){
		if(dialogstore==null){
			dialogstore=new DialogStore();
		}
		
		return dialogstore;
	}
	
	public static void main(String[] args){
		String test_dialog_name="dummy2";
		//test save
		DialogStore ds=DialogStore.getInstance();
		ds.getDialogFromStore(test_dialog_name).save();
		//test load
		Dialog d = Dialog.loadFromResourcesDir(test_dialog_name);
		System.out.println(d.toXML());
	}
	
	public Dialog getDialogFromStore(String key){
		return store.get(key);
	}
	
	private Dialog createDummyDialog(){
		
		Dialog dialog = new Dialog("dummy1");
				
		//a dialog consists of tasks
		Task task1=new Task("bsp");
		dialog.addTask(task1);
		
		//a task consists of ITOs
		ITO ito;
		AQD aqd;
		
		//1
		ito=new ITO("getDrinkDecision", "Would you like a drink?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("decision"));
		ito.setAQD(aqd);	
		
		//2
		ito=new ITO("getCity", "Where do you want to go?", false);	
		task1.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);	
				
		//3
		ito=new ITO("getDate", "When do you want to leave?");	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.temporal.date"), new AQDContext("begin","trip"), new AQDForm()); //TODO politeness and formality set on dialogue level
		ito.setAQD(aqd);	
		
		return dialog;			
	}	
	
private Dialog createDummyDialog2(){
		
		Dialog dialog = new Dialog("dummy2");
		dialog.setGlobal_politeness(2); //4
		dialog.setGlobal_formality(2); //4
		dialog.setStart_task_name("start");
		ITO ito;
		AQD aqd;
		
		//Task 0
		//----------------------------------------------
		Task task0=new Task("start");
		ArrayList<String> bagOfWords = new ArrayList<String>(Arrays.asList("hello"));
		task0.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		dialog.addTask(task0);
	
		//ITO 1
		ito=new ITO("welcome", "How may I help you?", false);	
		task0.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("open_ended"));
		ito.setAQD(aqd);
		
//		//no ITOs, but follow up... works!
//		ITO followito2=new ITO("anotherOne", "Do you want to play a game or know the weather?",false);
//		aqd=new AQD();
//		aqd.setType(new AQDType("item(game,weather)"));
//		followito2.setAQD(aqd);
//		FollowUp follow2=new FollowUp();
//		follow2.setIto(followito2);
//		HashMap<String,String> answerMapping2=new HashMap<String, String>();
//		answerMapping2.put("GAME", "guessNumber");
//		answerMapping2.put("WEATHER", "getWeatherInformation");
//		follow2.setAnswerMapping(answerMapping2);
//		task0.setFollowup(follow2);
		
		//Task 1
		//----------------------------------------------
		Task task1=new Task("getTripInformation");
		bagOfWords = new ArrayList<String>(Arrays.asList("travel","book", "journey","trip"));
		task1.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		
//		JavaAction jaction=new JavaAction("This trip from %getDepartureCity to %getDestinationCity costs #temperature Euros.");
//		try{
//			jaction.setPath("/Users/markus/");
//			jaction.setClassName("net.mmberg.nadia.processor.nlu.actions.TestExtJavaAction");
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
//		}
//		task1.setAction(jaction);
		
		GroovyAction gaction = new GroovyAction("This trip from %getDepartureCity to %getDestinationCity costs #price Euros.");
		gaction.setCode("executionResults.put(\"price\",\"257\")");
		//gaction.setReturnAnswer(false);		
		//gaction.addResultMapping(new ActionResultMapping("price","257","#price Euro for %getDestinationCity is cheap!",null));
		//gaction.addResultMapping(new ActionResultMapping("price","1000","#price Euro for %getDestinationCity is expensive!",null));
		
		task1.setAction(gaction);
		
		dialog.addTask(task1);
		
		//ITO 1
		ito=new ITO("getDepartureCity", "Where do you want to start?", true);	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("begin","trip"), new AQDForm());
		ito.setAQD(aqd);		
		
		//ITO 2
		ito=new ITO("getDestinationCity", "Where do you want to go?", true);	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("end","trip"), new AQDForm());
		ito.setAQD(aqd);
		
		//ITO 3
		ito=new ITO("getNumberOfPersons", "For how many persons?", false);	
		task1.addITO(ito);
		aqd=new AQD();
		aqd.setType(new AQDType("fact.quantity"));
		ito.setAQD(aqd);	
		
		//ITO 4
		ito=new ITO("getDate", "When do you want to leave?");	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.temporal.date"), new AQDContext("begin","trip"), new AQDForm());
		ito.setAQD(aqd);	
		
		//ITO 5
		ito=new ITO("getDate", "When do you want to come back?");	
		task1.addITO(ito);
		aqd=new AQD(new AQDType("fact.temporal.date"), new AQDContext("end","trip"), new AQDForm());
		ito.setAQD(aqd);
		
		
		//Task2
		//----------------------------------------------
		Task task2=new Task("getWeatherInformation");
		bagOfWords = new ArrayList<String>(Arrays.asList("weather","forecast", "temperature"));
		task2.setSelector(new BagOfWordsTaskSelector(bagOfWords));
//		gaction = new GroovyAction("The temperature in %getWeatherCity is #temperature degrees.");
//		gaction.setCode("" +
//				"import groovyx.net.http.*\r\n"+
//				"import javax.xml.xpath.*\r\n"+
//				"def http = new HTTPBuilder('http://weather.yahooapis.com')\r\n"+
//				"http.get( path: '/forecastrss', query:[w:'2502265',u:'c'],  contentType: ContentType.XML) { resp, xml -> \r\n"+
//				"   def temp = xml.channel.item.condition[0].@temp\r\n"+
//				"	executionResults.put(\"temperature\",temp.toString())\r\n"+
//				"}"
//		);
//		task2.setAction(gaction);
		
		//different weather service:
		HTTPAction httpaction=new HTTPAction("The temperature in %getWeatherCity is #result degrees.");
	    httpaction.setUrl("http://api.openweathermap.org/data/2.5/weather");
	    httpaction.setMethod("get");
	    httpaction.setParams("q=%getWeatherCity&mode=xml&units=metric");
	    httpaction.setXpath("/current/temperature/@value");
	    task2.setAction(httpaction);
		
		dialog.addTask(task2);
		
		//ITO 1
		ito=new ITO("getWeatherCity", "For which city do you want to know the weather?", false);	
		task2.addITO(ito);
		//an ITO is associated with AQDs
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);		
		
		//Task3
		//-----------------------------------------------
		Task task3 = new Task("getWikipediaCityInfo");
		bagOfWords = new ArrayList<String>(Arrays.asList("wikipedia","tell me about", "tell me something", "know about"));
		task3.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		httpaction=new HTTPAction("#result");
		httpaction.setUrl("http://en.wikipedia.org/w/api.php");
		httpaction.setMethod("get");
		httpaction.setParams("format=xml&action=query&prop=extracts&explaintext&exsentences=1&titles=%getWikiCity");
		httpaction.setXpath("//extract");
		task3.setAction(httpaction);
		dialog.addTask(task3);
		
		//ITO1
		ito=new ITO("getWikiCity", "What city do you want to know more about?",false);
		task3.addITO(ito);
		aqd=new AQD();
		aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
		ito.setAQD(aqd);
		
		//FollowUp ITO
		ITO followito=new ITO("anotherOne", "Do you want to know about other cities?",false);
		aqd=new AQD();
		aqd.setType(new AQDType("decision"));
		followito.setAQD(aqd);
		FollowUp follow=new FollowUp();
		follow.setIto(followito);
		HashMap<String,String> answerMapping=new HashMap<String, String>();
		answerMapping.put("YES", "getWikipediaCityInfo");
		follow.setAnswerMapping(answerMapping);
		task3.setFollowup(follow);
		
		//Task4
		//-----------------------------------------------
		Task task4 = new Task("setLightbulb");
		task4.setAct("action");
		bagOfWords = new ArrayList<String>(Arrays.asList("bulb","switch"));
		task4.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		httpaction=new HTTPAction("#result");
		httpaction.setUrl("http://mmberg.net:8080/Lightbulb/Lightbulb");
		httpaction.setMethod("post");
		httpaction.setParams("state=%getLightAction");
		httpaction.setXpath("//message");
		task4.setAction(httpaction);
		dialog.addTask(task4);
		
		//ITO1
		ito=new ITO("getLightAction", "Do you want to switch it on or off?",false);
		task4.addITO(ito);
		aqd=new AQD();
		aqd.setType(new AQDType("onoff"));
		ito.setAQD(aqd);	
		
		//Task5 (this task has no ITOs!!!)
		//-----------------------------------------------
		Task task5 = new Task("getLightbulb");
		task5.setAct("seek");
		bagOfWords = new ArrayList<String>(Arrays.asList("bulb","switch"));
		task5.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		httpaction=new HTTPAction("#result");
		httpaction.setUrl("http://mmberg.net:8080/Lightbulb/Lightbulb");
		httpaction.setMethod("get");
		httpaction.setParams("getstate");
		httpaction.setXpath("//message");
		task5.setAction(httpaction);
		dialog.addTask(task5);
		
		
		//Task6
		//-----------------------------------------------
		Task task6 = new Task("guessNumber");
		task6.setAct("seek");
		bagOfWords = new ArrayList<String>(Arrays.asList("guess","number","play"));
		task6.setSelector(new BagOfWordsTaskSelector(bagOfWords));
		httpaction=new HTTPAction("%getNumber ");
		httpaction.setUrl("http://mmberg.net:8080/NumberGuessing/NumberGuessing");
		httpaction.setMethod("get");
		httpaction.setParams("guess=%getNumber");
		httpaction.setXpath("//code");
		httpaction.addResultMapping(new ActionResultMapping("result","TOO_BIG","was too big.","guessNumber"));
		httpaction.addResultMapping(new ActionResultMapping("result","TOO_SMALL","was too small.","guessNumber"));
		httpaction.addResultMapping(new ActionResultMapping("result","CORRECT","it is! Congratulations!",null));
		task6.setAction(httpaction);
		dialog.addTask(task6);
		
		//ITO1
		ito=new ITO("getNumber", "Guess a number between 1 and 99!",false);
		task6.addITO(ito);
		aqd=new AQD();
		aqd.setType(new AQDType("fact.quantity"));
		ito.setAQD(aqd);	
		
		//FollowUp ITO
		followito=new ITO("playAgain", "Do you want to play again?",false);
		aqd=new AQD();
		aqd.setType(new AQDType("decision"));
		followito.setAQD(aqd);
		follow=new FollowUp();
		follow.setIto(followito);
		answerMapping=new HashMap<String, String>();
		answerMapping.put("YES", "guessNumber");
		follow.setAnswerMapping(answerMapping);
		task6.setFollowup(follow);

		return dialog;			
	}	

private Dialog createDummyDialog3(){
	
	Dialog dialog = new Dialog("dummy3");
	dialog.setGlobal_politeness(4);
	dialog.setGlobal_formality(4);
	dialog.setStart_task_name("getWeatherInformation");
	ITO ito;
	AQD aqd;
	
	Task task2=new Task("getWeatherInformation");
	ArrayList<String> bagOfWords = new ArrayList<String>(Arrays.asList("weather","forecast", "temperature","trip"));
	task2.setSelector(new BagOfWordsTaskSelector(bagOfWords));
	GroovyAction gaction = new GroovyAction("The temperature in %getWeatherCity is #temperature degrees.");
	gaction.setCode("" +
			"import groovyx.net.http.*\r\n"+
			"import javax.xml.xpath.*\r\n"+
			"def http = new HTTPBuilder('http://weather.yahooapis.com')\r\n"+
			"http.get( path: '/forecastrss', query:[w:'2502265',u:'c'],  contentType: ContentType.XML) { resp, xml -> \r\n"+
			"   def temp = xml.channel.item.condition[0].@temp\r\n"+
			"	executionResults.put(\"temperature\",temp.toString())\r\n"+
			"}"
	);
	task2.setAction(gaction);
	dialog.addTask(task2);
	
	//ITO 1
	ito=new ITO("getWeatherCity", "For which city do you want to know the weather?", false);	
	task2.addITO(ito);
	//an ITO is associated with AQDs
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);		
	
	return dialog;			
}	

private Dialog createEvaluationDialog(){
	Dialog dialog = new Dialog("eval");

	ITO ito;
	AQD aqd;
	
	Task task0=new Task("welcome");
	ito = new ITO("open","How may I help you?",false);
	aqd = new AQD(new AQDType("open_ended"), new AQDContext(), new AQDForm());
	ito.setAQD(aqd);
	task0.addITO(ito);
	
	Task task1=new Task("weather");
	task1.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("weather"))));
	ito=new ITO("getWeatherCity", "For which city do you want to know the weather?", false);	
	//an ITO is associated with AQDs
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	ito=new ITO("getForecastType", "Do you want to know the weather for today or tomorrow?", false);	
	ito.setRequired(false);
	//an ITO is associated with AQDs
	aqd=new AQD();
	aqd.setType(new AQDType("item(today,tomorrow)"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	
	HTTPAction httpaction=new HTTPAction("The temperature in %getWeatherCity will be #result degrees tomorrow.");
    httpaction.setUrl("http://api.openweathermap.org/data/2.5/forecast/daily");
    httpaction.setMethod("get");
    httpaction.setParams("q=%getWeatherCity&mode=xml&units=metric&cnt=2");
    httpaction.setXpath("/weatherdata/forecast/time[last()]/temperature/@day");
	
//	HTTPAction httpaction=new HTTPAction("The temperature in %getWeatherCity is #result degrees.");
//    httpaction.setUrl("http://api.openweathermap.org/data/2.5/weather");
//    httpaction.setMethod("get");
//    httpaction.setParams("q=%getWeatherCity&mode=xml&units=metric");
//    httpaction.setXpath("/current/temperature/@value");
    task1.setAction(httpaction);
	
	dialog.addTask(task0);
	dialog.addTask(task1);
	
	return dialog;
}

private Dialog createEvaluationDialog2(){
	Dialog dialog = new Dialog("eval2");

	ITO ito;
	AQD aqd;
	
	Task task0=new Task("welcome");
	ArrayList<String> bagOfWords = new ArrayList<String>(Arrays.asList("hello"));
	task0.setSelector(new BagOfWordsTaskSelector(bagOfWords));
	ito = new ITO("open","How may I help you?",false);
	aqd = new AQD(new AQDType("open_ended"), new AQDContext(), new AQDForm());
	ito.setAQD(aqd);
	task0.addITO(ito);
	
	Task task1=new Task("route");
	task1.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("time","how long", "route"))));
	
	ito=new ITO("getDepartureCity", "Where do you want to start?", false);	
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	ito=new ITO("getDestinationCity", "Where do you want to go?", false);	
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	
	HTTPAction httpaction=new HTTPAction("You will need #result.");
    httpaction.setUrl("http://maps.googleapis.com/maps/api/directions/xml");
    httpaction.setMethod("get");
    httpaction.setParams("origin=%getDepartureCity&destination=%getDestinationCity&sensor=false&mode=driving&alternatives=false");
    httpaction.setXpath("/DirectionsResponse/route/leg/duration/text");
    task1.setAction(httpaction);
    
    
    Task task2=new Task("news");
	task2.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("news","headlines", "what's on"))));
	
	ito=new ITO("category", "What category are you interested in?", false);	
	ito.setRequired(false);
	aqd=new AQD();
	aqd.setType(new AQDType("item(sport,business,economy,politics)"));
	ito.setAQD(aqd);	
	task2.addITO(ito);
	
	httpaction=new HTTPAction("Today's headline is: #result.");
    httpaction.setUrl("http://news.google.com/?output=rss");
    httpaction.setMethod("get");
    httpaction.setParams("q=%category");
    httpaction.setXpath("/rss/channel/item/title");
    task2.setAction(httpaction);
	
	dialog.addTask(task0);
	dialog.addTask(task1);
	dialog.addTask(task2);
	
	return dialog;
}

private Dialog createEvaluationDialog3(){
	Dialog dialog = new Dialog("eval3");

	ITO ito;
	AQD aqd;
	
	Task task0=new Task("welcome");
	ArrayList<String> bagOfWords = new ArrayList<String>(Arrays.asList("hello"));
	task0.setSelector(new BagOfWordsTaskSelector(bagOfWords));
	ito = new ITO("open","How may I help you?",false);
	aqd = new AQD(new AQDType("open_ended"), new AQDContext(), new AQDForm());
	ito.setAQD(aqd);
	task0.addITO(ito);
	
	Task task1=new Task("route");
	task1.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("how far", "distance"))));
	
	ito=new ITO("getDepartureCity", "Where do you want to start?", false);	
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	ito=new ITO("getDestinationCity", "Where do you want to go?", false);	
	aqd=new AQD();
	aqd.setType(new AQDType("fact.named_entity.non_animated.location.city"));
	ito.setAQD(aqd);	
	task1.addITO(ito);
	
	
	HTTPAction httpaction=new HTTPAction("%getDepartureCity is #result away from %getDestinationCity.");
    httpaction.setUrl("http://maps.googleapis.com/maps/api/directions/xml");
    httpaction.setMethod("get");
    httpaction.setParams("origin=%getDepartureCity&destination=%getDestinationCity&sensor=false&mode=driving&alternatives=false");
    httpaction.setXpath("/DirectionsResponse/route/leg/distance/text");
    task1.setAction(httpaction);
    
    
    Task task2=new Task("news");
	task2.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("news","headlines", "what's on"))));
	
	ito=new ITO("category", "What category are you interested in?", false);	
	ito.setRequired(false);
	aqd=new AQD();
	aqd.setType(new AQDType("item(sport,business,economy,politics)"));
	ito.setAQD(aqd);	
	task2.addITO(ito);
	
	httpaction=new HTTPAction("Today's headline is: #result.");
    httpaction.setUrl("http://news.google.com/?output=rss");
    httpaction.setMethod("get");
    httpaction.setParams("q=%category");
    httpaction.setXpath("/rss/channel/item/title");
    task2.setAction(httpaction);
	
    Task task3=new Task("getTripInformation");
	bagOfWords = new ArrayList<String>(Arrays.asList("travel","book", "journey","trip"));
	task3.setSelector(new BagOfWordsTaskSelector(bagOfWords));
	
	ito=new ITO("getDepartureCity", "Where do you want to start?", true);	
	task3.addITO(ito);
	aqd=new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("begin","trip"), new AQDForm());
	ito.setAQD(aqd);		
	
	ito=new ITO("getDestinationCity", "Where do you want to go?", true);	
	task3.addITO(ito);
	aqd=new AQD(new AQDType("fact.named_entity.non_animated.location.city"), new AQDContext("end","trip"), new AQDForm());
	ito.setAQD(aqd);

	GroovyAction gaction = new GroovyAction("This trip from %getDepartureCity to %getDestinationCity costs #price Euros.");
	gaction.setCode("Random r=new Random(); Integer p=r.nextInt(300)+100; executionResults.put(\"price\",p.toString())");
	//gaction.setCode("Random r=new Random(); Integer p=r.nextInt(300)+100; executionResults.put(\"price\",p.toString()+frame.get(\"getDepartureCity\"))");
	task3.setAction(gaction);
	
	
	Task task4=new Task("calculator");
	task4.setSelector(new BagOfWordsTaskSelector(new ArrayList<String>(Arrays.asList("numbers","math","calculator"))));
		
	ito=new ITO("number1", "Please tell me the first number!", false);	
	ito.setRequired(true);
	aqd=new AQD();
	aqd.setType(new AQDType("fact.quantity"));
	ito.setAQD(aqd);	
	task4.addITO(ito);
	
	ito=new ITO("number2", "Please tell me the second number!", false);	
	ito.setRequired(true);
	aqd=new AQD();
	aqd.setType(new AQDType("fact.quantity"));
	ito.setAQD(aqd);	
	task4.addITO(ito);
	
	ito=new ITO("op", "Please tell me the operation!", false);	
	ito.setRequired(true);
	aqd=new AQD();
	aqd.setType(new AQDType("item(add,subtract,multiply,divide)"));
	ito.setAQD(aqd);	
	task4.addITO(ito);
		
	gaction = new GroovyAction("%number1 %op %number2 is #res.");
	gaction.setCode(""
			+ "Integer one=new Integer(frame.get(\"number1\"));\r\n"
			+ "Integer two=new Integer(frame.get(\"number2\"));\r\n"
			+ "Float res=0.0;\r\n"
			+ "switch(frame.get(\"op\")){\r\n"
			+ "case \"ADD\": res=one+two; break;\r\n"
			+ "case \"SUBTRACT\": res=one-two; break;\r\n"
			+ "case \"DIVIDE\": res=one/two; break;\r\n"
			+ "case \"MULTIPLY\": res=one*two;break;\r\n"
			+ "}\r\n"
			+ "executionResults.put(\"res\",res.toString());"
	);
	task4.setAction(gaction);
	
	
	dialog.addTask(task0);
	dialog.addTask(task1);
	dialog.addTask(task2);
	dialog.addTask(task3);
	dialog.addTask(task4);
	
	return dialog;
}

}