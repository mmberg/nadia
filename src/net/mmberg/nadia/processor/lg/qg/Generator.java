package net.mmberg.nadia.processor.lg.qg;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import net.mmberg.nadia.processor.NadiaProcessorConfig;
import net.mmberg.nadia.processor.dialogmodel.aqd.AQD;
import net.mmberg.nadia.processor.lg.qg.interrogatives.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import opennlp.ccg.realize.*;
import opennlp.ccg.synsem.LF;
import opennlp.ccg.grammar.*;

public class Generator {

	private static Generator instance;
	private Grammar grammar;
	private Realizer realizer;
	private boolean print=false;
	private List<InterroElem> interrogatives;
	private Lexicon lex;
	
	private class InterroElem{
		public List<String> applicable_types;
		public Interrogative interrogative;
		public List<Object> params;
		public float politeness;
		
		public InterroElem(List<String> applicable_types, Interrogative interrogative, List<Object> params, float politeness){
			this.applicable_types=applicable_types;
			this.interrogative=interrogative;
			this.params=params;
			this.politeness=politeness;
		}
	}

	public static Generator getInstance(){
		if(instance==null){
			try{
				NadiaProcessorConfig config=NadiaProcessorConfig.getInstance();
				instance=new Generator(new URL(config.getProperty(NadiaProcessorConfig.CCGGRAMMARPATH)), new URL(config.getProperty(NadiaProcessorConfig.ONTOLOGYPATH)));
			}
			catch(MalformedURLException ex){
				ex.printStackTrace();
			}
		}
		return instance;
	}

	private Generator(URL grammarURL, URL ontologyURL){
		this.init(grammarURL, ontologyURL);
	}
	
	private void init(URL grammarURL, URL ontologyURL) {
		try {
			grammar = new Grammar(grammarURL);
			realizer = new Realizer(grammar);
			lex = new Lexicon(ontologyURL);
					
			//SAXBuilder builder = new SAXBuilder();
			//Document lfxml = builder.build("./res/7/nowtellme"); //test3
			
			
			//the list of interrogatives is dependent on the question type (a y/n-type cannot be asked as a wh-question) 
			
			//List of Interrogatives with parameters (politeness,subj) and associated politeness score
			interrogatives=new ArrayList<InterroElem>();
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new N(), Arrays.asList((Object)false,false),-2));

