package net.mmberg.nadia.processor.lg.dialog;

public class Utterance {

		private String utterance;
		private String surface;
		private Meaning meaning;
		private Style style;
		//private InterroElem realizerClass;
		//private Document logicalForm;

		private int state;
		
		private static class State{
			public static int CREATED=0;
			public static int REALIZED=1;
		}
		
		public Utterance(Meaning meaning){
			this.meaning=meaning;
			setState(State.CREATED);
		}
		
		private void setState(int state){
			this.state=state;
		}
		
		public void setSurface(String phrase){
			this.surface=phrase;
			setState(State.REALIZED);
		}
		
		@Override
		public String toString(){
			return utterance;
		}
			
		private String beautify(String phrase){
			phrase=phrase.replaceAll("_", " ");
			phrase=phrase.substring(0, 1).toUpperCase()+phrase.substring(1);
			return phrase;
		}
}
