package net.mmberg.nadia.processor.lg.qg;

import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.mmberg.nadia.Nadia;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Lexicon {
	
	private OntModel model;
    private String prefix="PREFIX ns: <http://mmberg.net/lexicon#> "+
			  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
			  "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			  "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"+
			  "PREFIX fn: <http://www.w3.org/2005/xpath-functions#>";
    private static Logger logger=Nadia.getLogger();
	
	public Lexicon(URL ontologyURL){
		this.load(ontologyURL);
	}
	
	private void load(URL ontologyURL){
			try {
			    model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
			    //String file="file:///"+System.getProperty("user.dir")+"/ont/lexicon.owl";
			    String file=ontologyURL.toString();
			    model.read(file,"RDF/XML");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private ArrayList<String> query(String querystring){
		
		ArrayList<String> query_results=new ArrayList<String>();
		
		querystring=querystring.replace(" :", " ns:");
		String queryString =  prefix+querystring;
		Query query = QueryFactory.create(queryString);
			   
	    QueryExecution qe = QueryExecutionFactory.create(query, model);
	    ResultSet results = qe.execSelect();
	        
	    while(results.hasNext()){
	    	QuerySolution sol = results.next();
	    	query_results.add(sol.getLiteral("?lex").getLexicalForm());
	    }
	    
	    //ResultSetFormatter.out(System.out, results, query);
	    qe.close();
	    
	    //dirty error handling
	    if(query_results.size()==0){ //throw new Exception("No lexical entry found");
	    	query_results.add("");
	    }
	    
	    return query_results;
	    
	}
		
	public void close(){
		model.close();
	}
	
	public ArrayList<String> getLex(String dimension, String spec, String referent, String pos, int formality){
		
		  String w_ref="wildcard_referent";
		  String w_spec="wildcard_specification";
		  String n = System.getProperty("line.separator"); //new line
		  
		  String querystring="SELECT ?lex "+n+
					"WHERE {"+n+
						"?w :has_lex ?lex. "+n+
						"?w :has_context ?con. "+n+
						"?con :has_dimension :"+dimension+". "+n+
						"{{?con :has_spec :"+spec+".} "+n+
						" UNION "+n+
						"{?con :has_spec :"+w_spec+".}} "+n+
						"?w :has_pos :"+pos+". "+n+
						"{{?w :has_referent :"+w_ref+".} "+n+
						" UNION "+n+
						"{?w :has_referent :"+referent+".}} "+n+
						"FILTER(lang(?lex)='en'). "+n+
						((formality>0)?
							//((formality>0)?"FILTER(?f="+formality+"). ":"")+
							"?w :has_formality ?f. "+n+
							//"LET(?r:=?f-"+formality+"). "+   //TopBraid
							"BIND(fn:abs(?f-"+formality+") AS ?r). "  //JENA
			  				:"")+n+
					"}"+n+
					"ORDER BY asc(?r)";
		  
		  logger.fine(querystring);
		  return query(querystring);
	}
}