			interrogatives.add(new InterroElem(Arrays.asList("fact"), new NRequest(), Arrays.asList((Object)false,false),-1));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new WhRequest(), Arrays.asList((Object)false,false),-1));

			interrogatives.add(new InterroElem(Arrays.asList("fact"), new N(), Arrays.asList((Object)true,false),0));
			
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new NRequest(), Arrays.asList((Object)true,false),1));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new WhRequest(), Arrays.asList((Object)true,false),1));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new WhQuestion(), Arrays.asList((Object)false,false),2));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanWhQuestion(), Arrays.asList((Object)false,false),3));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanNQuestion(), Arrays.asList((Object)false,false),3));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanWhQuestion(), Arrays.asList((Object)true,false),4));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanNQuestion(), Arrays.asList((Object)true,false),4));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanWhQuestion(), Arrays.asList((Object)false,true),4));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanNQuestion(), Arrays.asList((Object)false,true),4));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanWhQuestion(), Arrays.asList((Object)true,true),5));
			interrogatives.add(new InterroElem(Arrays.asList("fact"), new CanNQuestion(), Arrays.asList((Object)true,true),5));
			//decision:
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new N(), Arrays.asList((Object)false,false),-2));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new N(), Arrays.asList((Object)true,false),-1));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),0));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),1));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),2));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),3));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),4));
			interrogatives.add(new InterroElem(Arrays.asList("decision"),new YNQuestion(), Arrays.asList((Object)false,false),5));

			//TODO
			//wh-n-qu:
			//what's your destination?
			//wh-ing:
			//where are you leaving from?
			//where are you heading to?
			//ellipses:
			//...and your destination?
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * The type describes the top level of the question type hierarchy (e.g., fact, decision)
	 * @param type
	 * @param politeness
	 * @return
	 */
	private ArrayList<InterroElem> getInterroElemsbyTypeAndPoliteness(String type, int politeness){
		String basetype=type.split("\\.")[0];
		ArrayList<InterroElem> elems=new ArrayList<InterroElem>();
		for(InterroElem elem : interrogatives){
			if(elem.applicable_types.contains(basetype) && elem.politeness==politeness) elems.add(elem);
		}
		return elems;
	}
	

	public String generateQuestion(Interrogative type, WordConf wconf, boolean opener, boolean sayPlease, boolean subj){
		return realize(type.createLF(wconf.getWhWord(), wconf.getVerb(), wconf.getNoun(), opener, sayPlease, subj));
	}
	
	public WordConf chooseWords(String dimension, String specification, String referent, int formality){
		
		String[] dimArr;
		if(dimension.contains(".")){
			dimArr=dimension.split("\\.");
			//if(dimArr.length==2) dimArr=new String[]{dimArr[1], dimArr[1]};
		}
		else{
			dimArr=new String[]{dimension}; //dimArr=new String[]{dimension, dimension};
		}
		
		String d1= (dimArr.length>1)?dimArr[dimArr.length-2]:dimArr[0];
		String d2= (dimArr.length>1)?dimArr[dimArr.length-1]:dimArr[0];
			
		String wh_word=lex.getLex(d1, specification, referent, "whadv", formality).get(0);
		String verb=lex.getLex(d1, specification, referent, "v", formality).get(0);
		String noun=lex.getLex(d2, specification, referent, "n", formality).get(0);
		
		/*
		String wh_word=lex.getLex(dimArr[dimArr.length-2], specification, referent, "whadv", formality).get(0);
		String verb=lex.getLex(dimArr[dimArr.length-2], specification, referent, "v", formality).get(0);
		String noun=lex.getLex(dimArr[dimArr.length-1], specification, referent, "n", formality).get(0);
		*/
		
		return new WordConf(wh_word, verb, noun);
	}
	
	public String generateParaphrase(GenConf conf){
		return generateParaphrase(conf.getType(), conf.getWhWord(), conf.getVerb(), conf.getNoun(), conf.getFormality(), conf.getPoliteness(), conf.isOpener());
	}
	
	public ArrayList<String> generateParaphrases(int number, GenConf conf){
		return generateParaphrases(number, conf.getType(), conf.getWhWord(), conf.getVerb(), conf.getNoun(), conf.getFormality(), conf.getPoliteness(), conf.isOpener());
	}
	
	private ArrayList<String> generateParaphrases(int n, String dimension, String wh_word, String verb, String noun, int formality, int politeness, boolean opener){
		
		ArrayList<String> candidates= new ArrayList<String>();
		
		//politeness:
		// - say please 
		// - (modal particles? -> could you PERHAPS / POSSIBLY ...)
		// - command > question > indirect question / subj (sentence structure)
		//formality:
		// - choice of words (context: academic, party, written/spoken...)
		// - T/V (social distance)
		
		ArrayList<InterroElem> elems=getInterroElemsbyTypeAndPoliteness(dimension, politeness);
		for(int i=0; i<n && i<elems.size(); i++){
			InterroElem elem=elems.get(i);
			candidates.add(
					realize(elem.interrogative.createLF(wh_word, verb, noun, opener, (Boolean)elem.params.get(0), (Boolean)elem.params.get(1))));
		}
			
		return candidates;
	}
	
	private String generateParaphrase(String dimension, String wh_word, String verb, String noun, int formality, int politeness, boolean opener){
					
		ArrayList<InterroElem> elems=getInterroElemsbyTypeAndPoliteness(dimension, politeness);
		InterroElem elem=elems.get(0);
		return realize(elem.interrogative.createLF(wh_word, verb, noun, opener, (Boolean)elem.params.get(0), (Boolean)elem.params.get(1)));
		
		//TODO return object instead of string, need information which class created phrase in order to prevent repetitive styles
	}
	
	private String realize(Element lf_xml){
		Document lf_doc=createDoc(lf_xml);
		LF lf;
		try {
			lf = grammar.loadLF(lf_doc); //lf = Realizer.getLfFromDoc(lfxml); <-- this example from literature is wrong!
			
			if(print){
				System.out.println("Realizing:\r\n"+lf.prettyPrint("")); //don't know what the argument is for
				
				XMLOutputter serializer= new XMLOutputter (Format.getPrettyFormat());
				serializer.output(lf_doc, System.out);
			}
			
			Edge bestEdge = realizer.realize(lf);
			return bestEdge.getSign().getOrthography();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} 

	}
	
	private Document createDoc(Element lf){
		Document lfxml=new Document();
		Element xml=new Element("xml");
		Element lfnode=new Element("lf");
		lfnode.addContent(lf);
		xml.addContent(lfnode);
		lfxml.setRootElement(xml);
		return lfxml;
	}
	
	public void printParaphrases(String dimension, String specification, String referent, int politeness, int formality, boolean opener){
		printParaphrases(dimension, specification, referent, politeness, politeness, formality, formality, opener);
	}
	
	public void printParaphrases(String dimension, String specification, String referent, int politeness_from, int politeness_to, int formality_from, int formality_to, boolean opener){
		//specification == "constraint"?
			
		//meaning: dimension, spec, referent
		//syntax: pos
		int formality;
		int politeness;
		for(formality=formality_from; formality<formality_to+1; formality++){
			for(politeness=politeness_from; politeness<politeness_to+1; politeness++){
		
				WordConf wconf = chooseWords(dimension, specification, referent, formality);
				GenConf gconf=new GenConf(dimension, wconf.getWhWord(), wconf.getVerb(), wconf.getNoun(), formality, politeness, opener);
				
				
				//show all phrases (with alternatives in brackets)
				ArrayList<String> phrases=generateParaphrases(3, gconf);
				String phrase=phrases.get(0);
				if(phrases.size()>1){
					phrase+=" (";
					for(int i=1; i<phrases.size(); i++) phrase+=phrases.get(i)+", ";
					phrase=phrase.substring(0, phrase.length()-2);
					phrase+=")";
				}
				
//				//select random
//				Random randomizer = new Random();
//				int random=randomizer.nextInt(phrases.size());
//				String phrase=phrases.get(random);

				printBeautifully(phrase, true);
			}
		}
	}
	
	public String generateQuestion(AQD aqd){
		WordConf wconf = chooseWords(aqd.getType().getAnswerType(), aqd.getContext().getSpecification(), aqd.getContext().getReference(), aqd.getForm().getFormality());
		GenConf gconf=new GenConf(aqd.getType().getAnswerType(), wconf.getWhWord(), wconf.getVerb(), wconf.getNoun(), aqd.getForm().getFormality(), aqd.getForm().getPoliteness(), aqd.getForm().getTemporalOpener());
		return makeBeautifully(generateParaphrase(gconf));
	}
	
	
	private String makeBeautifully(String phrase){
		phrase=phrase.replaceAll("_", " ");
		phrase=phrase.substring(0, 1).toUpperCase()+phrase.substring(1);
		return phrase;
	}
	
	private void printBeautifully(String phrase, boolean asDialogue){
	
		phrase=makeBeautifully(phrase);
		
		if(asDialogue){
			System.out.println("S: "+phrase);
			System.out.println("U: ...");
		}
		else System.out.println(phrase);
	}
}
